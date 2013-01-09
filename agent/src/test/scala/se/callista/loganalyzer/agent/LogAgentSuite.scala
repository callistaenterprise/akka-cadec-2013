package se.callista.loganalyzer.agent

import scala.util.Random
import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import akka.util.duration._
import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.junit.JUnitRunner
import se.callista.loganalyzer.{AccessLog, LogMessage}
import akka.testkit.TestActorRef
import akka.actor.UnhandledMessage
import java.util.Date

@RunWith(classOf[JUnitRunner])
class LogAgentSuite(_system: ActorSystem) extends TestKit(_system) 
  with FunSuite with BeforeAndAfterAll {
  
  def this() = this(ActorSystem("LogAgentSuite", ConfigFactory.parseString("")))

  override def afterAll {
    system.shutdown()
  }
  
  
  test("handle AccessLog objects") {
    val id = 1
    val accessLog = AccessLog("127.0.0.1", new Date(), "GET", "/", Random.nextInt(400)+200, 10)

    system.eventStream.subscribe(testActor, classOf[UnhandledMessage])
    val ref = TestActorRef(new LogAgent("host", TestProbe().ref))
    
    ref.receive(accessLog)
    expectNoMsg(500 millis)
  }
  
  test("create a log message and send it to the log server") {
    val accessLog = AccessLog("127.0.0.1", new Date(), "GET", "/", Random.nextInt(400)+200, 10)
    
    val probe = TestProbe()
    val actor = system.actorOf(Props(new LogAgent("host", probe.ref)))
    actor ! accessLog
    probe.expectMsgPF(1 second) {
      case LogMessage(_, _, accessLog) => // success
      case x => fail("Wrong type. Should be LogMessage, but was: " + x)
    }
  }
  
  test("set correct sequence numbers (1..n) on log messages") {
    val accessLog = AccessLog("127.0.0.1", new Date(), "GET", "/", Random.nextInt(400)+200, 10)
    
    val probe = TestProbe()
    val actor = system.actorOf(Props(new LogAgent("host", probe.ref)))
    actor ! accessLog
    probe.expectMsgPF(500 millis) {
      case LogMessage(_, 1, accessLog) => // success
      case LogMessage(_, x, _) => fail("the id should be 1 on the first log message, but was " + x)
      case x => fail("Wrong type. Should be LogMessage, but was: " + x)
    }
    actor ! accessLog
    probe.expectMsgPF(500 millis) {
      case LogMessage(_, 2, accessLog) => // success
      case LogMessage(_, x, _) => fail("the id should be 2 on the second log message, but was " + x)
      case x => fail("Wrong type. Should be LogMessage, but was: " + x)
    }
  }

}