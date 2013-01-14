package se.callista.loganalyzer.server

import scala.collection.mutable.HashSet
import akka.actor.{Actor, ActorLogging, ActorRef}
import se.callista.loganalyzer.{Count, HttpStatus, LogMessage}

class StatusCounter(
    val status: HttpStatus, // HTTP Status (Success, ClientError or ServerError)
    val presenter: ActorRef // actor reference to the presenter 	
  ) extends Actor with ActorLogging {

  def receive = {
    case None => // replace this row 
  }
  
}