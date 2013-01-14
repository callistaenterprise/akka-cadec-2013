package se.callista.loganalyzer.server

import akka.actor._
import akka.actor.SupervisorStrategy.Restart
import se.callista.loganalyzer._

class LogServer(presenter: ActorRef) extends Actor with ActorLogging {
  
  def receive = {
    case logMessage: LogMessage => {
      log.info("received: " + logMessage)
    }
  }
  
}
