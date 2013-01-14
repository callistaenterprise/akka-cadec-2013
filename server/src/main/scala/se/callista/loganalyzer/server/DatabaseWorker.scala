package se.callista.loganalyzer.server

import akka.actor.{Actor, ActorLogging}
import se.callista.loganalyzer.{ConfirmationMessage, LogMessage}

class DatabaseWorker extends Actor with ActorLogging {
  
  val database: Database = UnstableDatabase

  def receive = {
    case None => //ersätt denna rad med en korrekt pattern matching
  }
  
}