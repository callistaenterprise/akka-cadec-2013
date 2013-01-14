package se.callista.loganalyzer.server

import akka.actor.{Actor, ActorLogging}
import se.callista.loganalyzer.{ConfirmationMessage, LogMessage}

class DatabaseWorker extends Actor with ActorLogging {
  
  val database: Database = UnstableDatabase

  def receive = {
    case LogMessage(host, id, log) => {
      database.save(host, id, log)
      sender ! ConfirmationMessage(id)
    }
  }
}