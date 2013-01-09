package se.callista.loganalyzer.server

import scala.util.Random
import akka.actor.{ActorSystem, Props}
import akka.util.duration._
import akka.testkit.{TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.junit.JUnitRunner
import se.callista.loganalyzer.{Success, AccessLog, LogMessage, Count}
import java.util.Date

@RunWith(classOf[JUnitRunner])
class StatusCounterIdempotentSuite(_system: ActorSystem) extends TestKit(_system) 
  with FunSuite with BeforeAndAfterAll {
  
  def this() = this(ActorSystem("StatusCounterIdempotentSuite", ConfigFactory.parseString("")))

  override def afterAll {
    system.shutdown()
  }
  
  test("extra: counting should be idempotent") {
    val accessLog = AccessLog("127.0.0.1", new Date, "GET", "/", Random.nextInt(400)+200, 10)
    
    val probe = TestProbe()
    val actor = system.actorOf(Props(new StatusCounter(Success, probe.ref)))
    
    actor ! LogMessage("hostname", 1, accessLog)
    probe.expectMsg(1 second, Count(Success, 1))
    actor ! LogMessage("hostname", 1, accessLog)
    probe.expectNoMsg(500 millis)
    actor ! LogMessage("hostname", 2, accessLog)
    probe.expectMsg(1 second, Count(Success, 2))
  }

}