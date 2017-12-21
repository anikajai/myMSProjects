
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import java.io._

import scala.collection.mutable._
import org.apache.spark.rdd.RDD

object Anika_Jain_task2 {
  def main(args:Array[String])= {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("HW3")
    val sc = new SparkContext(sparkConf)
    val bigSetWithHeader = sc.textFile(args(0))
    val smallSetWithHeader = sc.textFile(args(1))
    val bigSetHeader = bigSetWithHeader.first()
    val smallSetHeader = smallSetWithHeader.first()
    val bigSetWithoutHeader = bigSetWithHeader.filter(line => line != bigSetHeader)
    val smallSetWithoutHeader = smallSetWithHeader.filter(line => line != smallSetHeader)
    val bigSet = bigSetWithoutHeader.map(row => row.split(",")).map(myList => ((myList(0).toInt, myList(1).toInt), myList(2).toDouble))

    val smallSet = smallSetWithoutHeader.map(row => row.split(",")).map(myList => (myList(0).toInt, myList(1).toInt))
    val smallSetPart1 = smallSet.map( x=> ((x._1, x._2), 1))

    val orginalTestRatings = bigSet.join(smallSetPart1).map { case ((k1, k2), (v1, v2)) => ((k1, k2), v1) }
    val trainingData = bigSet.subtractByKey(orginalTestRatings)


    val userAverageMap = trainingData.map{
      case ((u, m),r) => (u,(r,1))
    }.reduceByKey((a,b) =>(a._1+b._1, a._2 + b._2)).map{
      case (u,(num,den)) => (u,num/den)
    }.collectAsMap()

    val movieAverageMap = trainingData.map{
      case ((u, m),r) => (m,(r,1))
    }.reduceByKey((a,b) =>(a._1+b._1, a._2 + b._2)).map{
      case (m,(num,den)) => (m,num/den)
    }.collectAsMap()

    val movieUserData = trainingData.map{ case ((u, m),r) => (m,List(u))}.reduceByKey(_:::_)
    val movieUserDataMap = movieUserData.collectAsMap()

    val trainingDataRating = trainingData.map {
      case ((u,m),r) => ((u,m),r)
    }.collectAsMap()

    val trainingUserMovieList = trainingData.map {
      case ((user, movie),rating)=> (user, List(movie))
    }.reduceByKey(_:::_)//has users then list of movies

    val  testTrainJoin  = smallSet.join(trainingUserMovieList)

    val userMovieMovieListMap = testTrainJoin.map{
      case (user, (movie, movieList)) => ((user,movie)->movieList)
    }.collectAsMap()
    val movieMoviePairs =   testTrainJoin.map{
      case (user, (movie1, movieList)) => (movie1, movieList)
    }.reduceByKey(_:::_).mapValues(_.toSet).flatMapValues(x=>x)

    val movieMoviePairs1 = movieMoviePairs.filter {
       case (m1,m2) => (m1 < m2)
    }

    val movieMoviePairs2 = movieMoviePairs.filter {
      case (m1,m2) => (m2 < m1)
    }.map {
      case (m1,m2) => (m2,m1)
    }

    val movieMoviePairsFinal = movieMoviePairs1.union(movieMoviePairs2).filter{
      case (m1,m2) => movieUserDataMap.contains(m1) && movieUserDataMap.contains(m2)
    }

    val userMoviePairs:RDD[((Int,Int), List[(Double,Double)])]  = movieMoviePairsFinal.mapPartitions(map1ForChunk(movieUserDataMap, trainingDataRating))
    val similarities:RDD[((Int,Int),Double)] = userMoviePairs.mapPartitions(map2ForChunk(1))
    val similarityAsMap = similarities.collectAsMap()
    //var minRMSE:Double = 0
      //var indexMinRMSE = -1
    //for (i<-(7 to 14)) {
    val smallSet2 = smallSet.map{
      case (u,m) => (u,m, userMovieMovieListMap(u,m))
    }
      var predictions = smallSet2.mapPartitions(map3ForChunk(13, similarityAsMap,trainingDataRating))
      predictions = predictions.mapPartitions(map4ForChunk(userAverageMap, movieAverageMap))
    /*  case ((u,m), r) => {
        var rUpdated = r
        if (r == 0.0) {
          if (userAverageMap.contains(u)) {
            rUpdated = userAverageMap(u)
          } else if (movieAverageMap.contains(m)) {
            rUpdated = movieAverageMap(m)
          }
        }
        ((u,m),rUpdated)
      }
    }*/
      predictions = predictions.sortBy(_._2).sortBy(_._1)
      val predictionsAsMap = predictions.collectAsMap()

      val accuracyLevels = orginalTestRatings.map{ case ((u,m),r) => (Math.floor(Math.abs(r-predictionsAsMap((u,m)))),1) }.reduceByKey(_+_).map {
        case (k,v) => (prepareLevels(k.toInt), v)
      }.reduceByKey(_+_).sortBy(_._1)
      val screenAccuracyDisplayedText:StringBuilder = new StringBuilder
      //screenAccuracyDisplayedText ++= "I is:" + i + "\n"
      for((k,v) <- accuracyLevels.collect()) {
        screenAccuracyDisplayedText ++= k
        screenAccuracyDisplayedText ++= ":"
        screenAccuracyDisplayedText ++= v.toString
        screenAccuracyDisplayedText ++= "\n"
      }

      val out = orginalTestRatings.map {
        case ((u,m),r) => {
          Math.pow(Math.abs(r-predictionsAsMap((u,m))),2)
          // err*err
        }
      }.mean()
      val rmse = Math.sqrt(out)
      screenAccuracyDisplayedText ++= "RMSE = "
      screenAccuracyDisplayedText ++= rmse.toString
      screenAccuracyDisplayedText ++= "\n"

      val screenOut = screenAccuracyDisplayedText.toString
      print(screenOut)

  //  }

  //  println("Best RMSE at " + indexMinRMSE + " with val " + minRMSE)

    val fileOut = predictions.map{ case ((u,m),r) => List(u.toString,m.toString,r.toString)}
    val outputFile = new PrintWriter(new File("Anika_Jain_result_task2.txt"))
    try {
      outputFile.println("UserId,MovieId,Pred_rating")
      for(line <- fileOut.collect()) {
        outputFile.println(line.mkString(","))
      }
    } finally {
      outputFile.close
    }
    //for (sim<-predictions.collect().take(100)) {
      //println(sim)
    //}
  }

