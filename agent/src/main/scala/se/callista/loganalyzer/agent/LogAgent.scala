package se.callista.loganalyzer.agent

import scala.collection.mutable.Map
import se.callista.loganalyzer.HandleUnprocessedLogs;
import akka.actor.{Actor, ActorRef, ActorLogging}
import akka.util.duration._
import se.callista.loganalyzer.{AccessLog, ConfirmationMessage, LogMessage, HandleUnprocessedLogs}

class LogAgent(hostname: String, server: ActorRef) extends Actor with ActorLogging {

  var sequence = 0
  val logs = Map[Int, AccessLog]()

  val scheduler = context.system.scheduler.schedule(2 seconds, 2 seconds, self, HandleUnprocessedLogs)
  
  def receive = {
    case log: AccessLog => processLog(log)
    case ConfirmationMessage(id) => handleProcessedLog(id)
    case HandleUnprocessedLogs => handleUnprocessedLogs()
  }
  
  def processLog (log: AccessLog) {
    sequence += 1
    logs += sequence -> log
    sendLog(sequence, log) 
  }
  
  def sendLog(id: Int, log: AccessLog) {
    server ! LogMessage(hostname, id, log)
  }
  
  def handleProcessedLog(id: Int) {
    log.info("Processed log: {}", id)
    logs -= id
  }
  
  def handleUnprocessedLogs() {
    log.info("Resend unprocessed logs: {}", logs.keys.mkString(", "))
    logs.foreach { case (k, v) => sendLog(k, v) }
  }
  
}
