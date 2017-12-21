import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.collection.mutable._
import java.io._
import scala.math.Ordering.Implicits._


object hw2 {
  def main(args:Array[String]): Unit = {
    var sConfig = new SparkConf().setMaster("local[2]").setAppName("MyHW2")
    var sc = new SparkContext(sConfig)
    var setOfValues:RDD[scala.collection.immutable.Set[String]] = getSetOfValuesBasedOnCase(sc:SparkContext, args(0), args(1), args(2));

    var baskets:RDD[scala.collection.immutable.Set[String]] = setOfValues
    var superSet1 = setOfValues.flatMap(v=>v).collect().toSet
    var superSet:scala.collection.immutable.Set[scala.collection.immutable.Set[String]] = superSet1.map(v=>scala.collection.immutable.Set(v))
    var support = args(3).toInt
    //var partitionCount = baskets.getNumPartitions
    var newSupportThreshold = support.toDouble/baskets.count()

    var map1Result:RDD[scala.collection.immutable.Set[String]] = baskets.mapPartitions(map1ForChunk(newSupportThreshold, superSet))
    var inputForPhase2:List[scala.collection.immutable.Set[String]] = map1Result.collect().toList

    var candidatePairsPhase1:scala.collection.immutable.Set[scala.collection.immutable.Set[String]] = map1Result.collect().toSet
    var map2Result:RDD[(scala.collection.immutable.Set[Int], Int)] = baskets.mapPartitions(map2ForChunk(inputForPhase2))
    var myTemp:Array[List[Int]] = map2Result.reduceByKey((x,y) => x+y).filter{ case(x,y) => y>=support}.map{ case(k,v)=>k.toList.sorted}.collect()
    var sortedFrequentItemsets =  myTemp.sorted.sortBy(_.length)
    printToFile(args(0), support, sortedFrequentItemsets)

  }

  def getSetOfValuesBasedOnCase(sc:SparkContext, _case:String, ratingFileName:String, userFileName:String): RDD[scala.collection.immutable.Set[String]] = {
    if (_case == "1") {
      var maleUsers = sc.textFile(userFileName).map(lines => lines.split("::").toList).filter(_(1) == "M").map(full=> (full(0), 1))
      var ratingsLines = sc.textFile(ratingFileName).map(lines=>lines.split("::").toList).map(full=>(full(0), full(1))).groupByKey.mapValues(_.toSet)
      var filteredUsers = maleUsers.join(ratingsLines).map{case (k1,(v1, v2)) => (k1,v2)}//.sortByKey();
      return filteredUsers.map{case (k,v) => v}
    } else if (_case == "2") {
      var femaleUsers = sc.textFile(userFileName).map(lines => lines.split("::").toList).filter(_(1) == "F").map(full=> (full(0), 1))
      var userRatings = sc.textFile(ratingFileName).map(lines=>lines.split("::").toList).map(full=>(full(0), full(1)))//.groupByKey.mapValues(_.toSet);
      var filteredUsers = femaleUsers.join(userRatings).map{case (k1,(v1, v2)) => (v2,k1)}.groupByKey.mapValues(_.toSet)//.sortByKey();
      return filteredUsers.map{case (k,v) => v}
    } else {
      println("Improper case value.**** PROGRAM TERMINATING****")
      return null
    }
  }

  def printToFile(_case:String, support:Int, lines:Array[List[Int]])= {
    var fileName:StringBuilder = new StringBuilder("Anika_Jain_SON.")
    if (_case == "1") {
      fileName.append("case1_")
    } else {
      fileName.append("case2_")
    }
    val output = new PrintWriter(new File(fileName.append(support).append(".txt").toString()), "UTF-8")


    try {
      var len = 0
      var start = true
      for (line:List[Int] <- lines){
        var currentLen = line.length
        if (len != currentLen) {
          if (start == true) {
            start = false
          } else {
            output.println()
          }
          len = currentLen
        } else {
          output.print(", ")
        }
        output.print(line.mkString("(",", ", ")"))
      }
    } finally {
      output.close
    }
  }

  def map2ForChunk (pairs:scala.collection.immutable.List[scala.collection.immutable.Set[String]])
                   (basketsIterator:Iterator[scala.collection.immutable.Set[String]]):Iterator[(scala.collection.immutable.Set[Int], Int)] = {
    var baskets:List[scala.collection.immutable.Set[String]] = basketsIterator.toList
    var listBaskets:List[List[String]] = baskets.map(elem=>elem.toList)
    var mapItemValue:List[(scala.collection.immutable.Set[Int], Int)] = pairs.map(pair=> {
      var count:Int = 0
      listBaskets.foreach(basket => {
        if (pair.forall(basket.contains)) count +=1
      })
      (pair.map(_.toInt),count)
    })
    return mapItemValue.toIterator
  }

  def map1ForChunk(support:Double,
                   pairs:scala.collection.immutable.Set[scala.collection.immutable.Set[String]])
                  (basketsIterator:Iterator[scala.collection.immutable.Set[String]]): Iterator[scala.collection.immutable.Set[String]] = {
    var result = new ListBuffer[scala.collection.immutable.Set[String]]()
    var level= 1
    var currentPairs = pairs.toList
    var baskets = basketsIterator.toList

    var supportThresholdModifiedPerPartition = support*baskets.length
    //println("Baskets count is:"+ baskets.length + "new support is:" + supportThresholdModifiedPerPartition)
    while(true) {
      var lastList:List[scala.collection.immutable.Set[String]] = getFrequentItemsets(currentPairs, baskets, supportThresholdModifiedPerPartition)
      if (lastList.isEmpty) {
        return result.toIterator
      }
      result++=lastList
      var baseItems = lastList.flatten
      level += 1
      if (baseItems.length < level) {
        return result.toIterator
      }
      var candidates:Iterator[List[String]] = baseItems.combinations(level)
      var currentPairs2 = candidates.toList
      var preparingPairs = new ListBuffer[scala.collection.immutable.Set[String]]()
      for (aMayBePair<-currentPairs2) {
        var candidates1:Iterator[List[String]] = aMayBePair.combinations(level-1)
        var shortLevelPairsNeeded = candidates1.map(_.toSet).toList
        if (shortLevelPairsNeeded.forall(lastList.contains)) {
            preparingPairs.append(aMayBePair.toSet)
        }
      }
      currentPairs = preparingPairs.toList
    }
    return result.toIterator
  }


  def getFrequentItemsets(pairs:List[scala.collection.immutable.Set[String]],
                          baskets:List[scala.collection.immutable.Set[String]],
                          support:Double): List[scala.collection.immutable.Set[String]] = {
    var list = new ListBuffer[scala.collection.immutable.Set[String]]()
    for (pair<-pairs) {
      var count = 0
      for (basket<-baskets) {
        if (pair.forall(basket.contains)) {
          count = count + 1
        }
      }
      if (count > support) {
        list.append(pair)
      }
    }
    return list.toList
  }
}
