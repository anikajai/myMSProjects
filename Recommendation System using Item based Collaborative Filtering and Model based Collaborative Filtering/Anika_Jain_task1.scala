import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import java.io._
import org.apache.spark.rdd.RDD


object Anika_Jain_task1 {
    def main(args:Array[String])= {
      val sparkConf = new SparkConf().setMaster("local[2]").setAppName("HW3")
      val sc = new SparkContext(sparkConf)
      val bigSetWithHeader = sc.textFile(args(0))
      val smallSetWithHeader = sc.textFile(args(1))
      val bigSetHeader = bigSetWithHeader.first()
      val smallSetHeader = smallSetWithHeader.first()
      val bigSetWithoutHeader = bigSetWithHeader.filter(line => line != bigSetHeader)
      val smallSetWithoutHeader = smallSetWithHeader.filter(line => line != smallSetHeader)
      val bigSet = bigSetWithoutHeader.map(row=>row.split(",")).map(myList => ((myList(0), myList(1)), myList(2)))
      val smallSet = smallSetWithoutHeader.map(row=>row.split(","))
      val smallSetPart1 = smallSet.map(myList => ((myList(0), myList(1)), 1))
      val smallSetPart2 = smallSet.map(myList => (myList(0).toInt, myList(1).toInt))
      val smallSetPart3:RDD[((Int,Int),Double)] = smallSet.map(myList => ((myList(0).toInt, myList(1).toInt), 1.0))
      val lines = bigSet.leftOuterJoin(smallSetPart1).map{ case((k1, k2),(v1, v2))=> (k1.toInt,k2.toInt, v1.toDouble, v2)}
      val orginalTestRatings = lines.filter(x => x._4 == Some(1)).map{case (k1,k2, v1, v2) => ((k1,k2), v1)}
      val trainingData =  lines.filter(x => x._4 == None).map{case (k1,k2, v1, v2) => ((k1.toInt,k2.toInt), v1.toDouble)}
      val trainingData1  = trainingData.map{case ((k1,k2), v1) => Rating(k1,k2, v1)}
      var rank = 15
      var numIterations = 12
      //println("Small set has: " + smallSetPart2.count())

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

      val model = ALS.train(trainingData1, rank, numIterations, 0.1)
      val predictions:RDD[((Int,Int),Double)] = model.predict(smallSetPart2).map { case Rating(user, movie, rating) => ((user.toInt, movie.toInt), rating.toDouble)}
      val remainingPred = smallSetPart3.subtractByKey(predictions)
      val guessPred:RDD[((Int,Int),Double)] = remainingPred.map {
        case ((u,m), r) => (u,m)
      }.map{
        case (u,m) => {
          var r = 0.0
          if (userAverageMap.contains(u)) {
            r = userAverageMap(u)
          } else if (movieAverageMap.contains(m)) {
            r = movieAverageMap(m)
          }
          ((u,m),r)
        }
      }
      val predictionsFull = predictions.union(guessPred).sortBy(_._2).sortBy(_._1)
      //println("P " +predictions.count())
      val out = orginalTestRatings.join(predictionsFull).map{case ((k1,k2), (v1,v2)) => (k1.toInt, k2.toInt, Math.abs(v1-v2))}
      val accuracyLevels = out.map{ case (k1,k2,k3)=> (prepareLevels(k3),1)}.reduceByKey(_+_).sortByKey()
      val screenAccuracyDisplayedText:StringBuilder = new StringBuilder
      for((k,v) <- accuracyLevels.collect()) {
        screenAccuracyDisplayedText ++= k
        screenAccuracyDisplayedText ++= ":"
        screenAccuracyDisplayedText ++= v.toString
        screenAccuracyDisplayedText ++= "\n"
        //print(screenAccuracyDisplayedText.toString)
      }
     // print("why?" + screenAccuracyDisplayedText.mkString)
      val mse = out.map{case (k1,k2,err) =>
        err*err}.mean()
      val rmse = Math.sqrt(mse)
      screenAccuracyDisplayedText ++= "RMSE = "
      screenAccuracyDisplayedText ++= rmse.toString
      screenAccuracyDisplayedText ++= "\n"

      val screenOut = screenAccuracyDisplayedText.toString
      print(screenOut)

      val fileOut = predictionsFull.map{ case ((k1,k2), k3) => List(k1.toString, k2.toString, k3.toString)}
      val outputFile = new PrintWriter(new File("Anika_Jain_result_task1.txt"))
      try {
        outputFile.println("UserId,MovieId,Pred_rating")
        for(line <- fileOut.collect()) {
          outputFile.println(line.mkString(","))
        }
      } finally {
        outputFile.close
      }

    }

  def prepareLevels(line:Double):String =  {
    line match {
      case x if x >=0 && x<1 => ">=0 and <1"
      case x if x >=1 && x<2 => ">=1 and <2"
      case x if x >=2 && x<3 => ">=2 and <3"
      case x if x >=3 && x<4 => ">=3 and <4"
      case x if x >=4 => ">=4"
    }
  }

}
