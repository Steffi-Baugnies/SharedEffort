#! /usr/bin/python
# -*- coding:utf-8 -*-

from flask import Flask
from flask import jsonify
from flask import request
from flask_mysqldb import MySQL
from flask_cors import CORS
from flask_bcrypt import Bcrypt
import db_credentials as config

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
	cursor = mysql.connection.cursor()
	cursor.callproc('proc_getUserPasswordHash',[mailAddress])
	for fields in cursor:
		userId = fields[0]
		passwordHash = fields[1]
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
		
	return jsonify({'connectionStatus': state, 'message' : response, 'userId' : userId})
	
@app.route('/register', methods=['POST'])
def register():

	jsonData = request.json
	mailAddress = jsonData["mailAddress"]
	password = bcrypt.generate_password_hash(jsonData["password"])
	lName = jsonData["lName"]
	fName = jsonData["fName"]
	birthdate = jsonData["birthdate"]
	cursor = mysql.connection.cursor()
	cursor.callproc('proc_createUser', [mailAddress, password, lName, fName, birthdate])
	
	rowCount = 0
	state = 0
	for fields in cursor:
		rowCount = fields[0]
	cursor.close()
	mysql.connection.commit()
	if rowCount > 0:
		response = 'Le compte a bien été créé'
		state = 1
	else: 
		response = 'Erreur interne, veuillez réessayer plus tard'
	return jsonify({'registrationStatus': state, 'message' : response})

@app.route('/createBoard', methods=['POST'])
def createBoard():

	jsonData = request.json
	boardName = jsonData["boardName"]
	adminPswd = jsonData["adminPswd"]
	cursor = mysql.connection.cursor();
	cursor.callproc('proc_createBoard', [boardName, adminPswd])
	
	cursor.close()
	mysql.connection.commit()
	
	rowCount = 0
	state = 0
	for fields in cursor:
		rowCount = fields[0]
	if rowCount > 0:
		response = 'Le tableau a bien été créé'
		state = 1
	else:
		response = 'Erreur interne, veuillez réessayer plus tard'
	return jsonify({'boardCreationStatus': state, 'message' : response})

if __name__ == '__main__':
	app.run(debug=True, threaded=True)
	app.run(host='0.0.0.0', port = 5000)
