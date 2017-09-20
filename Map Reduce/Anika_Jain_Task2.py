from pyspark import SparkContext
import sys

sc = SparkContext.getOrCreate()
usersFile_rdd = sc.textFile(sys.argv[1]).map(lambda line: tuple(line.split("::"))).map(lambda user: (user[0], user[1]))#.collect()
#count = 0
#for (k,user) in usersFile_rdd:
 #  print ( k + "with value as:" +user[0]+ user[1])
  # count = count + 1
   #if count == 5:
    # break

rating_rdd = sc.textFile(sys.argv[2])
ratingUserFile_rdd = rating_rdd.map(lambda line: tuple(line.split("::"))).map(lambda rating: (rating[0], (rating[1], rating[2])))
moviesFile_rdd = sc.textFile(sys.argv[3]).map(lambda line: tuple(line.split("::"))).map(lambda movie: (movie[0],  movie[2]))
user_rating_joined = usersFile_rdd.join(ratingUserFile_rdd).map(lambda (userId, (gender,(movieId, rating))):(movieId,(gender,rating)))
final_joined = moviesFile_rdd.join(user_rating_joined)\
    .map(lambda (movieId,(genre, (gender, rating))):((str(genre), str(gender)), float(rating)))\
    .mapValues(lambda v: (v,1)).reduceByKey(lambda a,b: (float(a[0])+float(b[0]), a[1]+b[1])).mapValues(lambda v:round(v[0]/v[1], 11))\
    .sortByKey(True)
#for k in temp:
 #  print k
output = final_joined.collect()
output_file = open("anika_jain_result_task2.txt", "w")
for ((k1,k2),v) in output:
   line = (k1+","+ k2 + "," + str(v) + "\n")
   output_file.write(line)
