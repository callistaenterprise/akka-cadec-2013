package se.callista.loganalyzer.agent

import scala.util.Random
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.UnhandledMessage
import akka.testkit.TestKit
import akka.testkit.TestProbe
import akka.util.duration._
import se.callista.loganalyzer.AccessLog
import se.callista.loganalyzer.LogMessage
import se.callista.loganalyzer.ConfirmationMessage
import java.util.Date

@RunWith(classOf[JUnitRunner])
class LogAgentResendSuite(_system: ActorSystem) extends TestKit(_system) 
  with FunSuite with BeforeAndAfterAll {
  
  def this() = this(ActorSystem("LogAgentSuite", ConfigFactory.parseString("")))

  override def afterAll {
    system.shutdown()
  }
  
  test("resend messages that are not handled the log server") {
    val accessLog = AccessLog("127.0.0.1", new Date(), "GET", "/", Random.nextInt(400)+200, 10)
    
    val probe = TestProbe()
    val actor = system.actorOf(Props(new LogAgent("host", probe.ref)))
    actor ! accessLog
    probe.expectMsgPF(1 second) {
      case LogMessage(_, 1, accessLog) => // success
      case x => fail("Wrong type. Should be LogMessage, but was: " + x)
    }
    probe.expectMsgPF(5 seconds) {
      case LogMessage(_, 1, accessLog) => // success
      case x => fail("Wrong type. Should be LogMessage, but was: " + x)
    }
  }
  
  test("don't resend messages that are not handled the log server") {
    val accessLog = AccessLog("127.0.0.1", new Date(), "GET", "/", Random.nextInt(400)+200, 10)
    
    val probe = TestProbe()
    val actor = system.actorOf(Props(new LogAgent("host", probe.ref)))
    actor ! accessLog
    probe.expectMsgPF(1 second) {
      case LogMessage(_, 1, accessLog) => // success
      case x => fail("Wrong type. Should be LogMessage, but was: " + x)
    }
    actor ! ConfirmationMessage(1)
    probe.expectNoMsg(5 seconds) 
  }

}