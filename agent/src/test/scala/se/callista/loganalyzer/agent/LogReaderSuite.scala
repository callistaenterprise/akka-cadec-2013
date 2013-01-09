package se.callista.loganalyzer.agent

import java.io.File
import akka.testkit.{TestProbe, ImplicitSender, TestKit}
import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.scalatest.{FunSuite, BeforeAndAfterAll, WordSpec}
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import se.callista.loganalyzer.AccessLog
import java.text.SimpleDateFormat

@RunWith(classOf[JUnitRunner])
class LogReaderSuite(_system: ActorSystem) extends TestKit(_system)
with FunSuite with BeforeAndAfterAll {

  def this() = this(ActorSystem("LogReaderSuite", ConfigFactory.parseString(
    """
      file-reader-dispatcher {
        executor = thread-pool-executor
        type = PinnedDispatcher
      }
    """)))

  override def afterAll {
    system.shutdown()
  }

  test("read two rows in log file") {

    val probe = TestProbe()
    val file = new File("agent/src/test/resources/test-file.log")

    system.dispatchers
    val logReader = system.actorOf(Props(new LogReader(probe.ref, file)), "logreader")

    val timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z").parse("10/Apr/2007:10:54:51 +0300")
    
    probe.expectMsg(AccessLog("217.0.22.3",timestamp,"GET","/",200,34))
    probe.expectMsg(AccessLog("217.0.22.3",timestamp,"GET","/favicon.ico",200,11514))
  }
}