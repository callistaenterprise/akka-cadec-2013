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
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class LogAgentSuite(_system: ActorSystem) extends TestKit(_system) 
  with FunSuite with BeforeAndAfterAll with MustMatchers {
  
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
  
  test("set correct sequence numbers (x, x+1, x+2, .., x+n) on log messages") {
    val accessLog = AccessLog("127.0.0.1", new Date(), "GET", "/", Random.nextInt(400)+200, 10)
    
    val probe = TestProbe()
    val actor = system.actorOf(Props(new LogAgent("host", probe.ref)))
    actor ! accessLog
    val first = probe.expectMsgPF(500 millis) {
      case LogMessage(_, x, a: AccessLog) => x
      case x => fail("Wrong type. Should be LogMessage, but was: " + x)
    }
    actor ! accessLog
    val second = probe.expectMsgPF(500 millis) {
      case LogMessage(_, x, a: AccessLog) => x
      case x => fail("Wrong type. Should be LogMessage, but was: " + x)
    }
    
    second must be (first+1)
  }

}