package se.callista.loganalyzer.agent

import scala.collection.mutable.Map
import se.callista.loganalyzer.HandleUnprocessedLogs;
import akka.actor.{Actor, ActorRef, ActorLogging}
import akka.util.duration._
import se.callista.loganalyzer.{AccessLog, ConfirmationMessage, LogMessage, HandleUnprocessedLogs}

class LogAgent(hostname: String, server: ActorRef) extends Actor with ActorLogging {

  def receive = {
    case None => //ersätt denna rad med en korrekt pattern matching
  }
  
}
