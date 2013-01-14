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
import se.callista.loganalyzer.Count
import se.callista.loganalyzer.Success
import se.callista.loganalyzer.ClientError
import se.callista.loganalyzer.ServerError

@RunWith(classOf[JUnitRunner])
class LogServerStatusCountSuite(_system: ActorSystem) extends TestKit(_system) 
  with ImplicitSender with FunSuite with BeforeAndAfterAll with MustMatchers  {
  
  def this() = this(ActorSystem("LogServerTask2Suite", ConfigFactory.parseString("")))

  override def afterAll {
    system.shutdown()
  }
  
  val server = TestActorRef(new LogServer(testActor))
  
  test("count successful (status < 400) requests") {
    val successLog = AccessLog("127.0.0.1", new Date, "GET", "/", 200, 10)   
    server ! LogMessage("hostname", 1, successLog)
    expectMsg(Count(Success, 1))
  }
  
  test("count client errors (status >= 400 & < 500 ) requests") {
    val clientError = AccessLog("127.0.0.1", new Date, "GET", "/", 404, 10)   
    server ! LogMessage("hostname", 1, clientError)
    expectMsg(Count(ClientError, 1))
  }
  
  test("count server errors (status >= 500 ) requests") {
    val serverError = AccessLog("127.0.0.1", new Date, "GET", "/", 503, 10)   
    server ! LogMessage("hostname", 1, serverError)
    expectMsg(Count(ServerError, 1))    
  }
}