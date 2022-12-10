# -*- coding: utf-8 -*-
"""
Created on Sun Nov 29 14:07:35 2022

@author: Sumant Kulkarni
"""

import numpy as np
import matplotlib.pyplot as plt
import tensorflow as tf
import os

def draw(n):
    plt.imshow(n,cmap=plt.cm.binary)
    plt.show()

#Load the MNIST dataset
mnist = tf.keras.datasets.mnist
#Split the dataset to train and validation
(x_train,y_train) , (x_test,y_test) = mnist.load_data()

#Preprocess the dataset
x_train = tf.keras.utils.normalize(x_train,axis=1)
x_test = tf.keras.utils.normalize(x_test,axis=1)


average_pixel = x_train[x_train != 0].mean()
x_train[x_train != 0] = average_pixel
x_test[x_test != 0] = average_pixel


def load_image(img_path):
    img_file = open(img_path, 'rb')
    decoded_data = img_file.read()
    img_data = tf.image.decode_jpeg(decoded_data, channels=3)
    #Convert to grey scale
    img_data_grey = tf.image.rgb_to_grayscale(img_data)
    #Resize to required scale
    img_data_resized = tf.image.resize(img_data_grey, [28, 28])
    #Create the ndarray
    img_ndarray = np.array(img_data_resized).reshape(1, 28,28)
    #Normalise the image data
    img_ndarray_norm = tf.keras.utils.normalize(img_ndarray,axis=1)
    img_ndarray_norm_reverse = img_ndarray_norm.max() - img_ndarray_norm
    img_ndarray_norm_reverse[img_ndarray_norm_reverse < np.quantile(img_ndarray_norm_reverse, 0.75)] = 0
    pixel_average = img_ndarray_norm_reverse[img_ndarray_norm_reverse != 0].mean()
    img_ndarray_norm_reverse[img_ndarray_norm_reverse < pixel_average]  = 0
    img_ndarray_norm_reverse[img_ndarray_norm_reverse != 0] = 0.28163262805318545
#    draw(img_ndarray_norm_reverse[0])
#    print(img_ndarray_norm_reverse)
    return img_ndarray_norm_reverse

x_train_mnst = x_train
y_train_mnst = y_train
x_train = x_train[range(0)]
y_train = y_train[range(0)]



x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\0.png"),
          axis=0)
y_train = np.append(y_train,0)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\0_b.png"),
          axis=0)
y_train = np.append(y_train,0)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\1.png"),
          axis=0)
y_train = np.append(y_train,1)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\1_b.png"),
          axis=0)
y_train = np.append(y_train,1)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\2.png"),
          axis=0)
y_train = np.append(y_train,2)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\2_b.png"),
          axis=0)
y_train = np.append(y_train,2)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\2_c.png"),
          axis=0)
y_train = np.append(y_train,2)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\3.png"),
          axis=0)
y_train = np.append(y_train,3)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\3_b.png"),
          axis=0)
y_train = np.append(y_train,3)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\3_c.png"),
          axis=0)
y_train = np.append(y_train,3)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\4.png"),
          axis=0)
y_train = np.append(y_train,4)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\4_b.png"),
          axis=0)
y_train = np.append(y_train,4)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\5.png"),
          axis=0)
y_train = np.append(y_train,5)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\5_b.png"),
          axis=0)
y_train = np.append(y_train,5)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\6.png"),
          axis=0)
y_train = np.append(y_train,6)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\6_b.png"),
          axis=0)
y_train = np.append(y_train,6)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\6_c.png"),
          axis=0)
y_train = np.append(y_train,6)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\6_d.png"),
          axis=0)
y_train = np.append(y_train,6)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\6_e.png"),
          axis=0)
y_train = np.append(y_train,6)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\6_f.png"),
          axis=0)
y_train = np.append(y_train,6)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\6_g.png"),
          axis=0)
y_train = np.append(y_train,6)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\7.png"),
          axis=0)
y_train = np.append(y_train,7)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\7_b.png"),
          axis=0)
y_train = np.append(y_train,7)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\8.png"),
          axis=0)
