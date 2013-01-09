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

  def receive = {
    case None => //ersätt denna rad med en korrekt pattern matching av objekt 
  }
  
}