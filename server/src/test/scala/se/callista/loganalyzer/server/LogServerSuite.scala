package se.callista.loganalyzer.server

import scala.util.Random
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem, UnhandledMessage}
import akka.testkit.ImplicitSender
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import akka.util.duration.intToDurationInt
import se.callista.loganalyzer.AccessLog
import se.callista.loganalyzer.LogMessage
import akka.testkit.TestProbe
import java.util.Date

@RunWith(classOf[JUnitRunner])
class LogServerSuite(_system: ActorSystem) extends TestKit(_system) 
  with ImplicitSender with FunSuite with BeforeAndAfterAll with MustMatchers  {
  
  def this() = this(ActorSystem("LogServerSuite", ConfigFactory.parseString("")))

  override def afterAll {
    system.shutdown()
  }
  
  test("handle LogMessage objects") {
    val id = 1
    val accessLog = AccessLog("127.0.0.1", new Date, "GET", "/", 200, 10)
    val logMessage = LogMessage("hostname", id, accessLog)

    system.eventStream.subscribe(testActor, classOf[UnhandledMessage])
    val ref = TestActorRef(new LogServer(TestProbe().ref))
    
    ref.receive(logMessage)
    expectNoMsg(500 millis)
    
  }
  
}