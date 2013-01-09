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
  
  def receive = {
    case logMessage: LogMessage => log.info("received: " + logMessage)
  }
  
}
