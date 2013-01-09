package se.callista.loganalyzer.agent

import com.typesafe.config._
import akka.actor.{ActorSystem, Props}
import akka.util.Duration
import java.util.concurrent.TimeUnit
import scala.util.Random
import se.callista.loganalyzer.AccessLog
import akka.util.duration._

object LogAgentApplication extends App {
  
  val config = ConfigFactory.load()

  val hostname = config.getString("akka.remote.netty.hostname")
  
  val system = ActorSystem("LogAgentActorSystem", config)
  val server = system.actorFor("akka://LogServerActorSystem@127.0.0.1:2553/user/logServer")

  val agent = system.actorOf(Props(new LogAgent(hostname, server)), "logAgent")

  val reader = system.actorOf(Props(new LogReaderSimulator(agent, 25)), "logReader")
  
  val scheduler = system.scheduler.schedule(1 seconds, 1 seconds, reader, Tick)

}

