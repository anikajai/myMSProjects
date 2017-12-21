Part 1
======
Implemented a Model-based Collaborating Filtering(CF) recommendation system using Spark MLlib. The datasets you are going to
use are the MovieLens datasets.

Part 2
=======
Implemented Item-based CF system without using Pearson correlation weighted similarity and prediction. The datasets you are going to
use are the MovieLens datasets. The goal of the assignment is to find a way to improve the accuracy of the recommendation system yourself.


Download testing file: testing_small.csv. The testing dataset is a subset of the original dataset, each containing two columns: <userId> and <movieId>. The file
testing_small.csv (20256 records) is from ratings.csv in ml-latest-small. The goal is to predict the ratings of every <userId> and <movieId> combination in the test files. 
We CANNOT use the ratings in the testing datasets to train your recommendation system. Thus, extracted training data from the ratings.csv file downloaded from Movie Lens using the testing data. Then by using the training data, predicted rate for movies in the testing dataset. Used the testing data as ground truth to evaluate the accuracy of my recommendation system.

Part 1
======

Execution command:
spark-submit --class Anika_Jain_task1 Anika_Jain_task1.jar ratings.csv testing_small.csv
>=0 and <1:14771
>=1 and <2:4591
>=2 and <3:799
>=3 and <4:90
>=4:5
RMSE = 0.9536211776047475
Accuracy is 0.9536
Explanation:
For cold start cases, assigned average rating of the user from training file, if the user ever rated any movie, else if movie was ever rated by anyone assigned it’s average rating. If none of these two cases found then assigned it to 0.0.

Part 2
======

Execution command:
spark-submit --class Anika_Jain_task2 --driver-memory 3g --master local[*] Anika_Jain_task2.jar ratings.csv testing_small.csv
>=0 and <1:3
>=1 and <2:115
>=2 and <3:2912
>=3 and <4:13158
>=4:4068
RMSE = 1.0153901793408913
Accuracy is 1.0154
Explanation:
For cold start cases, assigned average rating of the user from training file, if the user ever rated any movie, else if movie was ever rated by anyone assigned it’s average rating. If none of these two cases found then assigned it to 0.0.
If predicted rating is below 1, set it to 1, because if it is less than 1 then the rating must be of the lowest value that user rates with and if it is greater than 5 set it to 5.