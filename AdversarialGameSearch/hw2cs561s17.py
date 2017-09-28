import sys
import math
import time;
from sets import Set
class Node:
    def __init__(self, state, color, player):
        self.state = state
        self.color = color
        self.player = player
        return

class MyLifoQueue:
    nodes = []
    def add(self, node):
        self.nodes.append(node)

    def qsize(self):
        return self.nodes.__len__()

    def pop(self):
        return self.nodes.pop(self.qsize() -1)

    def peek(self):
        aNode = self.nodes.pop(self.qsize() - 1)
        self.add(aNode)
        return aNode

    def getList(self):
        return self.nodes

    def contains(self, nodeName):
        for node in self.nodes:
            if (nodeName == node.name):
                return True
        return False
    def empty(self):
        if (self.qsize() == 0):
            return True
        else:
            return False

def getPlayerPreferences(playerString):
    pairs = playerString.split(',')
    preferenceList = {}
    for playerPreference in pairs:
        playerPreferencePair = playerPreference.split(':')
        preferenceList[playerPreferencePair[0].strip()] = int(playerPreferencePair[1].strip())
    return preferenceList

def utility(util, color, player):
    if (player == 'P1'):
        val = player1Preferences[color]
        return (util + val)
    elif (player == 'P2'):
        val = player2Preferences[color]
        return (util - val)

def getChildren(parentsList):
    arcRule1Neighbors = {}
    for k in originalStateList:
        arcRule1Neighbors[k] = 0;
    for parent in parentsList:
        state = parent.state
        neighborList = adjacencyMap[state]
        for neighbor in neighborList:
            arcRule1Neighbors[neighbor] = 1;
    for parent in parentsList:
        arcRule1Neighbors[parent.state] = 0;
    childrenMap = {}
    for arcRule1Neighbor in arcRule1Neighbors:
        arcRule1ColoredNeighbors = {}
        for color in colorValues:
            arcRule1ColoredNeighbors[color] = 1;
        childrenMap[arcRule1Neighbor] = arcRule1ColoredNeighbors
    for parent in parentsList:
        adjacencyList = adjacencyMap[parent.state]
        parentColor = parent.color
        for adjacent in adjacencyList:
            colorsSet = childrenMap[adjacent]
            colorsSet[parentColor] = 0;

    children = []
    for aState in originalStateList:
        if (arcRule1Neighbors[aState] == 1):
            colorSet = childrenMap[aState]
            for color in colorValues:
                if (colorSet[color] == 1):
                    node = Node(aState, color, '')
                    children.append(node)
    return children

def outputAppender(string):
    fo.write(string + "\n")

def alphaBetaSearch(stack, util, state, color):
    v = alphaBetaMax(stack, state, color, 0, util, float('-inf'),float('inf'))

def getPrintableAlphaBeta(value):
    if (value == float('-inf')):
        return '-inf'
    elif (value == float('inf')):
        return 'inf'
    else:
        return str(int(value))

def alphaBetaMax(stack, state, color, depth, util, alpha, beta):
    newUtil = utility(util, color, 'P2')
    v = float('-inf')
    if (depth == maxDepth):
        outputAppender(", ".join([state, color, str(depth), str(newUtil), getPrintableAlphaBeta(alpha), getPrintableAlphaBeta(beta)]))
        return newUtil
    else:
        actions = getChildren(stack.getList())
        if (len(actions) == 0):
            outputAppender(", ".join([state, color, str(depth), str(newUtil), getPrintableAlphaBeta(alpha), getPrintableAlphaBeta(beta)]))
            return newUtil
        else:
            outputAppender(", ".join([state, color, str(depth), str(v), getPrintableAlphaBeta(alpha), getPrintableAlphaBeta(beta)]))
        for a in actions:
            stack.add(Node(a.state, a.color, ''))
            oldv = v
            v = max(v, alphaBetaMin(stack, a.state, a.color, depth + 1, newUtil, alpha, beta))
            if (depth == 0 and v != oldv):
               choice = a.state + ", " + a.color + ", " + str(v)
            stack.pop()
            if (v >= beta):
                outputAppender(", ".join([state, color, str(depth), str(v), getPrintableAlphaBeta(alpha), getPrintableAlphaBeta(beta)]))
                return v
            alpha = max(alpha, v)
            outputAppender(", ".join([state, color, str(depth), str(v), getPrintableAlphaBeta(alpha), getPrintableAlphaBeta(beta)]))

        if (depth == 0):
            fo.write(choice)
        return v

