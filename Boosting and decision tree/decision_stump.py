import numpy as np
from typing import List
from classifier import Classifier

class DecisionStump(Classifier):
	def __init__(self, s:int, b:float, d:int):
		self.clf_name = "Decision_stump"
		self.s = s
		self.b = b
		self.d = d

	def train(self, features: List[List[float]], labels: List[int]):
		pass
		
	def predict(self, features: List[List[float]]) -> List[int]:

		output = []
		for instance in features:
			dim = instance[self.d]
		#for dim in features:
			if (dim > self.b):
				output.append(self.s)
			else:
				output.append(-1*self.s)
		return output
		