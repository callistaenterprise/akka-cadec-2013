package se.callista.loganalyzer.server

import scala.annotation.implicitNotFound
import scala.collection.mutable.HashMap
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import unfiltered.netty.websockets._
import unfiltered.request.{Path, Seg}
import unfiltered.response.{BadRequest, ResponseString}
import se.callista.loganalyzer.AccessLog
import java.util.Date

object LogServerApplication extends App {

  val config = ConfigFactory.load()
  lazy val indexFile = io.Source.fromInputStream(getClass.getResourceAsStream("/index.html")).mkString
  
  val system = ActorSystem("LogServerActorSystem", config)

  val activeWebsockets = HashMap[String, WebSocket]()
  
  val send = (msg: String) => activeWebsockets.foreach {
    case (_, s: WebSocket) => s.send(msg)
  }
  
  val presenter = system.actorOf(Props(new WebSocketPresenter(send)), "webSocket")
  
  val logServer = system.actorOf(Props(new LogServer(presenter)), "logServer")
   
  val db = UnstableDatabase
  
  lazy val nettyServer = unfiltered.netty.Http(8080)
    .handler(unfiltered.netty.websockets.Planify({
      case Path(Seg("socket" :: Nil))  => {
        case Open(s) => activeWebsockets += s.channel.getId.toString->s
        case Close(s) => activeWebsockets -= s.channel.getId.toString
        case Error(s, e) => activeWebsockets -= s.channel.getId.toString
        case Message(s, Text(msg)) => 
      }}).onPass(_.sendUpstream(_)))
    .handler(unfiltered.netty.cycle.Planify({
      case Path(Seg(Nil)) => try {
        ResponseString(indexFile)
      } catch { case e => BadRequest }
      case Path(Seg("logs" :: Nil)) => try {
        ResponseString(db.latestTwenty.mkString("\n"))
      } catch { case e => BadRequest }
      case _ => ResponseString("Couldn't handle request")
    }))

  nettyServer.run()

}