def alphaBetaMin(stack, state, color, depth, util, alpha, beta):
    newUtil = utility(util, color, 'P1')
    v = float('inf')
    #print "maxdepth: " + str(maxDepth) + " depth: " + str(depth)
    if (depth == maxDepth):
        #print "terminal"
        outputAppender(", ".join([state, color, str(depth), str(newUtil), getPrintableAlphaBeta(alpha), getPrintableAlphaBeta(beta)]))
        return newUtil
    else:
        actions = getChildren(stack.getList())
        if (len(actions) == 0):
            outputAppender(", ".join([state, color, str(depth), str(newUtil), getPrintableAlphaBeta(alpha), getPrintableAlphaBeta(beta)]))
            return newUtil
        else:
            outputAppender(", ".join([state, color, str(depth), str(v), getPrintableAlphaBeta(alpha), getPrintableAlphaBeta(beta)]))
        for a in actions:
            stack.add(Node(a.state, a.color, ''))
            v = min(v, alphaBetaMax(stack, a.state, a.color, depth + 1, newUtil, alpha, beta))
            stack.pop()
            if (v <= alpha):
                outputAppender(", ".join([state, color, str(depth), str(v), getPrintableAlphaBeta(alpha), getPrintableAlphaBeta(beta)]))
                return v
            beta = min(beta, v)
            outputAppender(", ".join([state, color, str(depth), str(v), getPrintableAlphaBeta(alpha), getPrintableAlphaBeta(beta)]))
        return v

fileText=[]
#sys.argv[2]
#print "Number of ticks since 12:00am, January 1, 1970:", time.time()
with open(sys.argv[2]) as f:
    fileText.extend(f.read().splitlines())
fo= open("output.txt","w")
maxDepth = 0
colorValues = []
colorString = fileText[0]
colors = colorString.split(',')
for color in colors:
    colorValues.append(color.strip())
colorValues.sort()
coloredNodes = []
colorAssignmentString = fileText[1]
colorAssignments = colorAssignmentString.split(',')
for colorAssignment in colorAssignments:
    info = colorAssignment.split(':')
    colorAndPlayer = info[1].split('-')
    node = Node(info[0].strip(), colorAndPlayer[0].strip(), int(colorAndPlayer[1].strip()))
    coloredNodes.append(node)

maxDepth = int(fileText[2])
#player1
player1Preferences = getPlayerPreferences(fileText[3])

#player2
player2Preferences = getPlayerPreferences(fileText[4])
inputAdjacencyIndex = 5

adjacencyMap = {}
while (inputAdjacencyIndex < len(fileText)):
    adjacency = fileText[inputAdjacencyIndex].split(':')
    neighbors = adjacency[1].split(',')
    neighborList = []
    for neighbor in neighbors:
       neighborList.append(neighbor.strip())
    adjacencyMap[adjacency[0].strip()] = neighborList
    inputAdjacencyIndex = inputAdjacencyIndex + 1

originalStateList = adjacencyMap.keys()
originalStateList.sort()
util = 0
stack = MyLifoQueue()
for assigned in coloredNodes:
    #print assigned.state + " " + assigned.color  + " " +  str(assigned.player)
    stack.add(Node(assigned.state, assigned.color, assigned.player))
    if (assigned.player == 1):
        #print "Its 1"
        score = player1Preferences[assigned.color]
        util = util + int(score)
    elif (assigned.player == 2):
        #print "Its 2"
        score = player2Preferences[assigned.color]
        util = util - int(score)
lastNode = stack.peek()
if (lastNode.player == 1):
    # print "Its 1"
    score = player1Preferences[assigned.color]
    util = util - int(score)
elif (lastNode.player == 2):
    # print "Its 2"
    score = player2Preferences[assigned.color]
    util = util + int(score)

alphaBetaSearch(stack, util, lastNode.state, lastNode.color)
#print "Number of ticks since 12:00am, January 1, 1970:", time.time()
fo.close()