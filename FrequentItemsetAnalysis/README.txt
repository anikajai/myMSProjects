Implemented SON Algorithm to solve every case on top of Apache Spark Framework.
-------------------------------------------------------------------------------

Steps to find all the possible combinations of the frequent itemsets for any given input file that follows the format of the MovieLens Ratings Dataset ml-1m.
Specifically, in order to process each chunk, I used A-Priori for counting frequent item-set in each phase.
Computed frequent itemsets using SON algorithm, for the dataset as ml-1m (ml-1m.zip)

Case 1
------
We would like to calculate the combinations of frequent movies (as singletons, pairs, triples, etc…) that
were rated by male users and are qualified as frequent given a support threshold value.
In order to apply this computation, we will need to create a basket for each male user containing the ids of
the movies that were rated by this male user. If a movie was rated more than one time from a user, we
consider that this movie was rated only once. More specifically, the movie ids are unique within each
basket.The generated baskets are similar to:
Male-user1 = (movie11, movie12, movie13, …)
Male-user2 = (movie21, movie22, movie23, …)
Male-user3 = (movie31, movie32, movie33, …)

Case 2
-------
In the second case we want to calculate the combinations of frequent female users (as singletons, pairs,
triples, etc…) who rated the movies. The frequent combination of female users has to be calculated on the
basis of the support threshold value.
In order to apply this computation, we will need to create a basket for each movie that has been rated by
the female users. More specifically, the female users ids are unique within each basket. The generated
baskets are similar to:
Movie 1 = (female-user1, female-user2, female-user3, …)
Movie 2 = (female-user3, female-user4, female-user5, …)
Movie 3 = (female-user1, female-user2, female-user5, …)

To execute the jar file, go to the jar location folder and execute following command:
spark-submit –class hw2 File_SON.jar <case_1_or_2> <Path_to_ratings_data_file> <Path_to_users_data_file> <support>

Approach description:
--------------------
1.	For both cases, I extracted baskets after joining and grouping on attributes. 
2.	I flattened those baskets to get initial single frequent candidate sets to go in to phase 1’s Map algorithm.
3.	Phase 1’s map is called via mappartition on baskets. Inside Phase 1 Map, I treated every time a list of candidate pairs for threshold support check into a list of list of String. Note: In my entire code, I used Sets to avoid duplicate entries.
4.	The modified support threshold is based on fraction of elements received by a call of mappartition * support divided by the total no. of elements in the data. At every iteration after I get list of frequent item-sets among candidates, I flatten this list to form possible candidate pairs for next level size. After this, for every such pair I check if its 1 level less all possible pairs do exist in my last set of frequent items. If this pair passes this case then it is treated as a candidate pair for frequent itemset test.
5.	The frequent itemset test is overall based on Apriori. An item is frequent if it’s occurrence in baskets in greater than or equal to the threshold support for the partition.
6.	After phase 1 map, the output is simplify provided as input to phase 2’s map function.
7.	Phase 2 map function in partitions (used mappartition) generate key value pairs for all the phase 1’s result candidate pairs, where key is the candidate pair and value is the no. of baskets containing this pair-set. 
8.	In the phase 2 reduce phase, we sum the total count of occurrence of each candidate pair in the baskets and filter those records whose count crosses the support input in arguments.
9.	After all this, we have the frequent item sets with us. I just sorted the numbers within the baskets, then sorted baskets based on their value range and then, on item-set’s length. Then displayed these in the desired format using a custom print method.




