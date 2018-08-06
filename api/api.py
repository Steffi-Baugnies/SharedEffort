#! /usr/bin/python
# -*- coding:utf-8 -*-

from flask import Flask
from flask import jsonify
from flask import request
from flask_mysqldb import MySQL
from flask_cors import CORS
from flask_bcrypt import Bcrypt
import db_credentials as config
import MySQLdb as my

app = Flask(__name__)
bcrypt = Bcrypt(app)
CORS(app)
mysql = MySQL()

app.config['MYSQL_USER'] = config.credentials['user']
app.config['MYSQL_PASSWORD'] = config.credentials['password']
app.config['MYSQL_DB'] = config.credentials['database']
app.config['MYSQL_HOST'] = config.credentials['host']

mysql.init_app(app)

@app.route('/register', methods=['POST'])
def register():
	jsonData = request.json
	mailAddress = jsonData["mailAddress"]
	password = bcrypt.generate_password_hash(jsonData["password"])
	familyName = jsonData["familyName"]
	rowCount = 0
	state = 0
	response = ''
	try:
		cursor = mysql.connection.cursor()
		cursor.callproc('proc_register', [mailAddress, password, familyName])
		for fields in cursor:
			rowCount = fields[0]
		if rowCount > 0:
			response = 'Le compte a bien été créé'
			state = 1
		else: 
			response = 'Erreur interne, veuillez réessayer plus tard'
		cursor.close()
		mysql.connection.commit()
	except my.Error as e:
		response = "L'adresse e-mail entrée est déjà utilisée"
		print(e)
	
	print(response)
	return jsonify({'registrationStatus': state, 'message' : response})
@app.route('/login', methods=['POST'])
def login():
	jsonData = request.json
	mailAddress = jsonData["mailAddress"]
	password = jsonData["password"]
	passwordHash = ''
	userId = -1
	familyId = -1
	cursor = mysql.connection.cursor()
	cursor.callproc('proc_getUserPasswordHash',[mailAddress])
	for fields in cursor:
		userId = fields[0]
		passwordHash = fields[1]
		familyId = fields[2]
	cursor.close()
	
	state = 0
	if passwordHash != '':
		if(bcrypt.check_password_hash(passwordHash, password)):
			response = 'Mot de passe correct'
			state = 1
		else:
			response = 'Mot de passe érroné'
	else:
		response = 'Nom d\'utilisateur incorrect'
		
	return jsonify({'connectionStatus': state, 'message' : response, 'familyId' : familyId})

@app.route('/addFamilyMember', methods=['POST'])
def addFamilyMember():
	jsonData = request.json
	fname = jsonData["fname"]
	lname = jsonData["lname"]
	birthdate = jsonData["birthdate"]
	pswd = jsonData["pswd"]
	isAdmin = jsonData["isAdmin"]
	famId = jsonData["famId"]
	cursor = mysql.connection.cursor()
	cursor.callproc('proc_addFamilyMember', [fname, lname, birthdate, pswd, isAdmin, famId])
	
	state = 0
	
	for fields in cursor: 
		state = fields[0]
	cursor.close()
	mysql.connection.commit()
	return jsonify({'state' : state})

@app.route('/createFamily', methods=['POST'])
def createFamily():

	jsonData = request.json
	familyName = jsonData["boardName"]
	persId = jsonData["persId"]
	cursor = mysql.connection.cursor()
	cursor.callproc('proc_createBoard', [familyName, persId])
	
	rowCount = 0
	state = 0
	for fields in cursor:
		rowCount = fields[0]
	
	cursor.close()
	mysql.connection.commit()
	if rowCount > 0:
		response = 'Le tableau a bien été créé'
		state = 1
	else:
		response = 'Erreur interne, veuillez réessayer plus tard'
	return jsonify({'boardCreationStatus': state, 'message' : response})


@app.route('/board/familyInfo', methods=["POST"])
def getFamilyInfo():
	jsonData = request.json
	familyId = jsonData["familyId"]
	cursor = mysql.connection.cursor()
	familyMembers = []
	cursor.callproc('proc_getUsersFromFamily', [familyId])
	for familyMember in cursor:
		familyMember = {
			'id' : familyMember[0],
			'lname' : familyMember[1], 
			'fname' : familyMember[2]
		}
		familyMembers.append(familyMember)
	cursor.close()
	
	return jsonify({'familyMembers': familyMembers})
	
