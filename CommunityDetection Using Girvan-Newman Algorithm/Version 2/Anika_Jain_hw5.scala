import java.io.{File, PrintWriter}
import scala.collection.mutable._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import scala.collection.mutable
import org.apache.spark.graphx._

object Anika_Jain_hw5 {
  var adjMap:scala.collection.Map[Int, List[Int]] = new HashMap[Int, List[Int]]()

  case class QElem (parents: List[Int], betweenness:Double)

  def main(args:Array[String]) {
    val sparkConf = new SparkConf().setAppName("Task1App").setMaster("local[2]")
    val sc = new SparkContext(sparkConf)

    val bigSetWithHeader = sc.textFile(args(0))

    val bigSetHeader = bigSetWithHeader.first()
    val bigSetWithoutHeader = bigSetWithHeader.filter(line => line != bigSetHeader)
    val userMovies = bigSetWithoutHeader.map(row => row.split(",")).map{ case myList => (myList(0).toInt, List(myList(1).toInt))}.reduceByKey(_:::_).filter {
     case (user,m) => m.size >= 3
   }.map { case (u,m) => (u,m.toSet)}


    val betweenness:RDD[((Int,Int), Double)] = Betweenness(userMovies)

    val betweenness2 = betweenness.sortBy(_._1._2).sortBy(_._1._1)
    val outputFile = new PrintWriter(new File(args(2)))
    try {
      for(((u1,u2),v)<-betweenness2.collect()) {
        outputFile.println("("+u1+","+u2+","+v+")")
      }
    } finally {
      outputFile.close
    }

    val finalData:RDD[scala.collection.immutable.List[Int]] = Community(betweenness)
    val output = new PrintWriter(new File(args(1)))

    try {
      for (user <- finalData.collect){
        output.print(user.mkString("[",", ", "]"))
      }
    } finally {
      output.close
    }
  }

  def Community(betweenness:RDD[((Int,Int), Double)]):RDD[scala.collection.immutable.List[Int]] = {
    val edges1 = betweenness.map { case ((u1,u2), b) => (u1, u2, b)}
    val edges2 = betweenness.map { case ((u1,u2), b) => (u2, u1, b)}
    var edges = edges1.union(edges2)
    var edgesG = edges.map { case (u1,u2, b) => Edge(u1, u2, b)}

    var sortedBetweenness = betweenness.map {case ((u1,u2), b) => (b)}.distinct().collect().sorted.toList.toBuffer
    //println("Hi1")
    var edgeMap = edges.map {
      case (u1,u2, b) => ((u1,u2),1.0)
    }.collectAsMap()
    //println("Hi2")
    val graph = Graph.fromEdges(edgesG, "defaultProperty")

    var cc = graph.connectedComponents
    var vertexDegree = cc.inDegrees.map {case (v,value) => (v.toInt, value.toDouble)}.collectAsMap()

    var maxModularity = -1.0
    var loopCanGo = true
    while (loopCanGo) {
      var vertices = cc.vertices.collect()
      val mTwice = cc.edges.count()
      var modularity = 0.0
      vertices.foreach{
        case (v1,c1) => {
          vertices.foreach {
            case (v2,c2) => {
              if (c1 == c2) {
                modularity = modularity + edgeMap.getOrElse((v1.toInt,v2.toInt), 0.0) - (vertexDegree(v1.toInt) *vertexDegree(v2.toInt)*1.0)/mTwice*1.0
              }
            }
          }
        }
      }
      modularity = modularity/mTwice

      if (modularity < maxModularity) {
        loopCanGo = false // loop breaks here
      } else {
        maxModularity = modularity
        val maxEdge = sortedBetweenness(sortedBetweenness.size-1)
        sortedBetweenness.remove(sortedBetweenness.size-1)
        val removingEdges1 = edgesG.filter {
          case Edge(u1,u2, b) => b == maxEdge
        }.map {
          case Edge(u1,u2,b) => (u1.toInt,u2.toInt)
        }
        val removingEdges = removingEdges1.collect().toSet


        val removingVertices = removingEdges1.flatMap{
          case (u1,u2) => List(u1)
        }.collect().toList

        removingVertices.foreach{
          case x => {
            vertexDegree += (x -> (vertexDegree(x) -1))
          }
        }
        edgeMap = edgeMap.filter(t => (!(removingEdges contains t._1)))
        cc = cc.subgraph(epred = t => t.attr != maxEdge).connectedComponents()
      }

    }

    val finalData = cc.vertices.map { case (u1, cid) => (cid, List(u1.toInt))}.reduceByKey(_:::_).map {
      case (cid, lst) => lst.sorted
    }
    println("Max Modularity is: " + maxModularity)
    return finalData
  }

