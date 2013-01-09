package se.callista.loganalyzer.server

import akka.actor.Actor
import se.callista.loganalyzer.Count

class WebSocketPresenter (send: String => Unit) extends Actor {

  def receive = {
    case c: Count => send(c.toJson)
  }
  
}