  def  map4ForChunk (averageUserRatingMap:scala.collection.Map[Int, Double],
                     averageMovieRatingMap:scala.collection.Map[Int, Double])
                    (predictions:Iterator[((Int,Int),Double)]):Iterator[((Int,Int), Double)] = {
    val pUpdated = predictions.map{
      case ((u,m), r) => {
        var rUpdated:Double = r
        if (r == 0.0) {
          if (averageUserRatingMap.contains(u)) {
            rUpdated = averageUserRatingMap(u)
          } else if (averageMovieRatingMap.contains(m)) {
            rUpdated = averageMovieRatingMap(m)
          }
        }
        ((u,m),rUpdated)
      }
    }.toIterator
    return pUpdated
  }

  def map3ForChunk (limit:Int,
                    movieMovieSimilarityMap:scala.collection.Map[(Int,Int), Double],
                    userMovieRatingMap:scala.collection.Map[(Int,Int), Double])
                   (testData:Iterator[(Int,Int,List[Int])]):Iterator[((Int,Int), Double)] = {
    val userMovieMovies = testData.map {
      case (u,m, mList) => {
        val myList = new ListBuffer[(Int, Double)]()
        for (mL<-mList) {
          var pair = (1,1)
          if (m<mL) {
           pair = (m,mL)
          } else {
            pair = (mL,m)
          }
          if (movieMovieSimilarityMap.contains(pair)) {
            myList += ((mL,movieMovieSimilarityMap(pair)))
          }
        }
        val mostSimilar = myList.toList.sortBy(_._2)(Ordering[Double].reverse).take(limit)
        var num:Double = 0
        var den:Double = 0
        for ((mL,sim)<-mostSimilar) {
          num += sim*userMovieRatingMap(u,mL)
          den += Math.abs(sim)
        }
        var r:Double = 0
        if (den > 0) {
          r = num/den
          if (r<1) {
           // println("Less than 0 case")
            r = 1
          } else if (r > 5) {
            //println("> than 5 case")
            r = 5
          }
        }
        ((u,m),r)
      }
    }.toIterator
    return userMovieMovies
  }

  def map1ForChunk (movieUserDataMap:scala.collection.Map[Int,scala.collection.immutable.List[Int]], trainingDataRating:scala.collection.Map[(Int,Int),Double])
                   (moviePairIterator:Iterator[(Int,Int)]):Iterator[((Int,Int), List[(Double,Double)])] = {
    val moviePairUserList:Iterator[((Int,Int), scala.collection.immutable.List[Int])] = moviePairIterator.map {
      case (movie1, movie2) => ((movie1, movie2), movieUserDataMap(movie1).intersect(movieUserDataMap(movie2)))
    }

    val movieWithRatings: Iterator[((Int, Int), List[(Double, Double)])] = moviePairUserList.map {

      /*case ((m1, m2), users) => {
        //val myList = new ListBuffer[(Double, Double)]()
       val myList:List[(Int,Int), List[(Double,Double)]] = users.map {
          case user => ((m1,m2),List((trainingDataRating(user, m1), trainingDataRating(user, m2))))
        }
        ((m1, m2), myList)
      }*/
        case ((m1, m2), users) => {
        val myList = new ListBuffer[(Double, Double)]()
        for (user <- users) {
          myList += ((trainingDataRating(user, m1), trainingDataRating(user, m2)))
        }
        ((m1, m2), myList.toList)
      }
    }
    movieWithRatings
  }
   def map2ForChunk (x:Int) (movieWithRatings:Iterator[((Int,Int), List[(Double,Double)])]):Iterator[((Int, Int),Double)] = {
     movieWithRatings.map{
       case ((m1,m2), ratingList) => {
         var r1Mean:Double = 0
         var r2Mean:Double = 0
         var den = 0
         //ratingList(_)._1
         for((r1,r2) <-ratingList) {
           r1Mean += r1
           r2Mean += r2
           den +=1
         }
         r1Mean = r1Mean/den
         r2Mean = r2Mean/den
         val myList = new ListBuffer[(Double, Double)]()
         var num:Double = 0
         var den1:Double = 0
         var den2:Double = 0
         for((r1,r2) <-ratingList) {
           val newR1:Double = r1 -r1Mean
           val newR2:Double = r2 -r2Mean
           num += newR1 * newR2
           den1 += Math.pow(newR1,2)
           den2 += Math.pow(newR2,2)
         }
         if (den1 >0 && den2 > 0) {
           val w = num/(Math.sqrt(den1)*Math.sqrt(den2))
           ((m1,m2), w)
         } else {
           ((m1,m2),0)
         }
       }
     }
   }




  def prepareLevels(k:Int):String =  {
    k match {
      case k if k==0 => ">=0 and <1"
      case k if k==1 => ">=1 and <2"
      case k if k==2 => ">=2 and <3"
      case k if k==3 => ">=3 and <4"
      case _ => ">=4"
    }
  }


}
