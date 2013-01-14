package se.callista.loganalyzer.server

import scala.collection.mutable.HashSet
import akka.actor.{Actor, ActorLogging, ActorRef}
import se.callista.loganalyzer.{Count, HttpStatus, LogMessage}

class StatusCounter(
    val status: HttpStatus, // HTTP Status (Success, ClientError or ServerError)
    val presenter: ActorRef // actor reference to the presenter 	
  ) extends Actor with ActorLogging {

  /* Simple solution: */
//  var count = 0
//  
//  def receive = {
//    case LogMessage(host, id, log) => {
//      count = count + 1
//      val c = new Count(status, count)
//      presenter ! c
//    }
//  }
//  
  
  // Idempotent solution:
  val ids = HashSet[String]()
  var count = 0
  
  def receive = {
    case LogMessage(host, id, log) => {
      if(!ids.contains(host + ":" + id)){
        ids += host + ":" + id
        count = count + 1
        val c = new Count(status, count)
        presenter ! c
      } 
    }
  }
}