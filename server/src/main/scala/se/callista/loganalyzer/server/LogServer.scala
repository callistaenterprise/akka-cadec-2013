package se.callista.loganalyzer.server

import akka.actor._
import akka.actor.SupervisorStrategy.Restart
import se.callista.loganalyzer._

class LogServer(presenter: ActorRef) extends Actor with ActorLogging {
  
  val successCounter = context.actorOf(Props(new StatusCounter(Success, presenter)), "successCounter")
  val clientErrorCounter = context.actorOf(Props(new StatusCounter(ClientError, presenter)), "clientErrorCounter")
  val serverErrorCounter = context.actorOf(Props(new StatusCounter(ServerError, presenter)), "serverErrorCounter")
  
  val databaseWorker = context.actorOf(Props[DatabaseWorker], "databaseWorker")
  
  def receive = {
    case logMessage: LogMessage => {
      log.info("received: {}", logMessage)
      count(logMessage)
      databaseWorker.forward(logMessage)
    }
  }
  
  def count(logMessage: LogMessage) = logMessage match {
    case LogMessage(_, _, a: AccessLog) => {
      if(a.statusCode < 400) successCounter ! logMessage
      else if(a.statusCode >= 500) serverErrorCounter ! logMessage
      else clientErrorCounter ! logMessage
    }
  }
}
