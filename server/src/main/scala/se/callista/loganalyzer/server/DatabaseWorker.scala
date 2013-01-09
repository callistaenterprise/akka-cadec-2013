package se.callista.loganalyzer.server

import akka.actor.{Actor, ActorLogging}
import se.callista.loganalyzer.{ConfirmationMessage, LogMessage}

/**
 * UPPGIFT 3:
 * 1. Ta emot LogMessage objekt
 * 2. Spara logg-meddelanden(LogMessage) till databasen
 * 3. Skicka tillbaks ett bekräftelsemeddelande(ConfirmationMessage) till 
 *    actorn som skickade meddelandet
 *    Tips: "sender" ger en referens till den actor som från början skickade meddelandet 
 * 
 */
class DatabaseWorker extends Actor with ActorLogging {
  
  val database: Database = UnstableDatabase

  def receive = {
    case None => //ersätt denna rad med en korrekt pattern matching
  }
  
}