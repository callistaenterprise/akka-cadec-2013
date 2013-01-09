package se.callista.loganalyzer.agent

import akka.util.duration._
import akka.testkit.{TestProbe, TestKit}
import akka.actor.{Props, ActorSystem}
import org.scalatest.{FunSuite, BeforeAndAfterAll}
import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import se.callista.loganalyzer.AccessLog

@RunWith(classOf[JUnitRunner])
class LogReaderSimulatorSuite(_system: ActorSystem) extends TestKit(_system)
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

  def isValidIp(ip: String) = {
    ip.matches("""\d+\.\d+\.\d+\.\d+""") && ip.split("""\.""").map(_.toInt).forall(n=> n>0 && n<225)
  }

  test("generate 5000 ip addresses") {
    val gen = new IpGenerator(5000).ipAddresses

    assert(gen.size == 5000)
    gen.foreach{ ip =>
      assert(isValidIp(ip), ip)
    }
  }


  test("generation 1000 AccessLogs") {
    val probe = TestProbe()
    val simulator = system.actorOf(Props(new LogReaderSimulator(probe.ref, 25)))

    for(i <- 0 to 1000) {
      simulator ! Tick
      probe.expectMsgPF(1 second) {
        case a @ AccessLog(ip,time,method,path,statusCode,size) => {
          assert(isValidIp(ip), ip)
          assert(time != null, time)
        }
      }
    }
  }
}