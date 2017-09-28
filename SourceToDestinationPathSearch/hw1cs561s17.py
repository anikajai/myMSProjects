import sys
from Queue import PriorityQueue
class Node:
    def __init__(self, name, parent, cost, fuelcost):
        self.parent = parent
        self.name = name
        self.cost = cost
        self.fuelcost = fuelcost
        return
    def __cmp__(self, other):
        val = cmp(self.fuelcost, other.fuelcost)
        if (val == 0):
            return cmp(self.name, other.name)
        else:
            return val

class MyQueue:
    nodes = []
    def add(self, node):
        self.nodes.append(node)

    def pop(self):
        return self.nodes.pop(0)

    def contains(self, nodeName):
        for node in self.nodes:
            if (nodeName == node.name):
                return True
        return False
    def empty(self):
        if (self.nodes.__len__() == 0):
            return True
        else:
            return False

class MyLifoQueue:
    nodes = []
    def add(self, node):
        self.nodes.append(node)

    def qsize(self):
        return self.nodes.__len__()

    def pop(self):
        return self.nodes.pop(self.qsize() -1)

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

fileText=[]
with open(sys.argv[2]) as f:
    fileText.extend(f.read().splitlines())
fo= open("output.txt","w")

def contains(exploredSet, nodeName):
    for item in exploredSet:
        if (item.name == nodeName):
            return True
    return False

algo = fileText[0]
fuel = fileText[1]
start = fileText[2]
end = fileText[3]
adjacencyList = {}
for index, item in enumerate(fileText, start=0):
    if (index > 3):
        lineComponents = item.split(': ')
        mainNode = lineComponents[0] # origin node
        finalNodes = lineComponents[1].split(', ')
        adjacentNode = []
        dict = {}
        for finalNode in finalNodes:
            nodeValue = []
            nodeValue = finalNode.split('-')
            adjacentNode.append(nodeValue[0])
            myNode = Node(nodeValue[0], "", nodeValue[1], 0)
            dict[nodeValue[0]] = myNode
        adjacentNode.sort()
        adjacencyArray = []
        #print ("mainnode is" + mainNode)
        for aNode in adjacentNode:
            adjacencyArray.append(dict[aNode])
            #print("adjacent is: " + aNode)
        adjacencyList[mainNode] = adjacencyArray

if (algo.upper() == "BFS"):
    frontier = MyQueue()
    startNode = Node(start, "", 0, fuel)
    frontier.add(startNode)
elif (algo.upper() == "DFS"):
    frontier = MyLifoQueue()
    startNode = Node(start, "", 0, fuel)
    frontier.add(startNode)
elif (algo.upper() == "UCS"):
    frontier= PriorityQueue()
    startNode = Node(start, "", 0, -1 * long(fuel))
    frontier.put(startNode)

exploredSet = {}
#frontier.put(startNode)
goalFound = False
while (not frontier.empty()):
    #exploringNode = frontier.get()
    if (algo.upper() == "UCS"):
        exploringNode = frontier.get()
    else:
        exploringNode = frontier.pop()
    #print ("exploding is:" + exploringNode.name)
    if (not (exploringNode.name in exploredSet)):
        #print ("val" + exploringNode.name + str(exploringNode.fuelcost))
        parent = exploringNode.name
        #print ("name " + parent)
        exploredSet[parent] = exploringNode
        if (parent == end):
            goalFound = True
            break
        else:
            adjacencyArray = []
            adjacencyArray = adjacencyList[parent]
            if (algo.upper() == "BFS"):
                for neighbour in adjacencyArray:
                   #if (not(neighbour.name in exploredSet)): #and not(frontier.contains(neighbour.name))):
                    remainingFuel = long(exploringNode.fuelcost) - long(neighbour.cost)
                    if ((remainingFuel >= 0) and (not (neighbour.name in exploredSet))):
                        newNode = Node(neighbour.name, parent, neighbour.cost, remainingFuel)
                        frontier.add(newNode)
            elif (algo.upper() == "DFS"):
                for neighbour in reversed(adjacencyArray):
                    #if (not(neighbour.name in exploredSet)): #and not(frontier.contains(neighbour.name))):
                    remainingFuel = long(exploringNode.fuelcost) - long(neighbour.cost)
                    if ((remainingFuel >= 0) and (not (neighbour.name in exploredSet))):
                        newNode = Node(neighbour.name, parent, neighbour.cost, remainingFuel)
                        frontier.add(newNode)
            elif (algo.upper() == "UCS"):
                for neighbour in adjacencyArray:
                    # if (not(neighbour.name in exploredSet)): #and not(frontier.contains(neighbour.name))):
                    remainingFuel = long(exploringNode.fuelcost) + long(neighbour.cost)
                    if ((remainingFuel <= 0)  and (not (neighbour.name in exploredSet))):
                        newNode = Node(neighbour.name, parent, neighbour.cost, remainingFuel)
                        frontier.put(newNode)
outputString = ""
if (goalFound):
    #nodeSet = MyLifoQueue()
    node = exploredSet[end]
    remainingFuel = node.fuelcost
    if (algo.upper() == "UCS"):
        remainingFuel = -1 * remainingFuel
    outputString = outputString + " " + str(remainingFuel)
    #print ("node is: " + node.name)
    while (True):
        outputString = node.name + outputString
        currentParent = node.parent
        if (currentParent == ""):
            break
        else:
            outputString = "-" + outputString
            node = exploredSet[currentParent]
else:
    outputString = "No Path"
fo.write(outputString)
fo.close()