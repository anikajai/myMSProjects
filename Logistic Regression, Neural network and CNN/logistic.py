from __future__ import division, print_function

import numpy as np
import scipy as sp

from matplotlib import pyplot as plt
from matplotlib import cm


#######################################################################
# DO NOT MODIFY THE CODE BELOW 
#######################################################################

def binary_train(X, y, w0=None, b0=None, step_size=0.5, max_iterations=1000):
    """
    Inputs:
    - X: training features, a N-by-D numpy array, where N is the 
    number of training points and D is the dimensionality of features
    - y: binary training labels, a N dimensional numpy array where 
    N is the number of training points, indicating the labels of 
    training data
    - step_size: step size (learning rate)

    Returns:
    - w: D-dimensional vector, a numpy array which is the weight 
    vector of logistic regression
    - b: scalar, which is the bias of logistic regression

    Find the optimal parameters w and b for inputs X and y.
    Use the average of the gradients for all training examples to
    update parameters.
    """
    N, D = X.shape
    assert len(np.unique(y)) == 2

    D1,D2 = X.shape
    #print (D1)
    #print (D2)
    w = np.zeros(D)
    if w0 is not None:
        w = w0
    
    b = 0
    if b0 is not None:
        b = b0
        

    x_tilda = np.insert(X, 0,1, axis = 1)
    w_tilda = np.append(b, w)
    
    for i in range(max_iterations):
        vector = np.matmul(w_tilda, np.transpose(x_tilda))#np.matmul(x_tilda, np.transpose(w_tilda))
        #vector = vector - max(vector)
        error_diff = sigmoid(vector) - y
        gradient = np.matmul(x_tilda.T, error_diff)
        w_tilda = w_tilda - step_size/N * gradient
    b = w_tilda[0]
    w = w_tilda[1:]
    assert w.shape == (D,)
    return w, b


def binary_predict(X, w, b):
    """
    Inputs:
    - X: testing features, a N-by-D numpy array, where N is the 
    number of training points and D is the dimensionality of features
    
    Returns:
    - preds: N dimensional vector of binary predictions: {0, 1}
    """
    N, D = X.shape
    preds = np.zeros(N) 

    x_tilda = np.insert(X, 0,1, axis = 1)
    w_tilda = np.append(b, w)
    
    vector = np.matmul(w_tilda, np.transpose(x_tilda))
    preds = sigmoid(vector)
    for index, pred in enumerate(preds):
        if pred < 0.5:
            preds[index] = 0
        else:
            preds[index] = 1
    """
    TODO: add your code here
    """      
    assert preds.shape == (N,) 
    return preds


def multinomial_train(X, y, C, 
                     w0=None, 
                     b0=None, 
                     step_size=0.5, 
                     max_iterations=1000):
    """
    Inputs:
    - X: training features, a N-by-D numpy array, where N is the 
    number of training points and D is the dimensionality of features
    - y: multiclass training labels, a N dimensional numpy array where
    N is the number of training points, indicating the labels of 
    training data
    - C: number of classes in the data
    - step_size: step size (learning rate)
    - max_iterations: maximum number for iterations to perform

    Returns:
    - w: C-by-D weight matrix of multinomial logistic regression, where 
    C is the number of classes and D is the dimensionality of features.
    - b: bias vector of length C, where C is the number of classes

    Implement a multinomial logistic regression for multiclass 
    classification. Keep in mind, that for this task you may need a 
    special (one-hot) representation of classification labels, where 
    each label y_i is represented as a row of zeros with a single 1 in
    the column, that corresponds to the class y_i belongs to. 
    """

    N, D = X.shape

    w = np.zeros((C, D))
    if w0 is not None:
        w = w0
    
    b = np.zeros(C)
    if b0 is not None:
        b = b0

    x_tilda = np.insert(X, 0,1, axis = 1)
    
    newLabels = np.zeros((N,C))
    for index,label in enumerate(y):
        newLabels[index][label] = 1
        
    w_tilda = np.insert(w, 0,b, axis = 1)
    for i in range(max_iterations):
        matrix = np.matmul(x_tilda, np.transpose(w_tilda)) #  (N,D+1)* (D+1,C) = (N, C)
        tempMax = np.array([np.amax(matrix, axis=1)]).T
        matrix = np.exp(matrix - tempMax)
        #print(matrix.shape)
        for index, instance in enumerate(matrix):
            matrix[index] = instance/np.sum(instance)
        matrix = matrix - newLabels
        gradients = np.matmul(np.transpose(matrix), x_tilda) #(C,D+1)
        w_tilda = w_tilda - step_size/N*gradients
        
    w_tilda = w_tilda.T
    b = w_tilda[0]
    w = w_tilda[1:].T
    #print (w.shape)
    
    assert w.shape == (C, D)
    assert b.shape == (C,)
    return w, b

