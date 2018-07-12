import numpy as np


class KMeans():

    '''
        Class KMeans:
        Attr:
            n_cluster - Number of cluster for kmeans clustering
            max_iter - maximum updates for kmeans clustering
            e - error tolerance
    '''

    def __init__(self, n_cluster, max_iter=100, e=0.0001):
        self.n_cluster = n_cluster
        self.max_iter = max_iter
        self.e = e

    def fit(self, x):
        '''
            Finds n_cluster in the data x
            params:
                x - N X D numpy array
            returns:
                A tuple
                (centroids or means, membership, number_of_updates )
            Note: Number of iterations is the number of time you update means other than initialization
        '''
        assert len(x.shape) == 2, "fit function takes 2-D numpy arrays as input"
        np.random.seed(42)
        N, D = x.shape
        meansIndices = np.random.choice(range(N), self.n_cluster, replace= False)
        membership = np.zeros(N)
        membershipScore = np.zeros(N)
        means = x[meansIndices]#[1,5,10,20]]
        
        dupli_x = np.repeat(x, self.n_cluster, axis=0).reshape(N,self.n_cluster, D)
        dupli_means = np.zeros((N,self.n_cluster, D))
        for i in range(N):
        	dupli_means[i] = means
        #temp_point = np.repeat(point, self.n_cluster, axis=0).reshape(D,self.n_cluster).T
        distances = np.square(np.linalg.norm(dupli_x - dupli_means, axis=2))
        membershipScore = np.amin(distances, axis=1)
        membership= np.argmin(distances, axis=1)

        J = np.sum(membershipScore)/N
        noOfUpdates = 0
        
        for iter in range(self.max_iter):
        	#clustersToUpdateLen = len(np.unique(means))
        	temp_means = np.zeros((self.n_cluster, D))
        	participant_counter= np.zeros(self.n_cluster)
        	
        	for ind, member in enumerate(membership):
        		member = int(member)
        		temp_means[member] += x[ind]
        		participant_counter[member] += 1
        	participant_counter = participant_counter.reshape(self.n_cluster,1)
        	means = temp_means/participant_counter
        	
        	#duplicate
        	#print ("membership calc starts")
        	dupli_x = np.repeat(x, self.n_cluster, axis=0).reshape(N,self.n_cluster, D)
	        dupli_means = np.zeros((N,self.n_cluster, D))
	        for i in range(N):
	        	dupli_means[i] = means
	        #temp_point = np.repeat(point, self.n_cluster, axis=0).reshape(D,self.n_cluster).T
	        distances = np.square(np.linalg.norm(dupli_x - dupli_means, axis=2))
	        membershipScore = np.amin(distances, axis=1)
	        membership= np.argmin(distances, axis=1)
        	

	        #J = Jnew
	        Jnew = np.sum(membershipScore)/N
	        error = abs(J-Jnew)
	        #print (error)
	        noOfUpdates = noOfUpdates + 1
	        if (error <= self.e):
	        	break
	        J = Jnew

        return means, membership, noOfUpdates

class KMeansClassifier():

    '''
        Class KMeansClassifier:
        Attr:
            n_cluster - Number of cluster for kmeans clustering
            max_iter - maximum updates for kmeans clustering
            e - error tolerance
    '''

    def __init__(self, n_cluster, max_iter=100, e=1e-6):
        self.n_cluster = n_cluster
        self.max_iter = max_iter
        self.e = e

    def fit(self, x, y):
        '''
            Train the classifier
            params:
                x - N X D size  numpy array
                y - N size numpy array of labels
            returns:
                None
            Stores following attributes:
                self.centroids : centroids obtained by kmeans clustering
                self.centroid_labels : labels of each centroid obtained by
                    majority voting
        '''

        assert len(x.shape) == 2, "x should be a 2-D numpy array"
        assert len(y.shape) == 1, "y should be a 1-D numpy array"
        assert y.shape[0] == x.shape[0], "y and x should have same rows"

        np.random.seed(42)
        N, D = x.shape
        k_means = KMeans(self.n_cluster, self.max_iter, self.e)
        centroids, membership, updatesCount = k_means.fit(x)
        centroid_labels = np.zeros(self.n_cluster)
        #y_labels = np.unique(y)
        #vote = np.zeros((self.n_cluster, len(y_labels)))
        for i in range(self.n_cluster):
        	voters_ind = np.argwhere(membership == i)
        	voters = y[voters_ind]
        	unique_counts, count_elems = np.unique(voters, return_counts = True)
        	max_voter_index = np.argmax(count_elems)
        	centroid_labels[i] = unique_counts[max_voter_index]
        

        self.centroid_labels = centroid_labels
        self.centroids = centroids

        assert self.centroid_labels.shape == (self.n_cluster,), 'centroid_labels should be a vector of shape {}'.format(
            self.n_cluster)

        assert self.centroids.shape == (self.n_cluster, D), 'centroid should be a numpy array of shape {} X {}'.format(
            self.n_cluster, D)

    def predict(self, x):
        '''
            Predict function

            params:
                x - N X D size  numpy array
            returns:
                predicted labels - numpy array of size (N,)
        '''

        assert len(x.shape) == 2, "x should be a 2-D numpy array"

        np.random.seed(42)
        
       
        N, D = x.shape

        dupli_x = np.repeat(x, self.n_cluster, axis=0).reshape(N,self.n_cluster, D)
        dupli_means = np.zeros((N,self.n_cluster, D))
        for i in range(N):
        	dupli_means[i] = self.centroids
        #temp_point = np.repeat(point, self.n_cluster, axis=0).reshape(D,self.n_cluster).T
        distances = np.square(np.linalg.norm(dupli_x - dupli_means, axis=2))
        membership= np.argmin(distances, axis=1)
        predicted_labels = self.centroid_labels[membership]
        return predicted_labels
        