y_train = np.append(y_train,8)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\8_b.png"),
          axis=0)
y_train = np.append(y_train,8)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\8_c.png"),
          axis=0)
y_train = np.append(y_train,8)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\8_d.png"),
          axis=0)
y_train = np.append(y_train,8)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\8_e.png"),
          axis=0)
y_train = np.append(y_train,8)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\8_f.png"),
          axis=0)
y_train = np.append(y_train,8)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\8_g.png"),
          axis=0)
y_train = np.append(y_train,8)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\9.png"),
          axis=0)
y_train = np.append(y_train,9)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\9_b.png"),
          axis=0)
y_train = np.append(y_train,9)

x_train = np.append(x_train, 
          load_image("D:\ASU\CEN\Mobile_Computing_CSE535\Assignment3\Dataset\\9_c.png"),
          axis=0)
y_train = np.append(y_train,9)


for _ in range(11):
    x_train = np.append(x_train, x_train, axis=0)
    y_train = np.append(y_train, y_train, axis=0)

x_train = np.append(x_train, x_train_mnst, axis=0)
y_train = np.append(y_train, y_train_mnst, axis=0)

x_train_Q1 = x_train[:,:14,:14]
x_train_Q2 = x_train[:,:14,14:]
x_train_Q3 = x_train[:,14:,:14]
x_train_Q4 = x_train[:,14:,14:]

x_test_Q1 = x_test[:,:14,:14]
x_test_Q2 = x_test[:,:14,14:]
x_test_Q3 = x_test[:,14:,:14]
x_test_Q4 = x_test[:,14:,14:]

#Display images visually
draw(x_train[2])
draw(x_train_Q1[2])
draw(x_train_Q2[2])
draw(x_train_Q3[2])
draw(x_train_Q4[2])
print(y_train[2])

draw(x_train[55555])
draw(x_train_Q1[55555])
draw(x_train_Q2[55555])
draw(x_train_Q3[55555])
draw(x_train_Q4[55555])
print(y_train[55555])

draw(x_train[111111])
draw(x_train_Q1[111111])
draw(x_train_Q2[111111])
draw(x_train_Q3[111111])
draw(x_train_Q4[111111])
print(y_train[111111])


#Build the Deep Learning Model from scratch
model = tf.keras.models.Sequential()
model.add(tf.keras.layers.Flatten(input_shape=(14, 14)))
model.add(tf.keras.layers.Dense(128,activation=tf.nn.relu))
model.add(tf.keras.layers.Dense(128,activation=tf.nn.relu))
model.add(tf.keras.layers.Dense(10,activation=tf.nn.softmax))
model.compile(optimizer='adam',
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy']
              )

def build_model(x_train_data, x_test_data, filename):
    #Train the model using the dataset
    #Check the weights of the Model and perform fine tuning if required
    model.fit(x_train_data,y_train,epochs=10)
    #Validate the trained model
    print("\nTraining Evaluation:")
    val_loss,val_acc = model.evaluate(x_train_data,y_train)
    #Store the trained model
    model.save(filename + '.h5')
    #Load the trained model for testing
    loaded_model = tf.keras.models.load_model(filename + '.h5')
    print("\nPrediction using saved model:")
    predictions=loaded_model.predict([x_test_data])
    print('label -> ',y_test[66])
    print('prediction -> ',np.argmax(predictions[66]))
    draw(x_test_data[66])
    #Check the accuracy of the model
    print("\nTesting Evaluation: ")
    val_loss,val_acc = loaded_model.evaluate(x_test_data,y_test)
    #Load coverter of the trained model
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()
    with open(filename + '.tflite', 'wb') as f:
        f.write(tflite_model)
    
    
build_model(x_train_Q1, x_test_Q1, 'tf_classifier_Q1')
build_model(x_train_Q2, x_test_Q2, 'tf_classifier_Q2')
build_model(x_train_Q3, x_test_Q3, 'tf_classifier_Q3')
build_model(x_train_Q4, x_test_Q4, 'tf_classifier_Q4')
