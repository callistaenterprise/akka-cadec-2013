package se.callista.loganalyzer.agent

import org.apache.commons.io.input.{TailerListenerAdapter, Tailer, TailerListener}
import java.util.concurrent.{Executors, Executor}
import java.io.File
import akka.actor._
import se.callista.loganalyzer.AccessLog
import java.text.SimpleDateFormat

case object Stop
class LogReader(logAgent: ActorRef, file: File) extends Actor with ActorLogging {
  val executor = context.system.dispatchers.lookup("file-reader-dispatcher")
  val tailer = new Tailer(file, new LogfileTailListener(logAgent));

  executor.execute(tailer)

  override def postStop = {
    log.info("Stopping tailer")
    tailer.stop
  }

  def receive = {
    case _ =>
  }
}

class LogfileTailListener(logActor: ActorRef) extends TailerListenerAdapter {
  val regexp = """^([\d.:]+) (\S+) (\S+) \[([\w:/]+\s[+\-]\d{4})\] \"(\w+) ([^ ]+) ([^ ]+)\" (\d{3}) (\d+|-) \"([^\"]+)\" \"([^\"]+)\"""".r
  val formatter = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z")
  override def handle(line: String) = {
    def parseSize(size: String) = if(size=="-") "0" else size
    def getRow = line match {
      case regexp(ip, _, _, time, method, path, _, status, size, _, _) => AccessLog(ip, formatter.parse(time), method, path ,status.toInt, parseSize(size).toInt)
      case _ => throw new IllegalArgumentException("Could not parse row: " + line)
    }
    logActor ! getRow
  }
  override def handle(e: Exception) = print(e)
}