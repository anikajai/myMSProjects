import numpy as np
from typing import List
from classifier import Classifier

class DecisionTree(Classifier):
	def __init__(self):
		self.clf_name = "DecisionTree"
		self.root_node = None

	def train(self, features: List[List[float]], labels: List[int]):
		# init.
		assert(len(features) > 0)
		self.feautre_dim = len(features[0])
		num_cls = np.max(labels)+1

		# build the tree
		self.root_node = TreeNode(features, labels, num_cls)
		if self.root_node.splittable:
			self.root_node.split()

		return
		
	def predict(self, features: List[List[float]]) -> List[int]:
		y_pred = []
		for feature in features:
			y_pred.append(self.root_node.predict(feature))
		return y_pred

	def print_tree(self, node=None, name='node 0', indent=''):
		if node is None:
			node = self.root_node
		print(name + '{')
		if node.splittable:
			print(indent + '  split by dim {:d}'.format(node.dim_split))
			for idx_child, child in enumerate(node.children):
				self.print_tree(node=child, name= '  '+name+'/'+str(idx_child), indent=indent+'  ')
		else:
			print(indent + '  cls', node.cls_max)
		print(indent+'}')


class TreeNode(object):
	def __init__(self, features: List[List[float]], labels: List[int], num_cls: int):
		self.features = features
		self.labels = labels
		self.children = []
		self.num_cls = num_cls

		count_max = 0
		for label in np.unique(labels):
			if self.labels.count(label) > count_max:
				count_max = labels.count(label)
				self.cls_max = label # majority of current node

		if len(np.unique(labels)) < 2:
			self.splittable = False
		else:
			self.splittable = True

		self.dim_split = None # the dim of feature to be splitted

		self.feature_uniq_split = None # the feature to be splitted


	def split(self):
		def conditional_entropy(branches: List[List[int]]) -> float:
			'''
			branches: C x B array, 
					  C is the number of classes,
					  B is the number of branches
					  it stores the number of 
			'''
			########################################################
			# TODO: compute the conditional entropy
			########################################################
			C = len(branches)
			B = len(branches[0])
			npCB = np.array(branches)
			den = np.sum(npCB, axis=0)
			probMat = npCB/den
			#probMat_copy = probMat.copy()
			probMat[probMat == 0] =1
			probMat = -1 * probMat * np.log2(probMat)
			entropy = np.sum(probMat, axis = 0)
			#print (np.sum(entropy*den)/np.sum(den))
			return np.sum(entropy*den)/np.sum(den)
			#return entropy

		
		unique_labels = np.unique(self.labels)
		unique_labels_count = len(unique_labels)
		label_dict = {}
		index = 0
		for cls in unique_labels:
			label_dict[cls] = index
			index = index+1
		np_feature = np.array(self.features)
		entropy_feature_collection = []
		for idx_dim in range(len(self.features[0])):
		############################################################
		# TODO: compare each split using conditional entropy
		#       find the best split.
		############################################################
			current_feature = np_feature.T[idx_dim]
			unique_features = np.unique(current_feature)
			unique_features_count = len(unique_features)
			feature_dict = {}
			indx = 0
			for f in unique_features:
				feature_dict[f] = indx
				indx = indx + 1

			entropy_matrix = np.zeros((unique_labels_count, unique_features_count))
			for ex_no, example  in enumerate(current_feature):
				cls = label_dict[self.labels[ex_no]]
				branch = feature_dict[example]
				entropy_matrix[cls][branch] = entropy_matrix[cls][branch] + 1
			branches = entropy_matrix.tolist()
			entropy_feature_collection.append(conditional_entropy(branches))


		if entropy_feature_collection == []:
			self.splittable = False
			self.dim_split = None
			self.feature_uniq_split = None
			return
		
		############################################################
		# TODO: split the node, add child nodes
		############################################################
		feature_to_split_index = np.argmin(entropy_feature_collection)
		self.dim_split = feature_to_split_index
		feature_to_split = np_feature.T[feature_to_split_index]
		unique_features = np.unique(feature_to_split)
		self.feature_uniq_split = unique_features.tolist()
		unique_features_count = len(unique_features)

		for feature_val in unique_features:
			my_label = []
			my_feature = []
			for ex_no, example in enumerate(self.features):
				if example[feature_to_split_index] == feature_val:
					example1 = example[:feature_to_split_index] + example[1+feature_to_split_index:]
					my_feature.append(example1)
					my_label.append(self.labels[ex_no])
			my_num_cls = len(np.unique(my_label))
			child = TreeNode(my_feature, my_label, my_num_cls)
			
			self.children.append(child)
		
		# split the child nodes
		for child in self.children:
			if child.splittable:
				child.split()

		return

	def predict(self, feature: List[int]) -> int:

		if self.splittable:
			
			idx_child = self.feature_uniq_split.index(feature[self.dim_split])
			feature = feature[:self.dim_split] + feature[self.dim_split+1:]
			return self.children[idx_child].predict(feature)
		else:
			return self.cls_max
