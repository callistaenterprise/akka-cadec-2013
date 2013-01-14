package se.callista.loganalyzer.agent

import scala.collection.mutable.Map
import se.callista.loganalyzer.HandleUnprocessedLogs;
import akka.actor.{Actor, ActorRef, ActorLogging}
import akka.util.duration._
import se.callista.loganalyzer.{AccessLog, ConfirmationMessage, LogMessage, HandleUnprocessedLogs}

class LogAgent(hostname: String, server: ActorRef) extends Actor with ActorLogging {

  var sequence = 0
  
  def receive = {
    case log: AccessLog => processLog(log) 
  }
  
  def processLog (log: AccessLog) {
    sequence += 1
    val id = sequence 
    
    sendLog(id, log) 
  }
  
  def sendLog(id: Int, log: AccessLog) {
    server ! LogMessage(hostname, id, log)
  }
}