def multinomial_predict(X, w, b):
    """
    Inputs:
    - X: testing features, a N-by-D numpy array, where N is the 
    number of training points and D is the dimensionality of features
    - w: weights of the trained multinomial classifier
    - b: bias terms of the trained multinomial classifier
    
    Returns:
    - preds: N dimensional vector of multiclass predictions.
    Outputted predictions should be from {0, C - 1}, where
    C is the number of classes

    Make predictions for multinomial classifier.
    """
    N, D = X.shape
    C = w.shape[0]
    preds = np.zeros(N) 
    x = np.insert(X, 0,1, axis = 1)
    w = np.insert(w, 0,b, axis = 1)
    voter = np.matmul(x,w.T)
    for index,node in enumerate(voter):
        preds[index] = np.argmax(node)
        
    assert preds.shape == (N,)
    return preds


def OVR_train(X, y, C, w0=None, b0=None, step_size=0.5, max_iterations=1000):
    """
    Inputs:
    - X: training features, a N-by-D numpy array, where N is the 
    number of training points and D is the dimensionality of features
    - y: multiclass training labels, a N dimensional numpy array, 
    indicating the labels of each training point
    - C: number of classes in the data
    - w0: initial value of weight matrix
    - b0: initial value of bias term
    - step_size: step size (learning rate)
    - max_iterations: maximum number of iterations for gradient descent

    Returns:
    - w: a C-by-D weight matrix of OVR logistic regression
    - b: bias vector of length C

    Implement multiclass classification using binary classifier and 
    one-versus-rest strategy. Recall, that the OVR classifier is 
    trained by training C different classifiers. 
    """
    N, D = X.shape
    
    w = np.zeros((C, D))
    if w0 is not None:
        w = w0
    
    b = np.zeros(C)
    if b0 is not None:
        b = b0
        
    x_tilda = np.insert(X, 0,1, axis = 1)
    
    newLabels = np.zeros((N,C))
    for index,label in enumerate(y):
        newLabels[index][label] = 1
        
    w_tilda = np.insert(w, 0,b, axis = 1)
    for i in range(max_iterations):
        matrix = np.matmul(x_tilda, np.transpose(w_tilda)) #  (N,D+1)* (D+1,C) = (N, C)
        #tempMax = np.array([np.amax(matrix, axis=1)]).T
        matrix = sigmoid(matrix)
        #print(matrix.shape
        matrix = matrix - newLabels
        gradients = np.matmul(np.transpose(matrix), x_tilda) #(C,D+1)
        w_tilda = w_tilda - step_size/N*gradients
        
    w_tilda = w_tilda.T
    b = w_tilda[0]
    w = w_tilda[1:].T
    
   
    """
    TODO: add your code here
    """
    assert w.shape == (C, D), 'wrong shape of weights matrix'
    assert b.shape == (C,), 'wrong shape of bias terms vector'
    return w, b


