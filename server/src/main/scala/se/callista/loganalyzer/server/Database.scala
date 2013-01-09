package se.callista.loganalyzer.server

import se.callista.loganalyzer.AccessLog
import scala.collection.immutable.TreeMap

abstract class Database {
  
  val logs = scala.collection.mutable.Map[String, AccessLog]()
  var timeline = TreeMap[Long, List[String]]()
  
  def save(host: String, id: Int, log: AccessLog) = {
    val key = host+":"+id
    if (!logs.contains(key)) {
      logs += key -> log
      val time = log.timestamp.getTime       
      timeline += (time -> (key :: timeline.getOrElse(time, Nil )))
    }
  }

  def findAll: Seq[AccessLog] = logs.values.toSeq
  
  def latestTwenty: Seq[AccessLog] = {
   (for( (_,keys) <- timeline.takeRight(20); key <- keys) yield logs(key) ).toList.take(20)
  }
} 

object StableDatabase extends Database 

object UnstableDatabase extends Database {
  val randomizer = new scala.util.Random
  
  override def save(host: String, id: Int, log: AccessLog) = {
    if (id % (randomizer.nextInt(17) + 17) == 0) throw new DatabaseFailureException("Database error")
    super.save(host, id, log)
  }
}

class DatabaseFailureException(msg: String) extends Exception
