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
		familyId = fields[2]
		passwordHash = fields[1]
	cursor.close()
	
	if(familyId is None):
		familyId = -1
	
	state = 0
	if passwordHash != '':
		if(bcrypt.check_password_hash(passwordHash, password)):
			response = 'Mot de passe correct'
			state = 1
		else:
			response = 'Mot de passe érroné'
	else:
		response = 'Nom d\'utilisateur incorrect'
		
	return jsonify({'connectionStatus': state, 'message' : response, 'userId' : userId, 'familyId' : familyId})
	
@app.route('/register', methods=['POST'])
def register():

	jsonData = request.json
	mailAddress = jsonData["mailAddress"]
	password = bcrypt.generate_password_hash(jsonData["password"])
	lName = jsonData["lName"]
	fName = jsonData["fName"]
	birthdate = jsonData["birthdate"]
	rowCount = 0
	state = 0
	response = ''
	try:
		cursor = mysql.connection.cursor()
		cursor.callproc('proc_createUser', [mailAddress, password, lName, fName, birthdate])
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

@app.route('/createBoard', methods=['POST'])
def createBoard():

	jsonData = request.json
	boardName = jsonData["boardName"]
	adminPswd = jsonData["adminPswd"]
	persId = jsonData["persId"]
	cursor = mysql.connection.cursor()
	cursor.callproc('proc_createBoard', [boardName, adminPswd, persId])
	
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
		task = {
			'id' : task[0], 
			'nomTache' : task[1],
			'nbPointsTache' : task[2],
			'nbPointsTransfert' : task[3],
			'estRecurente' : task[4],
			'idPersonne' : task[5],
			'dateTache' : task[6],
			'estFaite' : task[7]
		}
		personTasks.append(task)
	cursor.close()
	
	return jsonify({'personTasks' : personTasks})
	
@app.route('/board/addTask', methods=["POST"])
def addTask():
	jsonData = request.json
	pswd = jsonData["pswd"]
	taskName = jsonData["taskName"]
	points = int(jsonData["points"])
	pointsForTransfer = int(jsonData["pointsForTransfer"])
	taskDate = jsonData["taskDate"]
	persId = jsonData["persId"]
	recurrent = jsonData["recurrent"]
	cursor = mysql.connection.cursor()
	message = ""
	cursor.callproc('proc_addTask', [pswd, taskName, points, pointsForTransfer, taskDate, persId, recurrent])
	
	for fields in cursor:
		message = fields[0].decode('utf-8')
	cursor.close()
	mysql.connection.commit()
	print(message)
	return jsonify({'message' : message})

if __name__ == '__main__':
	app.run(debug=True, threaded=True)
	app.run(host='0.0.0.0', port = 5000)
