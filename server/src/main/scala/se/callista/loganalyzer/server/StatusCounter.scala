package se.callista.loganalyzer.server

import scala.collection.mutable.HashSet
import akka.actor.{Actor, ActorLogging, ActorRef}
import se.callista.loganalyzer.{Count, HttpStatus, LogMessage}

/**
 * UPPGIFT 2:
 * 1. Ta emot LogMessage objekt
 * 2. Räkna upp med ett varje gång en logg kommer in
 * 3. Skapa ett Count-objekt och skicka till presenter-actorn
 * 4. Verifiera flödet på http://localhost:8080
 * 
 * UPPGIFT 5:
 * 5. Gör uppräkningen idempotent. Räkna alltså inte loggar med samma id från samma host två gånger om.
 */
class StatusCounter(status: HttpStatus, presenter: ActorRef) extends Actor with ActorLogging {

  /* Simple solution: */
//  var count = 0
//  
//  def receive = {
//    case LogMessage(host, id, log) => {
//      count = count + 1
//      val c = new Count(status, count)
//      presenter ! c
//    }
//  }
//  
  
  // Idempotent solution:
  val ids = HashSet[String]()
  var count = 0
  
  def receive = {
    case LogMessage(host, id, log) => {
      if(!ids.contains(host + ":" + id)){
        ids += host + ":" + id
        count = count + 1
        val c = new Count(status, count)
        presenter ! c
      }
      
    }
  }
}