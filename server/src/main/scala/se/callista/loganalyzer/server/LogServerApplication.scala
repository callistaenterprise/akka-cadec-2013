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

  // ladda in konfiguration (server/src/main/resources/application.conf)
  val config = ConfigFactory.load()
  
  // peka ut index-fil
  lazy val indexFile = io.Source.fromInputStream(getClass.getResourceAsStream("/index.html")).mkString
  
  // skapa och starta ett nytt actor system för servern
  val system = ActorSystem("LogServerActorSystem", config)

  // skapa och starta en websocket-actor
  val presenter = system.actorOf(Props[WebSocketPresenter], "webSocket")
  
  // skapa och starta LogServer-actorn
  val logServer = system.actorOf(Props(new LogServer(presenter)), "logServer")
   
  // peka ut "databasen"
  val db = UnstableDatabase
  
  // skapa och starta en webbserver på port 8080
  lazy val nettyServer = unfiltered.netty.Http(8080)
    .handler(unfiltered.netty.websockets.Planify({
      case Path(Seg("socket" :: Nil))  => {
        case Open(s) => presenter ! StartSocket(s.channel.getId.toString, s)
        case Close(s) => presenter ! StopSocket(s.channel.getId.toString)
        case Error(s, e) => presenter ! StopSocket(s.channel.getId.toString)
        case Message(s, Text(msg)) => 
      }}).onPass(_.sendUpstream(_)))
    .handler(unfiltered.netty.cycle.Planify({
      case Path(Seg(Nil)) => try {
        ResponseString(indexFile)
      } catch { case e => BadRequest }
      case Path(Seg("logs" :: Nil)) => try {
        ResponseString("LATEST 20 LOGS:\n"+db.latestTwenty.mkString("\n"))
      } catch { case e => BadRequest }
      case _ => ResponseString("Couldn't handle request")
    }))
  nettyServer.run()

}

