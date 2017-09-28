from pyspark import SparkContext
import sys

sc = SparkContext.getOrCreate()
usersFile_rdd = sc.textFile(sys.argv[1]).map(lambda line: tuple(line.split("::"))).map(lambda user: (user[0], user[1]))
rating_rdd = sc.textFile(sys.argv[2])
ratingUserFile_rdd = rating_rdd.map(lambda line: tuple(line.split("::"))).map(lambda rating: (rating[0], (rating[1], rating[2])))
user_rating_joined = usersFile_rdd.join(ratingUserFile_rdd)
avg_by_key = user_rating_joined.map(lambda rating: ((int(rating[1][1][0]),str(rating[1][0])),float(rating[1][1][1]))).mapValues(lambda v: (v, 1))\
    .reduceByKey(lambda a,b: (float(a[0])+float(b[0]), a[1]+b[1])).mapValues(lambda v:round(v[0]/v[1], 11))\
    .sortByKey(True)#flatMap(lambda ((k1,k2),v): (k1,k2,v))
output = avg_by_key.collect()
output_file = open("anika_jain_result_task1.txt", "w")
for ((k1,k2),v) in output:
    line = (str(k1)+","+ k2 + "," + str(v) + "\n")
    output_file.write(line)