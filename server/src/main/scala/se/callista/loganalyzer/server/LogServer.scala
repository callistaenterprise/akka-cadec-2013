package se.callista.loganalyzer.server

import akka.actor._
import akka.actor.SupervisorStrategy.Restart
import se.callista.loganalyzer._

/**
 * UPPGIFT 1:
 * 1. Ta emot LogMessage objekt och skriv ut dem i loggen
 *    Tips: Loggning görs med "log.info(...)"
 * 
 * UPPGIFT 2:
 * 2. Skapa StatusCounter actors för varje typ av http-status
 * 3. Skicka rätt fel till rätt actors  (success < 400, clientFailure >= 400, serverFailure >= 500)
 * 
 * UPPGIFT 3:
 * 4. Skapa en databaseWorker
 * 5. Forwarda alla loggmeddelanden till databaseWorker actorn
 * 
 * UPPGIFT 4:
 * 6. Sätt upp en failover strategi att databaseWorker actorn ska startas om när DatabaseFailureException kastas
 */
class LogServer(presenter: ActorRef) extends Actor with ActorLogging {
  
  override val supervisorStrategy = OneForOneStrategy() {
    case d: DatabaseFailureException => Restart
  }
  
  val successCounter = context.actorOf(Props(new StatusCounter(Success, presenter)), "successCounter")
  val clientErrorCounter = context.actorOf(Props(new StatusCounter(ClientError, presenter)), "clientErrorCounter")
  val serverErrorCounter = context.actorOf(Props(new StatusCounter(ServerError, presenter)), "serverErrorCounter")
  
  val databaseWorker = context.actorOf(Props[DatabaseWorker], "databaseWorker")
  
  def receive = {
    case logMessage: LogMessage => {
      log.info("received: " + logMessage)
      count(logMessage)
      databaseWorker.forward(logMessage)
    }
  }
  
  def count(logMessage: LogMessage) = logMessage match {
    case LogMessage(_, _, a: AccessLog) if(a.statusCode == 200) => successCounter ! logMessage
    case LogMessage(_, _, a: AccessLog) if(a.statusCode >= 400 && a.statusCode < 500) => clientErrorCounter ! logMessage
    case LogMessage(_, _, a: AccessLog) if(a.statusCode >= 500) => serverErrorCounter ! logMessage
    case _ => // alla andra fall ska ignoreras
  }
  
}
