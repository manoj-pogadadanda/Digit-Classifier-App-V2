
from flask import Flask
from flask import request,Response
import base64
import os

#import tensorflow as tf
#import numpy as np
#import matplotlib.pyplot as plt

TIMEOUT = 400
PORT = 8080

app = Flask(__name__)

'''
def draw(n):
    plt.imshow(n,cmap=plt.cm.binary)
    plt.axis('off')
    plt.show()
'''    

@app.route('/', methods=['POST', 'GET'])
def receive():
    if 'image' not in request.form:
        return 'Missing required image param' , 400
    elif 'filename' not in request.form:
        return 'Missing required filename param' , 400
    else:
        #filename = request.form['filename']
        #decode base64 string data
        decoded_data=base64.b64decode((request.form['image']))
        
        # Check if directory exists
        category = "Received"       
        if not os.path.isdir(category):
            os.makedirs(category)
        #write the decoded data back to original format in  file
        img_file = open(os.path.join(os.getcwd(), category, "latest.jpg") , 'wb')
        img_file.write(decoded_data)
        img_file.close()
        #Clear the predictions
        pred_file = open(os.path.join(os.getcwd(), category, "pred.txt") , 'w')
        pred_file.close()
        return 'Success', 200

@app.route('/get', methods=['POST', 'GET'])
def send():
    category = "Received"       
    #Read the decoded data back to original format in  file
    img_file = open(os.path.join(os.getcwd(), category, "latest.jpg") , 'rb')
    data = img_file.read()
    #encode base64 string data
    #encoded_data=base64.encodestring(data)
    encoded_data=base64.encodebytes(data)
    img_file.close()
    return encoded_data, 200

@app.route('/receivepred', methods=['POST', 'GET'])
def receivePred():
    category = "Received"       
    if not os.path.isdir(category):
        os.makedirs(category)
    #write the decoded data back to original format in  file
    pred_file = open(os.path.join(os.getcwd(), category, "pred.txt") , 'a')
    predictions = request.form['predictions']
    print(predictions)
    pred_file.write(predictions)
    pred_file.close()
    return 'Success', 200

@app.route('/sendpred', methods=['POST', 'GET'])
def sendPred():
    category = "Received"       
    #Read the predictions form the file
    pred_file = open(os.path.join(os.getcwd(), category, "pred.txt") , 'r')
    predictions = pred_file.read()
    pred_file.close()
    return predictions, 200


# Start the web server
if __name__ == "__main__":
    app.secret_key = ".."
#    app.run(host = 'localhost',port = PORT, debug=False)
    app.run(host = '0.0.0.0',port = PORT, debug=False)
    