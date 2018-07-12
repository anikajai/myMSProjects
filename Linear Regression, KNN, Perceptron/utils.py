from typing import List

import numpy as np


def mean_squared_error(y_true: List[float], y_pred: List[float]) -> float:
    assert len(y_true) == len(y_pred)
    a = np.array(y_true)
    b= np.array(y_pred)
    diff = (np.linalg.norm(a-b)**2)/len(y_true)
    return diff


def f1_score(real_labels: List[int], predicted_labels: List[int]) -> float:
    """
    f1 score: https://en.wikipedia.org/wiki/F1_score
    """
    assert len(real_labels) == len(predicted_labels)
    
    tp, fp, fn, tn = 0.0,0.0,0.0,0.0
    for i in range(len(real_labels)):
        if (real_labels[i] == predicted_labels[i]):
            if (real_labels[i] == 1):
                tp = tp + 1
            else:
                tn = tn + 1
        else:
            if real_labels[i] == 1:
                fn = fn + 1
            else:
                fp = fp + 1
    if tp == 0:
        return 0
    precision = (tp*1.0)/(tp + fp)
    recall = (tp * 1.0)/(tp+fn)
    #print (tp,fp,fn)
    f1score = (2* precision * recall)/(precision + recall)
    return f1score


def polynomial_features(
        features: List[List[float]], k: int
) -> List[List[float]]:
    features_array = np.array(features)
    features_array_new = np.array(features)
    for i in range(2,k+1):
        features_array_new = np.hstack((features_array_new, features_array**i))
    features_array_new = np.round(features_array_new,6)
    return features_array_new.tolist()


def euclidean_distance(point1: List[float], point2: List[float]) -> float:
    a = np.array(point1)
    b = np.array(point2)
    dist = np.linalg.norm(a-b)
    return dist


def inner_product_distance(point1: List[float], point2: List[float]) -> float:
    dist = np.dot(np.array(point1), np.array(point2))
    return dist


def gaussian_kernel_distance(
        point1: List[float], point2: List[float]
) -> float:
    a = np.array(point1)
    b = np.array(point2)
    eucDist = np.linalg.norm(a-b)
    exponent = -1/2 * (np.square(eucDist))
    return -np.exp(exponent)


class NormalizationScaler:
    def __init__(self):
        pass

    def __call__(self, features: List[List[float]]) -> List[List[float]]:
        """
        normalize the feature vector for each sample . For example,
        if the input features = [[3, 4], [1, -1], [0, 0]],
        the output should be [[0.6, 0.8], [0.707107, -0.707107], [0, 0]]
        """
       
        features_array = np.array(features)#np.array([np.array(row) for row in features])
        for index, sample in enumerate(features_array):
            _norm = np.linalg.norm(sample)
            features_array[index] = sample/_norm
        return features_array.tolist()
        #raise NotImplementedError


class MinMaxScaler:
    """
    You should keep some states inside the object.
    You can assume that the parameter of the first __call__
        must be the training set.

    Note:
        1. you may assume the parameters are valid when __call__
            is being called the first time (you can find min and max).

    Example:
        train_features = [[0, 10], [2, 0]]
        test_features = [[20, 1]]

        scaler = MinMaxScale()
        train_features_scaled = scaler(train_features)
        # now train_features_scaled should be [[0, 1], [1, 0]]

        test_features_sacled = scaler(test_features)
        # now test_features_scaled should be [[10, 0.1]]

        new_scaler = MinMaxScale() # creating a new scaler
        _ = new_scaler([[1, 1], [0, 0]]) # new trainfeatures
        test_features_scaled = new_scaler(test_features)
        # now test_features_scaled should be [[20, 1]]

    """

    def __init__(self):
        self.called = False
        self.minMaxStore = np.zeros(2)
        pass

    def __call__(self, features: List[List[float]]) -> List[List[float]]:
        """
        normalize the feature vector for each sample . For example,
        if the input features = [[2, -1], [-1, 5], [0, 0]],
        the output should be [[1, 0], [0, 1], [0.333333, 0.16667]]
        """
        features_array = np.array(features)#np.array([np.array(row) for row in features])
        features_transposed = np.transpose(features_array)
        if self.called != True:
            self.minMaxStore = np.zeros((len(features[0]), 2))
        for index, feature in enumerate(features_transposed):
            if self.called == False:
                _max = np.amax(feature)
                _min = np.amin(feature)
                self.minMaxStore[index][0] = _min
                self.minMaxStore[index][1] = _max
            else:
                _min = self.minMaxStore[index][0]
                _max = self.minMaxStore[index][1]
            den = _max - _min
            if den !=0:
                features_transposed[index] = (feature - _min)/den
        if self.called == False:
            self.called = True
        features_array = np.transpose(features_transposed)
        return features_array.tolist()