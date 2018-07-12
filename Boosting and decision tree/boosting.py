import numpy as np
from typing import List, Set

from classifier import Classifier
from decision_stump import DecisionStump
from abc import abstractmethod

class Boosting(Classifier):
  # Boosting from pre-defined classifiers
	def __init__(self, clfs: Set[Classifier], T=0):
		self.clfs = clfs
		self.num_clf = len(clfs)
		if T < 1:
			self.T = self.num_clf
		else:
			self.T = T
	
		self.clfs_picked = [] # list of classifiers h_t for t=0,...,T-1
		self.betas = []       # list of weights beta_t for t=0,...,T-1
		return

	@abstractmethod
	def train(self, features: List[List[float]], labels: List[int]):
		return

	def predict(self, features: List[List[float]]) -> List[int]:
		########################################################
		# TODO: implement "predict"
		########################################################

		sum = np.zeros(len(features))
		for index, clf in enumerate(self.clfs_picked):
			ht = clf.predict(features)
			htA = np.array(ht)
			beta = self.betas[index]
			sum = sum + htA * beta
		sum = np.sign(sum)
		return sum.tolist()

		

class AdaBoost(Boosting):
	def __init__(self, clfs: Set[Classifier], T=0):
		Boosting.__init__(self, clfs, T)
		self.clf_name = "AdaBoost"
		return
		
	def train(self, features: List[List[float]], labels: List[int]):

		N = len(labels)
		weights = np.ones(N)*1/N

		for t in range(self.T):

			hts = []
			clfs = list(self.clfs)
			ht = []#np.zeros(len(self.clfs))
			for index, clf in enumerate(clfs):
				curr = clf.predict(features)
				ht.append(curr)
				
				sum = 0
				for i, label in enumerate(labels):
					if  label != curr[i]:
						sum = sum + weights[i]
				hts.append(sum) #np.matmul(indicator, weights)

				
			minInd = np.argmin(hts)
			self.clfs_picked.append(clfs[minInd])

			e_t = hts[minInd]
			beta = 0.5 * np.log((1-e_t)/e_t)
			self.betas.append(beta)
				
			for j in range(N):
				if labels[j] == ht[minInd][j]:
					weights[j] = weights[j] * np.exp(-1 * beta)
				else:
					weights[j] = weights[j] * np.exp(beta)
				
			den = np.sum(weights)
			weights = weights/den

		
	def predict(self, features: List[List[float]]) -> List[int]:
		return Boosting.predict(self, features)


class LogitBoost(Boosting):
	def __init__(self, clfs: Set[Classifier], T=0):
		Boosting.__init__(self, clfs, T)
		self.clf_name = "LogitBoost"
		return

	def train(self, features: List[List[float]], labels: List[int]):
		N = len(labels)
		pies = np.ones(N)*1/2
		f = np.zeros(N)
		#z = np.zeros(N)
		y = np.array(labels)
		#weights = np.zeros(N)
		for t in range(self.T):
			weights = (pies * ( 1 - pies))
			z = (((y + 1)/2) - pies)/weights
			hts = np.zeros(len(self.clfs))
			clfs = list(self.clfs)
			#clfs = self.clfs.tolist()
			#htSign = np.zeros(len(self.clfs))
			htArray = []#= np.zeros(len(self.clfs))
			for index, clf in enumerate(clfs):
				ht = clf.predict(features)
				htArray.append(ht)
				temp = np.square(z - ht) #**2
				#htA = np.array(ht)
				hts[index] = np.sum(np.multiply(weights, temp))
			minIndex = np.argmin(hts)
			#print (hts[minIndex])
			min_clf = clfs[minIndex]
			self.clfs_picked.append(min_clf)
			error_min = htArray[minIndex]#hts[minIndex]
			f = f + np.array(error_min)/2
			#if t== 0:
			#	print (f)
			pies = 1/ (1+ np.exp(-2 * f))
			#beta = np.log2((1-error_min)/error_min)/2
			self.betas.append(0.5)

		
		
	def predict(self, features: List[List[float]]) -> List[int]:
		return Boosting.predict(self, features)
	