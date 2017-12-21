Implemented Girvan -Newman algorithm for Community detection on users in MovieLens million dataset.
User1 and user2 has an edge if tehy have rated greater than or equal to 3 movies in common.
The Betweenness is computed for this graph and then ordered edges in decreasing order of betweenness.
Removing one edge at a time, modularity is computed and the first dip in modularity marks the community set.

Two implementations:
1. Observing connected components using graphx and stopping when modularity dips for the first time
2. Observing connected components using custom BFS algorithm, and observing fluctuations in modularity till all edges are removed.

Execution command:
 spark-submit --class Anika_Jain_hw5 Communties.jar  ratings.csv Anika_Jain_communities.txt Anika_Jain_betweenness.txt