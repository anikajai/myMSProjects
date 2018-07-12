from __future__ import division, print_function

from typing import List, Callable

import numpy
import scipy

class KNN:

    def __init__(self, k: int, distance_function) -> float:
        self.k = k
        self.distance_function = distance_function

    def train(self, features: List[List[float]], labels: List[int]):
        self.train_data_features = features
        self.train_data_labels = labels

    def predict(self, features: List[List[float]]) -> List[int]:
        labels = numpy.zeros(len(features))
        for outerIndex, prediction_point in enumerate(features):
            dist_arr = []#numpy.zeros(len(self.train_data_features))
            for train_point in self.train_data_features:
                dist = self.distance_function(prediction_point, train_point)
                dist_arr.append(dist)
            #top_indices = numpy.argpartition(dist_arr, self.k)[:self.k]
            top_indices = sorted(range(len(dist_arr)), key=lambda index: dist_arr[index])[:self.k]
            label1 = 0
            label0 = 0
            for i in top_indices:
                if (self.train_data_labels[i] == 1):
                    label1 = label1 + 1
                else:
                    label0 =label0 + 1
            if label0 < label1:
                labels[outerIndex] = 1
            else:
                labels[outerIndex] = 0
        return labels
            


if __name__ == '__main__':
    print(numpy.__version__)
    print(scipy.__version__)
