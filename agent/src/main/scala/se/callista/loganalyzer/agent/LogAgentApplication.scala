package se.callista.loganalyzer.agent

import com.typesafe.config._
import akka.actor.{ActorSystem, Props}
import akka.util.Duration
import java.util.concurrent.TimeUnit
import scala.util.Random
import se.callista.loganalyzer.AccessLog
import akka.util.duration._

object LogAgentApplication extends App {
  
  // ladda in konfiguration (agent/src/main/resources/application.conf)
  val config = ConfigFactory.load()

  // sätt hostname till det hostname som anges i konfigurationen för Akka Remote  
  val hostname = config.getString("akka.remote.netty.hostname")
  
  // skapa och starta ett nytt actor system för agenten
  val system = ActorSystem("LogAgentActorSystem", config)
  
  // peka ut server-actorn som kör på ett annat remote actor system  
  val server = system.actorFor("akka://LogServerActorSystem@127.0.0.1:2553/user/logServer")

  // skapa och starta LogAgent-actorn
  val agent = system.actorOf(Props(new LogAgent(hostname, server)), "logAgent")

  // skapa och starta reader-actorn som läser loggfilen 
  // (läsningen simuleras och en slumpad logg skapas en gång i sekunden)
  val reader = system.actorOf(Props(new LogReaderSimulator(agent, 25)), "logReader")
  val scheduler = system.scheduler.schedule(1 seconds, 1 seconds, reader, Tick)

}

