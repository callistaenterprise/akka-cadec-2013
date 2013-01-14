package se.callista.loganalyzer.server

import scala.collection.mutable.Map
import akka.actor.Actor
import se.callista.loganalyzer.{Count, HttpStatus}
import unfiltered.netty.websockets.WebSocket

/**
 * Skickar ut Count-objekt till aktiva websockets
 */
class WebSocketPresenter extends Actor {

  val counts = Map[HttpStatus, Count]()
  
  val sockets = Map[String, WebSocket]()
  
  def receive = {
    case c: Count => {
      counts += c.status -> c
      sockets.foreach{ case (_, s) => s.send(c.toJson)}
    }
    case StartSocket(id, s) => {
      sockets += id -> s
      counts.foreach{ case (_, c) => s.send(c.toJson)}
    }
    case StopSocket(id) => sockets -= id 
  }
  
}

case class StartSocket (id: String, webSocket: WebSocket) 

case class StopSocket (id: String)