package se.callista.loganalyzer.agent

import akka.actor.{ActorRef, Actor}
import sun.security.krb5.internal.Ticket
import se.callista.loganalyzer.AccessLog
import util.Random
import java.text.SimpleDateFormat
import java.util.Date

case object Tick
class IpGenerator(numAdresses: Int) {
  val ipAddresses = List.fill(numAdresses)(genIp)

  def getIp = ipAddresses.drop(Random.nextInt(ipAddresses.size)).head

  private def genIp = {
    val octets = List.fill(4)(Random.nextInt(223) + 1)
    octets.mkString(".")
  }
}

class LogReaderSimulator(logAgent: ActorRef, noIpAddresses: Int) extends Actor {
  private val ipGen = new IpGenerator(noIpAddresses)

  def receive = {
    case Tick => logAgent ! nextLog
  }

  private val extensions = Map(
    "html" -> 40,
    "php" -> 30,
    "png" -> 15,
    "gif" -> 10,
    "css" -> 5
  )
  private val methods = Map("GET" -> 90, "POST" -> 10)
  private val responseCodes = Map(
    200 -> 92,
    404 -> 5,
    503 -> 3
  )

  private def getWeightedKey[T](map: Map[T,Int]) = {
    val sum = map.foldLeft(0){ case (s,(_,v)) => s + v}
    val rand = Random.nextInt(sum)

    def find[T](s: Int, map: Map[T,Int]): T = {
      if (s > rand) map.head._1
      else find(s + map.tail.head._2, map.tail)
    }
    find(map.head._2, map)
  }

  private def nextLog = AccessLog(
    ip = ipGen.getIp,
    timestamp = new Date,
    method = getWeightedKey(methods),
    path = "/test." + getWeightedKey(extensions),
    statusCode = getWeightedKey(responseCodes),
    size = Random.nextInt(8000)
  )
}
