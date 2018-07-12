from __future__ import division, print_function

from typing import List

import numpy
import scipy


############################################################################
# DO NOT MODIFY ABOVE CODES
############################################################################
np = numpy
class LinearRegression:
    def __init__(self, nb_features: int):
        self.nb_features = nb_features

    def train(self, features: List[List[float]], values: List[float]):
        """TODO : Complete this function"""
        feature_array = np.array(features)
        x_tilda = np.insert(feature_array,0,1, axis=1)
        x_tilda_transposed = np.transpose(x_tilda)
        prod1 = np.matmul(x_tilda_transposed, x_tilda)
        _inv_prod1 = np.linalg.inv(prod1)
        prod2 = np.matmul(x_tilda_transposed,np.array(values))
        self.weights = np.matmul(_inv_prod1, prod2)
        #print (self.weights.shape)
        #print (np.transpose(self.weights).shape)

    def predict(self, features: List[List[float]]) -> List[float]:
        """TODO : Complete this function"""
        features_array = np.array([np.array(row) for row in features])
        features_array = np.insert(features_array,0,1, axis=1)
        outputs = np.matmul(features_array, self.weights) 
        return outputs.tolist()

    def get_weights(self) -> List[float]:
        """TODO : Complete this function"""

        """
        for a model y = 1 + 3 * x_0 - 2 * x_1,
        the return value should be [1, 3, -2].
        """
        return self.weights.tolist()
        #raise NotImplementedError


class LinearRegressionWithL2Loss:
    '''Use L2 loss for weight regularization'''
    def __init__(self, nb_features: int, alpha: float):
        self.alpha = alpha
        self.nb_features = nb_features

    def train(self, features: List[List[float]], values: List[float]):
        feature_array = np.array(features)
        x_tilda = np.insert(feature_array,0,1, axis=1)
        x_tilda_transposed = np.transpose(x_tilda)
        prod1 = np.matmul(x_tilda_transposed, x_tilda)
        I_matrix = np.identity(len(features[0])+1) # +1 for bias
        inner = prod1 + self.alpha* I_matrix
        _inv_inner = np.linalg.inv(inner)
        prod2 = np.matmul(x_tilda_transposed,np.array(values))
        self.weights = np.matmul(_inv_inner, prod2)
        self.norm_square = np.linalg.norm(self.weights)**2

    def predict(self, features: List[List[float]]) -> List[float]:
        """TODO : Complete this function"""
        features_array = np.array([np.array(row) for row in features])
        features_array = np.insert(features_array,0,1, axis=1)
        outputs = np.matmul(features_array, self.weights)
        return outputs.tolist()

    def get_weights(self) -> List[float]:
        """TODO : Complete this function"""
        """
        for a model y = 1 + 3 * x_0 - 2 * x_1,
        the return value should be [1, 3, -2].
        """
        return self.weights.tolist()
        #raise NotImplementedError


if __name__ == '__main__':
    print(numpy.__version__)
    print(scipy.__version__)
