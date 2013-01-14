package se.callista.loganalyzer.server

import akka.actor.{Actor, ActorLogging}
import se.callista.loganalyzer.{ConfirmationMessage, LogMessage}

class DatabaseWorker extends Actor with ActorLogging {
  
  val database: Database = UnstableDatabase

  def receive = {
    case None => // replace this row
  }
  
}