  def Betweenness(userMovies:RDD[(Int,scala.collection.immutable.Set[Int])]): RDD[((Int,Int),Double)] = {
    val userMoviesTemp = userMovies.map { case (user, movies) => (1,(user,movies))}
    val graphPairs = userMoviesTemp.join(userMoviesTemp).filter{
      case (dummy, ((user1, movies1), (user2, movies2))) => user1 < user2
    }.map {
      case (dummy, ((user1, movies1), (user2, movies2))) => (user1, user2, movies1.intersect(movies2))
    }.filter {
      case (user1, user2, movies) => movies.size >= 3
    }.map {
      case (user1, user2, movies) => (user1, user2)
    }

    val vertices:RDD[Int] = graphPairs.flatMap {
      case (u1,u2) => Set(u1,u2)
    }.distinct()


    val graphPairs1:RDD[(Int,List[Int])] = graphPairs.map {
      case (u1,u2) => (u1,List(u2))
    }.reduceByKey(_:::_)
    val graphPairs2:RDD[(Int,List[Int])] = graphPairs.map {
      case (u1,u2) => (u2,List(u1))
    }.reduceByKey(_:::_)

    adjMap = graphPairs1.union(graphPairs2).map {
      case (u1,u2) => (u1,u2)
    }.reduceByKey(_:::_).collectAsMap()


    val partialBetweenness = vertices.flatMap{
      case (user) => {
        calculateBetweenness(user)
      }
    }

    return partialBetweenness.reduceByKey(_+_).map{ case ((u1,u2), b) => ((u1,u2), (((b/2)*10.0).toInt/10.0))}
  }

  def  calculateBetweenness (user:Int):List[((Int,Int),Double)] = {

          var edges = new ListBuffer[((Int,Int),Double)]()
          var levelsMap: scala.collection.mutable.Map[Int, scala.collection.Map[Int, QElem]] = scala.collection.mutable.HashMap()
          var level = 0
          var elem = QElem(List(), 1)
          val exploredSet:scala.collection.mutable.Set[Int] = mutable.HashSet()
          levelsMap += (level->scala.collection.Map(user ->elem))
          var tempList:scala.collection.Map[Int, QElem] = levelsMap(level)
          exploredSet.add(user)
          while (!tempList.isEmpty) {
            level +=1
            val localExploredSet:scala.collection.mutable.Set[Int] = mutable.HashSet()
            for ((nodeName:Int, element) <- tempList) {
               val children:List[Int] = adjMap(nodeName)
                for (child <- children) {
                  if (!exploredSet.contains(child)) {
                    localExploredSet.add(child)
                    if (levelsMap contains level) {
                      var childLevelNodes:scala.collection.Map[Int,QElem] = levelsMap(level)
                      if (childLevelNodes contains(child)) {
                        childLevelNodes += (child -> QElem(childLevelNodes(child).parents :+ nodeName, childLevelNodes(child).betweenness))
                        levelsMap  += (level -> childLevelNodes)
                      } else {
                        childLevelNodes += (child -> QElem(List(nodeName), 1))
                        levelsMap  += (level -> childLevelNodes)
                      }
                    } else {
                      levelsMap += (level -> scala.collection.Map(child->QElem(List(nodeName), 1)))
                    }
                  }
                }
            }
            if (levelsMap contains level) {
              tempList = levelsMap(level)
            } else {
              tempList = scala.collection.Map[Int, QElem]()
            }
            for(e<-localExploredSet) {
              exploredSet.add(e)
            }
          }
          level = level-1
          while (level>0) {
            var levelNodes = levelsMap(level)
            for ((nodeName:Int, node) <- levelNodes) {
              val parents = node.parents
              var parentLevelNodes:scala.collection.Map[Int, QElem] = levelsMap(level-1)

              val distributionBetweenNess:Double = node.betweenness/parents.size
              for (parent:Int <- parents) {
                if (parent < nodeName) {
                  val edgeBtwness:Double = distributionBetweenNess
                  val x = ((parent, nodeName),edgeBtwness)
                  edges  += x
                } else {
                  val edgeBtwness:Double = distributionBetweenNess
                  val x = ((nodeName, parent), edgeBtwness)
                  edges  += x
                }
                val parentNode = parentLevelNodes(parent)
                val newBtwnness = parentNode.betweenness + distributionBetweenNess
                parentLevelNodes += (parent -> QElem(parentNode.parents, newBtwnness))
                levelsMap += (level-1 -> parentLevelNodes)
              }
            }
            level -= 1
          }
          edges.toList
  }
}
