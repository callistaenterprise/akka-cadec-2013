package se.callista.loganalyzer.agent

import scala.collection.mutable.Map
import se.callista.loganalyzer.HandleUnprocessedLogs;
import akka.actor.{Actor, ActorRef, ActorLogging}
import akka.util.duration._
import se.callista.loganalyzer.{AccessLog, ConfirmationMessage, LogMessage, HandleUnprocessedLogs}

/**
 * UPPGIFT 1:
 * 1. Hantera inkommande AccessLog-objekt
 * 1. Skapa ett logg-meddelande(LogMessage) för ett inkommande AccessLog
 * 2. Sätt ett unikt löpnummer (id) på meddelandet, räkna från 1
 *    Tips: 
 *      - Då meddelanden hanteras seriellt i en actor behöver man inte
 *        oroa sig för att göra variabler trådsäkra, de kan alltså vara "mutable".
 * 3. Skicka logg-meddelandet till servern
 * 
 * UPPGIFT 4:
 * 4. Kontrollera om servern tagit emot och hanterat logg-meddelandet. Om inte,
 *    skicka om det efter två sekunder.
 *    Tips:
 *      - En scheduler kan användas för att actorn ska skicka ett meddelande 
 *        till sig själv inom ett visst tidsintervall. 
 *        T.ex "context.system.scheduler.schedule(2 seconds, 2 seconds, self, "meddelande")"
 *      - En mutable map finns under scala.collection.mutable.Map:
 *          Lägg till element: map += key -> value
 *          Ta bort element: map -= key
 *      - För att köra en metod på alla element i en lista kan foreach användas. 
 *        T.ex "collection.foreach { x -> println(x) }"
 * 
 */
class LogAgent(host: String, server: ActorRef) extends Actor with ActorLogging {

  var sequence = 0
  val logs = Map[Int, AccessLog]()

  val scheduler = context.system.scheduler.schedule(2 seconds, 2 seconds, self, HandleUnprocessedLogs)
  
  def receive = {
    case log: AccessLog => processLog(log)
    case ConfirmationMessage(id) => handleProcessedLog(id)
    case HandleUnprocessedLogs => handleUnprocessedLogs()
  }
  
  def processLog (log: AccessLog) {
    sequence = sequence + 1
    val id = sequence 
    logs += id -> log
    
    server ! LogMessage(host, id, log) 
  }
  
  def handleProcessedLog(id: Int) {
    log.info("Processed log: {}", id)
    logs -= id
  }
  
  def handleUnprocessedLogs() {
    log.info("Resend unprocessed logs: {}", logs.keys.mkString(", "))
    logs.keys.foreach { k => server ! LogMessage(host, k, logs(k)) }
    // alt2: logs.foreach { case (k, v) => server ! LogMessage(host, k, v) }
    // alt3: for ((k, v) <- logs) { server ! LogMessage(host, k, v) }
  }
  
}
