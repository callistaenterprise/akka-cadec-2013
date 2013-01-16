package se.callista.loganalyzer.server

import scala.util.Random
import akka.actor.{ActorSystem, Props}
import akka.util.duration._
import akka.testkit.{TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.junit.JUnitRunner
import se.callista.loganalyzer._
import java.util.Date
import akka.testkit.TestActorRef

@RunWith(classOf[JUnitRunner])
class StatusCounterSuite(_system: ActorSystem) extends TestKit(_system) 
  with FunSuite with BeforeAndAfterAll {
  
  def this() = this(ActorSystem("StatusCounterSuite", ConfigFactory.parseString("")))

  override def afterAll {
    system.shutdown()
  }
  
  test("StatusCounter should count logs and send value to presenter when a log message arrives") {
    val accessLog = AccessLog("127.0.0.1", new Date, "GET", "/", 200, 10)
    
    val probe = TestProbe()
    val actor = TestActorRef(new StatusCounter(Success, probe.ref))
    
    actor ! LogMessage("hostname", 1, accessLog)
    probe.expectMsg(1 second, Count(Success, 1)) 
    actor ! LogMessage("hostname", 2, accessLog)
    probe.expectMsg(1 second, Count(Success, 2))
  }
  
  val server = TestActorRef(new LogServer(testActor))
  
  test("LogServer should route successful (status < 400) requests") {
    val successLog = AccessLog("127.0.0.1", new Date, "GET", "/", 200, 10)   
    server ! LogMessage("hostname", 1, successLog)
    expectMsg(Count(Success, 1))
  }
  
  test("LogServer should route client errors (status >= 400 & < 500)") {
    val clientError = AccessLog("127.0.0.1", new Date, "GET", "/", 404, 10)   
    server ! LogMessage("hostname", 1, clientError)
    expectMsg(Count(ClientError, 1))
  }
  
  test("LogServer should route server errors (status >= 500)") {
    val serverError = AccessLog("127.0.0.1", new Date, "GET", "/", 503, 10)   
    server ! LogMessage("hostname", 1, serverError)
    expectMsg(Count(ServerError, 1))    
  }
}