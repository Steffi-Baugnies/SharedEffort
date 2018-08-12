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
		
	return jsonify({'connectionStatus': state, 'message' : response, 'familyId' : familyId, 'userId' : userId})

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


@app.route('/board/familyMembersInfo', methods=["POST"])
def getFamilyMembersInfo():
	jsonData = request.json
	familyId = jsonData["familyId"]
	cursor = mysql.connection.cursor()
	familyMembers = []
	cursor.callproc('proc_getFamilyMembersInfo', [familyId])
	for familyMember in cursor:
		familyMember = {
			'id' : familyMember[0],
			'fname' : familyMember[1], 
			'lname' : familyMember[2], 
			'birthdate' : familyMember[3], 
			'points' : familyMember[4], 
			'mdp' : familyMember[5], 
			'isAdmin' : familyMember[6]
		}
		familyMembers.append(familyMember)
	cursor.close()
	
	return jsonify({'familyMembers': familyMembers})
	
@app.route('/board/getTasksAndEvents', methods=["POST"])
def getTasksAndEventsFromFamily():
	jsonData = request.json
	familyId = jsonData["familyId"]
	
	cursor = mysql.connection.cursor()
	tasks = []
	cursor.callproc('proc_getAllTasksFromFamily', [familyId])
	for task in cursor: 
		persId = task[5]
		if persId is None: 
			persId = -1
		task = {
			'taskId' : task[0], 
			'taskName' : task[1], 
			'points' : task[2],
			'tPoints' : task[3],
			'isRecu' : task[4], 
			'persId' : persId,
			'taskDate' : task[6], 
			'status' : task[7]
		}
		tasks.append(task)
	cursor.close()
	
	cursor = mysql.connection.cursor()
	events = []
	cursor.callproc('proc_getAllEventsFromFamily', [familyId])
	for event in cursor:
		persId = event[3]
		if persId is None:
			persId = -1
		event = {
			'eventId' : event[0], 
			'eventName' : event[1], 
			'eventDesc' : event[2],
			'persId' : persId,
			'isRecu' : event[4], 
			'eventDate' : event[5] 
		}
		events.append(event)
	cursor.close()
	
	return jsonify({'tasks' : tasks, 'events' : events})
	
@app.route('/board/addTask', methods=["POST"])
def addTask():
	jsonData = request.json
	taskName = jsonData["taskName"]
	points = jsonData["points"]
	tpoints = jsonData["tpoints"]
	isRecu = jsonData["isRecu"]
	persId = jsonData["persId"]
	famId = jsonData["famId"]
	taskDate = jsonData["taskDate"]
	
	if persId == -1 : 
		persId = None
	
	cursor = mysql.connection.cursor()
	cursor.callproc('proc_addTask', [taskName, points, tpoints, isRecu, persId, famId, taskDate])
	
	rowCount = 0
	message = ""
	
	for fields in cursor:
		rowCount = fields[0]
	
	cursor.close()
	mysql.connection.commit()
	
	if rowCount > 0:
		message = "La tâche a bien été ajoutée"
	else:
		message = "Erreur interne, veuillez réessayer plus tard"

	return jsonify({'status' : rowCount, 'message' : message})

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

@app.route('/board/claimTask', methods=["POST"])
def claimTask(): 
	jsonData = request.json
	persId = jsonData["persId"]
	taskId = jsonData["taskId"]
	cursor = mysql.connection.cursor()
	
	state = 0
	cursor.callproc('proc_claimTask', [persId, taskId])
	
	for fields in cursor:
		state = fields[0]
	if state == 1:
		message = "La tâche vous est maintenant attribuée"
	else: 
		message == "Erreur interne, veuillez réessayer plus tard"
	cursor.close()
	mysql.connection.commit()
	return jsonify({'message' : message, 'state' : state})

@app.route('/board/transferTask', methods=["POST"])
def transferTask():
	jsonData = request.json
	taskId = jsonData["taskId"]
	transferorId = jsonData["transferorId"]
	transfereeId = jsonData["transfereeId"]
	substractPoints = jsonData["substractPoints"]
	cursor = mysql.connection.cursor()
	cursor.callproc('proc_transferTask', [taskId, transferorId, transfereeId, substractPoints])
	
	state = 0
	
	for fields in cursor:
		state = fields[0]
	if state == 1:
		message = "La tâche a bien été transférée"
	else: 
		message = "Erreur interne veuillez réessayer plus tard"
	
	cursor.close()
	mysql.connection.commit()
	return jsonify({'message' : message, 'state' : state})

@app.route('/board/deleteTask', methods=["POST"])
def deleteTask():
	jsonData = request.json
	taskId = jsonData["taskId"]
	cursor = mysql.connection.cursor()
	cursor.callproc("proc_deleteTask", [taskId])
	
	state = 0
	
	for fields in cursor: 
		state = fields[0]
	if state == 1: 
		message = "La tâche a bien été supprimée"
	else:
		message = "Erreur interne, veuillez réessayer plus tard"
	
	cursor.close()
	mysql.connection.commit()
	return jsonify({'message' : message, 'state' : state})

@app.route('/board/requestValidation', methods=["POST"])
def requestValidation():
	jsonData = request.json
	taskId = jsonData["taskId"]
	cursor = mysql.connection.cursor()
	cursor.callproc('proc_requestValidation', [taskId])
	state = 0
	
	for fields in cursor:
		state = fields[0]
	if state == 1: 
		message = "La demande de validation de la tâche a bien été envoyée"
	else : 
		message = "Erreur interne, veuillez réessayer plus tard"
	
	cursor.close()
	mysql.connection.commit()
	return jsonify({'message' : message, 'state' : state})
	
@app.route('/board/validateTask', methods=["POST"])
def validateTask():
	jsonData = request.json
	taskId = jsonData["taskId"]
	points = jsonData["points"]
	persId = jsonData["persId"]
	print(taskId)
	print(points)
	print(persId)
	cursor = mysql.connection.cursor()
	cursor.callproc('proc_validateTask', [taskId, points, persId])
	
	state = 0
	
	for fields in cursor:
		state = fields[0]
	if state == 1 :
		message = "La tâche a bien été validée"
	else :
		message = "Erreur interne, veuillez réessayer plus tard"
	print(state)
	cursor.close()
	mysql.connection.commit()
	return jsonify({'message' : message, 'state' : state})

	
if __name__ == '__main__':
	app.run(debug=True, threaded=True)
	app.run(host='0.0.0.0', port = 5000)
