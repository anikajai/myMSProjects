import numpy as np
from kmeans import KMeans


class GMM():
    '''
        Fits a Gausian Mixture model to the data.

        attrs:
            n_cluster : Number of mixtures
            e : error tolerance
            max_iter : maximum number of updates
            init : initialization of means and variance
                Can be 'random' or 'kmeans'
            means : means of gaussian mixtures
            variances : variance of gaussian mixtures
            pi_k : mixture probabilities of different component
    '''

    def __init__(self, n_cluster, init='k_means', max_iter=100, e=0.0001):
        self.n_cluster = n_cluster
        self.e = e
        self.max_iter = max_iter
        self.init = init
        self.means = None
        self.variances = None
        self.pi_k = None

    def fit(self, x):
        '''
            Fits a GMM to x.

            x: is a NXD size numpy array
            updates:
                self.means
                self.variances
                self.pi_k
        '''
        assert len(x.shape) == 2, 'x can only be 2 dimensional'

        np.random.seed(42)
        N, D = x.shape

        if (self.init == 'k_means'):
            k_means = KMeans(self.n_cluster, self.max_iter, self.e)
            self.means, membership, updatesCount = k_means.fit(x)
            unique_counts, count_elems = np.unique(membership, return_counts = True)
            self.N_k = np.zeros(self.n_cluster)
            for i in range(len(unique_counts)):
                self.N_k[unique_counts[i]] = count_elems[i]
            self.pi_k = np.zeros(self.n_cluster)
            self.pi_k = self.N_k/N

            self.variances = np.zeros((self.n_cluster, D,D))
            self.gamma = np.zeros((N,self.n_cluster))
            for i in range(N):
                self.gamma[i][membership[i]] = 1
            for k in range(self.n_cluster):
                #diff = np.matmul(self.gamma.T[k], ) # N*D
                diff = (x - self.means[k])
                diff_filtered = np.transpose(self.gamma)[k].reshape(N,1) *  diff # N * D
                prod = np.matmul(diff_filtered.T, diff)  #D*D
                self.variances[k] = prod/self.N_k[k]
            
            

           
            

        elif (self.init == 'random'):
            

            self.means = np.random.random_sample((self.n_cluster,D))
            
            self.pi_k = np.ones(self.n_cluster,) * 1/self.n_cluster

            self.variances = np.array([np.identity(D)]* self.n_cluster)

        else:
            raise Exception('Invalid initialization provided')

        
        self.detK = np.zeros(self.n_cluster)
        self.invK = np.zeros((self.n_cluster, D, D))
        self.n_cross_k_mat = np.zeros((self.n_cluster, N))
        l_old = self.compute_log_likelihood(x)
        #print (l_old)
        
        
        update_counter = 0
        for iter in range(self.max_iter):
            # E step
            #Note: self.n_cross_k_mat is a computed product of p(z=k)*N(x|z=k) in the likelihood method
            
            temp = np.sum(self.n_cross_k_mat, axis=0) #N*1
            #print (temp.shape)
            den = temp.reshape(N,1) #K*1
            self.gamma = (self.n_cross_k_mat.T/den) #N*k
            #print (self.gamma.shape)

            #M step
            self.N_k = np.sum(self.gamma, axis=0)
            self.pi_k = self.N_k/N

            self.means=np.matmul(self.gamma.T,x)/np.sum(self.gamma,axis=0).reshape(-1,1)
            for k in range(self.n_cluster):
                diff = (x - self.means[k])
                diff_filtered = np.transpose(self.gamma)[k].reshape(N,1) *  diff # N * D
                prod = np.matmul(diff_filtered.T, diff)  #D*D
                self.variances[k] = prod/self.N_k[k]
            l_new = self.compute_log_likelihood(x)
            update_counter += 1
            if (abs(l_new - l_old) <= self.e):
                break
            l_old = l_new
        
        return update_counter
        # DONOT MODIFY CODE BELOW THIS LINE

    def sample(self, N):
        '''
        sample from the GMM model

        N is a positive integer
        return : NXD array of samples

        '''
        assert type(N) == int and N > 0, 'N should be a positive integer'
        np.random.seed(42)
        
        cluster_choice = np.random.randint(len(self.pi_k), size=N)
        out = []#= np.zero((N,D))
        for i in cluster_choice:
            mean = self.means[i]
            var = self.variances[i]
            out.append(np.random.multivariate_normal(mean, var))
        out2 = np.array(out)
        #print (out2.shape)
        return out2
        

    def compute_log_likelihood(self, x):
        '''
            Return log-likelihood for the data

            x is a NXD matrix
            return : a float number which is the log-likelihood of data
        '''
        assert len(x.shape) == 2,  'x can only be 2 dimensional'
        
        

        N, D = x.shape
        for k in range(self.n_cluster):
            
            #while (not is_invertible or det == 0):
            while (np.linalg.matrix_rank(self.variances[k]) < D):
                self.variances[k] += (10**-3)  * np.identity(D)
                #a = self.variances[k]
                #is_invertible = (a.shape[0] == a.shape[1] and np.linalg.matrix_rank(a) == a.shape[0])
            det = np.linalg.det(self.variances[k])    
            self.detK[k] = det
            self.invK[k] = np.linalg.inv(self.variances[k])

            #calculating constant term for p(X)
            const_term = self.pi_k[k] * 1/np.sqrt(((2*np.pi)**D)* self.detK[k]) # 1*1
            diff = (x - self.means[k])
            diff_inv = np.matmul(diff, self.invK[k])
            net_prod = np.sum(diff_inv * diff , axis=1) #N*1
            exp_term = np.exp(-0.5 * net_prod) #N*1
            value = const_term * exp_term #N*1
            self.n_cross_k_mat[k] = value.T

        likelihood = np.sum(np.log(np.sum(self.n_cross_k_mat, axis=0)))
        
        #print (likelihood)
        return float(likelihood)

        