@app.route('/board/personTasks', methods=["POST"])
def getTasksFromPerson():
	jsonData = request.json
	familyId = jsonData["familyId"]
	fName = jsonData["fName"]
	cursor = mysql.connection.cursor()
	personTasks = []
	cursor.callproc('proc_getTasksFromPerson', [familyId, fName])
	for task in cursor:
		idP = task[5]
		if idP is None :
			idP = -1
		task = {
			'id' : task[0], 
			'nomTache' : task[1],
			'nbPointsTache' : task[2],
			'nbPointsTransfert' : task[3],
			'estRecurrente' : task[4],
			'idPersonne' : idP,
			'dateTache' : task[6],
			'estFaite' : task[7]
		}
		personTasks.append(task)
	cursor.close()
	return jsonify({'personTasks' : personTasks})

@app.route('/board/allTasks', methods=["POST"])
def getTasksFromFamily():
	jsonData = request.json
	famId = jsonData["familyId"]
	cursor = mysql.connection.cursor()
	tasks = []
	cursor.callproc('proc_getTasksFromFamily', [famId])
	for task in cursor:
		idP = task[5]
		if idP is None :
			idP = -1
		task = {
			'id' : task[0], 
			'nomTache' : task[1],
			'nbPointsTache' : task[2],
			'nbPointsTransfert' : task[3],
			'estRecurrente' : task[4],
			'idPersonne' : idP,
			'dateTache' : task[6],
			'estFaite' : task[7]
		}
		tasks.append(task)
	cursor.close()
	return jsonify({'tasks' : tasks})

@app.route('/board/personEvents', methods=["POST"])
def getEventsFromPerson():
	jsonData = request.json
	familyId = jsonData["familyId"]
	fName = jsonData["fName"]
	cursor = mysql.connection.cursor()
	personEvents = []
	cursor.callproc('proc_getEventsFromPerson', [familyId, fName])
	for event in cursor:
		idP = event[3]
		if idP is None :
			idP = -1
		event = {
			'id' : event[0], 
			'nomEvenement' : event[1],
			'descriptionEvenement' : event[2],
			'idPersonne' : idP,
			'estRecurrent' : event[4],
			'dateEvenement' : event[5]
		}
		personEvents.append(event)
	cursor.close()
	return jsonify({'personEvents' : personEvents})

@app.route('/board/allEvents', methods=["POST"])
def getEventsFromFamily():
	jsonData = request.json
	famId = jsonData["familyId"]
	cursor = mysql.connection.cursor()
	events = []
	cursor.callproc('proc_getEventsFromFamily', [famId])
	for event in cursor:
		idP = event[3]
		if idP is None :
			idP = -1
		event = {
			'id' : event[0], 
			'nomEvenement' : event[1],
			'descriptionEvenement' : event[2],
			'idPersonne' : idP,
			'estRecurrent' : event[4],
			'dateEvenement' : event[5]
		}
		events.append(event)
	cursor.close()
	return jsonify({'events' : events})
	
@app.route('/board/addTask', methods=["POST"])
def addTask():
	jsonData = request.json
	connectedUser = jsonData["connectedUser"]
	pswd = jsonData["pswd"]
	taskName = jsonData["taskName"]
	points = int(jsonData["points"])
	pointsForTransfer = int(jsonData["pointsForTransfer"])
	taskDate = jsonData["taskDate"]
	persId = jsonData["persId"]
	famId = jsonData["famId"]
	if persId == -1 :
		persId = None
	recurrent = jsonData["recurrent"]
	cursor = mysql.connection.cursor()
	message = ""
	state = 0
	cursor.callproc('proc_addTask', [connectedUser, pswd, taskName, points, pointsForTransfer, taskDate, persId, recurrent, famId])
	
	for fields in cursor:
		message = fields[0].decode('utf-8')
		state = fields[1]
	cursor.close()
	mysql.connection.commit()
	print(message)
	return jsonify({'message' : message, 'state' : state})

@app.route('/board/addEvent', methods=["POST"])
def addEvent():
	jsonData = request.json
	eventName = jsonData["eventName"]
	eventDescription = jsonData["eventDescription"]
	eventDate = jsonData["eventDate"]
	famId = jsonData["famId"]
	persId = jsonData["persId"]
	if persId == -1 : 
		persId = None
	recu = jsonData["recu"]
	state = 0
	cursor = mysql.connection.cursor()
	cursor.callproc('proc_addEvent', [eventName, eventDescription, eventDate, famId, persId, recu])
	
	for fields in cursor:
		state = fields[0]
	if state == 1:
		message = "L'événement a bien été ajouté"
	else: 
		message = "Erreur interne, veuillez réessayer plus tard"
	cursor.close()
	mysql.connection.commit()
	return jsonify({'message' : message, 'state' : state})


if __name__ == '__main__':
	app.run(debug=True, threaded=True)
	app.run(host='0.0.0.0', port = 5000)
