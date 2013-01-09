package se.callista.loganalyzer.server

import scala.util.Random
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestActorRef, TestProbe}
import akka.util.duration._
import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.junit.JUnitRunner
import se.callista.loganalyzer.{AccessLog, ConfirmationMessage, LogMessage}
import org.scalatest.matchers.MustMatchers
import java.util.Date

@RunWith(classOf[JUnitRunner])
class DatabaseWorkerSuite(_system: ActorSystem) extends TestKit(_system) 
  with ImplicitSender with FunSuite with BeforeAndAfterAll with MustMatchers  {
  
  def this() = this(ActorSystem("DatabaseWorkerSuite", ConfigFactory.parseString("")))

  override def afterAll {
    system.shutdown()
  }
  
  test("save log to database and return a confirmation message to sender") {
    val id = 1
    val accessLog = AccessLog("127.0.0.1", new Date, "GET", "/", Random.nextInt(400)+200, 10)
    val logMessage = LogMessage("hostname", id, accessLog)

    val db = StableDatabase
    
    val actorRef = TestActorRef(new DatabaseWorker { override val database = db } )
    val actor = actorRef.underlyingActor
    
    actorRef ! logMessage
    
    expectMsg(1 second, ConfirmationMessage(id))
    db.findAll.size must be (1)
  }
  
}