import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import scala.math.BigDecimal
import java.io._

object task2 {
  def main(args:Array[String]) {
    val sparkConf = new SparkConf().setAppName("Task1App").setMaster("local[2]")
    val sc = new SparkContext(sparkConf)
    val user_text = sc.textFile(args(0)).map(line => line.split("::").toList)
    val filtered_user_list = user_text.map(myList => (myList(0), myList(1)))

    val rating_text = sc.textFile(args(1)).map(line => line.split("::"))
    val filtered_rating_list = rating_text.map(myList => (myList(0), (myList(1), myList(2))))

    val first_join = filtered_user_list.join(filtered_rating_list).map{ case (k, (u, (v,w))) => (v,(u,w)) }

    val movie_text = sc.textFile(args(2)).map(line => line.split("::")).map(myList => (myList(0), myList(2)))

   // movie_text.take(10).foreach(println)
    val filtered_movie_list2 =  movie_text
    //filtered_movie_list2.take(10).foreach(println)
    val final_join = filtered_movie_list2.join(first_join).map{ case (mid,(genre,(gender, rating))) => ((genre, gender), rating.toFloat)}
    val filtered_join = final_join.mapValues(v=>(v,1)).reduceByKey((a,b)=>(a._1 + b._1, a._2 + b._2))
    val result = filtered_join.mapValues(v=> BigDecimal(1.0*v._1/v._2).setScale(11, BigDecimal.RoundingMode.CEILING)).sortByKey()//.map{case ((k1,k2),v)=> Array(k1, k2, v).mkString(",")}

    val output = new PrintWriter(new File("anika_jain_result_task2.txt"), "UTF-8")

    try {
      for ( ((k1,k2),v) <- result.collect) {
        output.println(k1 + "," + k2 + "," + v.toDouble)
      }
    } finally {
      output.close
    }
  }
}
