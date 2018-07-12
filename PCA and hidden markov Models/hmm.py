from __future__ import print_function
import json
import numpy as np
import sys

def forward(pi, A, B, O):
    """
    Forward algorithm

    Inputs:
    - pi: A numpy array of initial probailities. pi[i] = P(Z_1 = s_i)
    - A: A numpy array of transition probailities. A[i, j] = P(Z_t = s_j|Z_t-1 = s_i)
    - B: A numpy array of observation probabilities. B[i, k] = P(X_t = o_k| Z_t = s_i)
    - O: A list of observation sequence (in terms of index, not the actual symbol)

    Returns:
    - alpha: A numpy array alpha[j, t] = P(Z_t = s_j, x_1:x_t)
    """
    S = len(pi)
    N = len(O)
    alpha = np.zeros([S, N])
    ###################################################
    # Q3.1 Edit here
    ###################################################
    start = True
    index = 0
    for val in O:
  	    for state in range(S):
  		    if start == True:
  			    alpha[state, index] = pi[state] * B[state,val]
  		    else:
  			    _sum = 0;
  			    for prev_state in range(S):
  				    _sum = _sum + A[prev_state, state]*alpha[prev_state, index-1]
  			    alpha[state,index] = B[state, val] * _sum

  	    if start == True:
  		    start = False
  	    index = index + 1
    #print (alpha)
    return alpha


def backward(pi, A, B, O):
    """
    Backward algorithm

    Inputs:
    - pi: A numpy array of initial probailities. pi[i] = P(Z_1 = s_i)
    - A: A numpy array of transition probailities. A[i, j] = P(Z_t = s_j|Z_t-1 = s_i)
    - B: A numpy array of observation probabilities. B[i, k] = P(X_t = o_k| Z_t = s_i)
    - O: A list of observation sequence (in terms of index, not the actual symbol)

    Returns:
    - beta: A numpy array beta[j, t] = P(Z_t = s_j, x_t+1:x_T)
    """
    S = len(pi)
    N = len(O)
    beta = np.zeros([S, N])
    ###################################################
    # Q3.1 Edit here
    ###################################################
    #index = N-1
    start = True
    for index in range(N-1,-1,-1):
  	    for state in range(S):
  		    if start == True:
  			    beta[state, index] = 1#B[state, val]
  		    else:
  			    _sum = 0
  			    for next_state in range(S):
  				    _sum = _sum + A[state, next_state] * B[next_state, O[index+1]]*beta[next_state, index+1]
  			    beta[state, index] = _sum
  	    if start == True:
  		    start = False
  	    #index = index -1

    #print (beta)
    return beta

def seqprob_forward(alpha):
    """
    Total probability of observing the whole sequence using the forward algorithm

    Inputs:
    - alpha: A numpy array alpha[j, t] = P(Z_t = s_j, x_1:x_t)

    Returns:
    - prob: A float number of P(x_1:x_T)
    """
    prob = 0
    ###################################################
    # Q3.2 Edit here
    ###################################################
    N = len(alpha[0])
    prob = np.sum(alpha[:, N-1])
  	#    prob = prob + alpha[state, N-1]
  
    return prob


def seqprob_backward(beta, pi, B, O):
    """
    Total probability of observing the whole sequence using the backward algorithm

    Inputs:
    - beta: A numpy array beta: A numpy array beta[j, t] = P(Z_t = s_j, x_t+1:x_T)
    - pi: A numpy array of initial probailities. pi[i] = P(Z_1 = s_i)
    - B: A numpy array of observation probabilities. B[i, k] = P(X_t = o_k| Z_t = s_i)
    - O: A list of observation sequence
      (in terms of the observation index, not the actual symbol)

    Returns:
    - prob: A float number of P(x_1:x_T)
    """
    prob = 0
    ###################################################
    # Q3.2 Edit here
    ###################################################
    for state in range(len(beta)):
  	    prob = prob + beta[state, 0]*pi[state]*B[state, O[0]]
  
    return prob

def viterbi(pi, A, B, O):
    """
    Viterbi algorithm

    Inputs:
    - pi: A numpy array of initial probailities. pi[i] = P(Z_1 = s_i)
    - A: A numpy array of transition probailities. A[i, j] = P(Z_t = s_j|Z_t-1 = s_i)
    - B: A numpy array of observation probabilities. B[i, k] = P(X_t = o_k| Z_t = s_i)
    - O: A list of observation sequence (in terms of index, not the actual symbol)

    Returns:
    - path: A list of the most likely hidden state path k* (in terms of the state index)
        argmax_k P(s_k1:s_kT | x_1:x_T)
    """
    path = []
    ###################################################
    # Q3.3 Edit here
    ###################################################
    S = len(pi)
    N = len(O)
    delta = np.zeros([S, N])
    start = True
    index = 0
    store = np.zeros([S, N])
    for val in O:
    	for state in range(S):
    		if start == True:
    			delta[state, index] = pi[state] * B[state, val]
    		else:
    			_max = float('-inf')
    			for prev_state in range(S):
    				term = A[prev_state, state] * delta[prev_state, index-1]
    				if (term > _max):
    					_max = term
    					store[state, index] = prev_state
    			delta[state, index] = B[state, val] * _max
    	if start == True:
    		start = False
    	index += 1
    #print (delta)
    #print (store)
    path.append(np.argmax(delta[:, N-1]))
    index = 0
    for i in range(N-1,-1,-1):
    	#print ("path" + str(path))
    	#print (store[int(path[index]),index])
    	path.append(int(store[int(path[index]),i]))
    	index += 1
    #print (path[::-1])
    path = path[0:len(path)-1]
    return path[::-1]



##### DO NOT MODIFY ANYTHING BELOW THIS ###################
def main():
  model_file = sys.argv[1]
  Osymbols = sys.argv[2]

  #### load data ####
  with open(model_file, 'r') as f:
    data = json.load(f)
  A = np.array(data['A'])
  B = np.array(data['B'])
  pi = np.array(data['pi'])
  #### observation symbols #####
  obs_symbols = data['observations']
  #### state symbols #####
  states_symbols = data['states']

  N = len(Osymbols)
  O = [obs_symbols[j] for j in Osymbols]

  alpha = forward(pi, A, B, O)
  beta = backward(pi, A, B, O)

  prob1 = seqprob_forward(alpha)
  prob2 = seqprob_backward(beta, pi, B, O)
  print('Total log probability of observing the sequence %s is %g, %g.' % (Osymbols, np.log(prob1), np.log(prob2)))

  viterbi_path = viterbi(pi, A, B, O)

  print('Viterbi best path is ')
  for j in viterbi_path:
    print(states_symbols[j], end=' ')

if __name__ == "__main__":
  main()