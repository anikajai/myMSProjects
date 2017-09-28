To implement the following search algorithms to deliver goods from a starting position S to a destination position D in an
undirected weighted graph G via a delivery drone:
● Breadth-first search
● Depth-first search
● Uniform-cost search using the amount of fuel needed as cost
The drone has an initial amount of fuel, F, and cannot refill until it reaches the destination. The
nodes in G represent the places where the drone can travel to. If an edge exists between node
A and node B, the drone can travel from A to B. The weight of an edge corresponds to the
amount of fuel needed to traverse that edge.

Description
=============
1. I have deined class for Node of the search tree which contains it's name, parent, cost and fuelcost.
2. I have defined a custom Queue as MyQueue with a set of functions.
3. I have defined a custom stack as MyLifoQueue with a set of functions.
4. The code takes input file path and name as argument.
5. With fuel constraints, code maintains a current Queue as frontier and exploredSet.
6. Depending on the search algorithm the earliest found path is returned if found, else No Path.