def OVR_predict(X, w, b):
    """
    Inputs:
    - X: testing features, a N-by-D numpy array, where N is the 
    number of training points and D is the dimensionality of features
    - w: weights of the trained OVR model
    - b: bias terms of the trained OVR model
    
    Returns:
    - preds: vector of class label predictions.
    Outputted predictions should be from {0, C - 1}, where
    C is the number of classes.

    Make predictions using OVR strategy and predictions from binary
    classifier. 
    """
    N, D = X.shape
    C = w.shape[0]
    preds = np.zeros(N)
    
    x = np.insert(X, 0,1, axis = 1)
    w = np.insert(w, 0,b, axis = 1)
    voter = np.matmul(x,w.T)
    for index,node in enumerate(voter):
        preds[index] = np.argmax(node)
    
    '''
    x_tilda = np.insert(X, 0,1, axis = 1)
    #w_tilda = np.append(b, w)
    voter = np.zeros((C, N))
    for index in range(C):
        currW = np.copy(w[index])
        currB = np.copy(b[index])
        w_tilda = np.append(currB, currW)
        vector = np.matmul(w_tilda, np.transpose(x_tilda))
        temp = sigmoid(vector)
        voter[index] = np.copy(temp)
    voter = voter.T # now its N*C
    for index,node in enumerate(voter):
        preds[index] = np.argmax(node)
    '''
   
    """
    TODO: add your code here
    """

    assert preds.shape == (N,)
    return preds


#######################################################################
# DO NOT MODIFY THE CODE BELOW 
#######################################################################

def sigmoid(x):
    return 1 / (1 + np.exp(-x))


def accuracy_score(true, preds):
    return np.sum(true == preds).astype(float) / len(true)

def run_binary():
    from data_loader import toy_data_binary, \
                            data_loader_mnist 

    print('Performing binary classification on synthetic data')
    X_train, X_test, y_train, y_test = toy_data_binary()
        
    w, b = binary_train(X_train, y_train)
    
    train_preds = binary_predict(X_train, w, b)
    preds = binary_predict(X_test, w, b)
    print('train acc: %f, test acc: %f' % 
            (accuracy_score(y_train, train_preds),
             accuracy_score(y_test, preds)))
    
    print('Performing binary classification on binarized MNIST')
    X_train, X_test, y_train, y_test = data_loader_mnist()

    binarized_y_train = [0 if yi < 5 else 1 for yi in y_train] 
    binarized_y_test = [0 if yi < 5 else 1 for yi in y_test] 
    
    w, b = binary_train(X_train, binarized_y_train)
    
    train_preds = binary_predict(X_train, w, b)
    preds = binary_predict(X_test, w, b)
    print('train acc: %f, test acc: %f' % 
            (accuracy_score(binarized_y_train, train_preds),
             accuracy_score(binarized_y_test, preds)))

def run_multiclass():
    from data_loader import toy_data_multiclass_3_classes_non_separable, \
                            toy_data_multiclass_5_classes, \
                            data_loader_mnist 
    
    datasets = [(toy_data_multiclass_3_classes_non_separable(), 
                        'Synthetic data', 3), 
                (toy_data_multiclass_5_classes(), 'Synthetic data', 5), 
                (data_loader_mnist(), 'MNIST', 10)]

    for data, name, num_classes in datasets:
        print('%s: %d class classification' % (name, num_classes))
        X_train, X_test, y_train, y_test = data
        
        print('One-versus-rest:')
        w, b = OVR_train(X_train, y_train, C=num_classes)
        train_preds = OVR_predict(X_train, w=w, b=b)
        preds = OVR_predict(X_test, w=w, b=b)
        print('train acc: %f, test acc: %f' % 
            (accuracy_score(y_train, train_preds),
             accuracy_score(y_test, preds)))
    
        print('Multinomial:')
        w, b = multinomial_train(X_train, y_train, C=num_classes)
        train_preds = multinomial_predict(X_train, w=w, b=b)
        preds = multinomial_predict(X_test, w=w, b=b)
        print('train acc: %f, test acc: %f' % 
            (accuracy_score(y_train, train_preds),
             accuracy_score(y_test, preds)))


if __name__ == '__main__':
    
    import argparse
    import sys

    parser = argparse.ArgumentParser()
    parser.add_argument("--type", )
    parser.add_argument("--output")
    args = parser.parse_args()

    if args.output:
            sys.stdout = open(args.output, 'w')

    if not args.type or args.type == 'binary':
        run_binary()

    if not args.type or args.type == 'multiclass':
        run_multiclass()
        