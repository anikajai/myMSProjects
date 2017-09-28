The goal is to implement a game at which two agents alternate in assigning values to the variables of a constraint satisfaction problem (CSP).
The type of CSP here is limited to the map coloring problem where a set of states and their neighbors are given and the goal is to color every
state in a way that it has a different color than its neighbors. The variables of the CSP are the states, and the colors represent the finite
domain of such variables. The game is played by two minimax agents which behave in the same way, except that they follow a defined set of 
preferences: for each agent, every color is assigned a weight and the goal is it to make assignments (i.e. take actions) that maximize the
total sum of weights.

Description
=============
1. I have deined class for Node of the search tree which contains it's state, color and player.
2. I have defined a custom stack as MyLifoQueue with a set of functions.
3. The code stores values of colors in appropriate values.
4. Read Player1 and Player2 preferences.
5. Prepared adjacency matrices.
6. Executed Aplpha-beta search on the top most element to find the best next move.
7. Inevery alpha-beta min/max method actions are fetched with a check of arc consistency, giving possible valid children of current node.
