package se.callista.loganalyzer.server

import scala.collection.mutable.HashSet
import akka.actor.{Actor, ActorLogging, ActorRef}
import se.callista.loganalyzer.{Count, HttpStatus, LogMessage}

class StatusCounter(status: HttpStatus, presenter: ActorRef) extends Actor with ActorLogging {

  var count = 0
  
  def receive = {
    case LogMessage(host, id, accessLog) => {
      count = count + 1
      presenter ! Count(status, count)
    }
  }
}