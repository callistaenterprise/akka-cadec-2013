package se.callista.loganalyzer.server

import scala.collection.mutable.HashSet
import akka.actor.{Actor, ActorLogging, ActorRef}
import se.callista.loganalyzer.{Count, HttpStatus, LogMessage}

class StatusCounter(status: HttpStatus, presenter: ActorRef) extends Actor with ActorLogging {

  def receive = {
    case None => // replace this row 
  }
  
}