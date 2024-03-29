Cadec Akka Tutorial 2013
====================

I denna tutorial kommer vi bygga ett enkelt system för central analys av loggar från webbservrar. Tanken är att man har en agent på varje webbserver som ligger och läser av den sk. [access-loggen](http://httpd.apache.org/docs/2.2/logs.html#accesslog) i Apache. Denna agent skickar sedan vidare dessa loggar till en central server som lagrar loggarna och gör realtidsanalys. All kommunikation som sker asynkront mellan agenterna och servern.

Tutorial är uppbyggd så att infrastukturen finns på plats, men logik måste skapas för de actors som ska användas.

Instruktioner för att sätta upp en utvecklingsmiljö finns [här](https://github.com/callistaenterprise/akka-cadec-2013/wiki/Installationsanvisningar).

[Lathund för Scala och Akka](https://github.com/callistaenterprise/akka-cadec-2013/wiki/Scala-och-Akka-101)

Uppgift 1: Skicka logg-meddelanden från en agent till en server
---------------------

Första uppgiften går ut på att få upp ett flöde där en agent på en webbserver scannar loggar och skickar logg-objekt vidare till en server. Med hjälp av Akka kan detta ske helt utan att agent-actorn behöver veta var servern befinner sig utan agenten har bara en referens (ActorRef) till server-actorn.

### Uppdatera LogServer-actorn

1.  Ta emot LogMessage-objekt
2.  Skriv ut en logg om att objektet är mottaget (loggning görs med `log.info(...)`)

LogServer-actorn finns under: [server/src/main/scala/se/callista/loganalyzer/server/LogServer.scala](https://github.com/callistaenterprise/akka-cadec-2013/blob/master/server/src/main/scala/se/callista/loganalyzer/server/LogServer.scala)

Använd följande kommando för att verifiera att LogServer tar emot LogMessage-objekt:
`sbt 'server/test-only se.callista.loganalyzer.server.LogServerSuite'`

[*Exempel på lösning*](https://github.com/callistaenterprise/akka-cadec-2013/blob/task1/server/src/main/scala/se/callista/loganalyzer/server/LogServer.scala)

### Start-scripts

#### Generera startscripts

##### Windows
Startskripten för Windows finns i rot-katalogen för projektet med namnen `start_server.bat` och `start_agent.bat` men behöver editeras. Uppdatera USER_HOME och PROJECT_HOME, så att de pekar  på din hemkatalog, resp var du har checkat ut projektet

##### Linux/Mac
Kör kommandot: `sbt start-script` och scripten `server/target/start` och `agent/target/start` ska ha genererats.

#### Starta server och agent

Kompilera koden med kommandot: `sbt compile` 

Starta sedan servern i ett terminal-fönster med kommandot: `server/target/start` för Linux/Mac och `start_server.bat` för Windows.

Starta sedan agenten i ett annat terminal-fönster med kommandot: `agent/target/start` för Linux/Mac och `start_agent.bat` för Windows.

### Uppdatera LogAgent-actorn
1.  Ta emot AccessLog-objekt
2.  Generera ett löpnummer. Börja på 1 och plussa på ett för varje ny logg (i++ fungerar inte i scala, använd `i += 1` eller `i = i + 1`)
3.  Skapar ett nytt LogMessage-objekt med hostname, löpnummer och AccessLog-objektet 
4.  Skicka LogMessage-objektet till server-actorn

LogAgent-actorn finns under: [agent/src/main/scala/se/callista/loganalyzer/agent/LogAgent.scala](https://github.com/callistaenterprise/akka-cadec-2013/blob/master/agent/src/main/scala/se/callista/loganalyzer/agent/LogAgent.scala)

Använd följande kommando för att verifiera att LogAgent fungerar enligt kraven ovan:
`sbt 'agent/test-only se.callista.loganalyzer.agent.LogAgentSuite'`

[*Exempel på lösning*](https://github.com/callistaenterprise/akka-cadec-2013/blob/task1/agent/src/main/scala/se/callista/loganalyzer/agent/LogAgent.scala)

### Testa hela flödet
    
Kompilera koden med kommandot: `sbt compile` 

Starta servern och sedan agenten med start-scripten.

Agenten ska nu skicka logg-meddelanden över nätverket till servern där meddelanden ska visas i ett terminalfönster. Verifiera att meddelanden loggas från servern.


Uppgift 2: Räkna antal anrop beroende på HTTP Status
---------------------

I AccessLog-objektet anges den [HTTP Status](http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html) som varje anrop har. 
  - Om HTTP status är 200 betyder detta att anropet gått bra
  - Om HTTP status börjar på 400 betyder detta att anropet misslyckats p.g.a ett klientfel, t.ex 404 om klienten försöker nå en resurs som inte finns.
  - Om HTTP status börjar på 500 betyder detta att ett fel uppstod på servern.

För att se hur väl våra webbservrar fungerar vill vi sätta upp en dashboard som visar hur många lyckade anrop, felaktiga och misslyckade som gjorts. Detta kan åstakommas genom sätta upp actors som räknar varje typ av status.

### Uppdatera LogServer-actorn
1.  Skapa tre [StatusCounter](https://github.com/callistaenterprise/akka-cadec-2013/blob/master/server/src/main/scala/se/callista/loganalyzer/server/StatusCounter.scala)-actors, en för varje typ av HTTP-status ([Success, ClientError och ServerError](https://github.com/callistaenterprise/akka-cadec-2013/blob/master/common/src/main/scala/se/callista/loganalyzer/HttpStatus.scala)). En actor skapar man med `context.actorOf(Props(new MyActor(p1, p2)), "childActorName")`
2.  Skicka logg-meddelandet till rätt StatusCounter beroende på HTTP-status:

    1. Success om HTTP-status är under 400
    2. ClientError om HTTP-status är 400-499
    3. ServerError om HTTP-status är över 500

[*Exempel på lösning*](https://github.com/callistaenterprise/akka-cadec-2013/blob/task2/server/src/main/scala/se/callista/loganalyzer/server/LogServer.scala)

### Uppdatera StatusCounter
1.  Ta emot LogMessage objekt
2.  Räkna upp med ett varje gång en logg kommer in
3.  Skapa ett [Count](https://github.com/callistaenterprise/akka-cadec-2013/blob/master/common/src/main/scala/se/callista/loganalyzer/Count.scala)-objekt med nuvarande antal anrop och skicka detta till presenter-actorn.

Använd följande kommando för att verifiera att StatusCountern fungerar:
`sbt 'server/test-only se.callista.loganalyzer.server.StatusCounterSuite'`
 
[*Exempel på lösning*](https://github.com/callistaenterprise/akka-cadec-2013/blob/task3/server/src/main/scala/se/callista/loganalyzer/server/StatusCounter.scala) 
 
### Testa hela flödet
Kompilera genom att köra kommandot: `sbt compile`

Starta återigen servern med: `server/target/start` (windows: `start_server.bat`)

...och agenten med: `agent/target/start` (windows: kör `start_agent.bat`)

Gå in på [localhost:8080](http://localhost:8080) och verifiera att siffrorna räknar upp


Uppgift 3: Spara logg-meddelanden till databasen
---------------------

Vi kommer i detta steg spara ner alla logg-meddelanden till en databas. Då databasen är något instabil och ibland returnerar exceptions vill vi inte att server-actorn själv ska spara meddelandena utan låta en egen actor, DatabaseWorker, ta hand om det något riskfyllda jobbet. 

### Uppdatera LogServer
1.  Skapa en DatabaseWorker-actor. 
2.  Forwarda alla logg-meddelanden till DatabaseWorker-actorn. Använd `actorReferens.forward([meddelande])` för att vidarebefordra meddelande och behålla referens till actorn som skickade meddelandet från början.

[*Exempel på lösning*](https://github.com/callistaenterprise/akka-cadec-2013/blob/task3/server/src/main/scala/se/callista/loganalyzer/server/LogServer.scala)

### Uppdatera DatabaseWorker
1.  Ta emot LogMessage objekt 
2.  Spara logg-meddelanden(LogMessage) till databasen. `database.save([hostname], [löpnummer], [AccessLog])`
3.  Skicka tillbaks ett bekräftelsemeddelande (ConfirmationMessage) med löpnumret (id) till actorn som skickade meddelandet (actor-referens: `sender`)

DatabaseWorker-actorn finns under: [server/src/main/scala/se/callista/loganalyzer/server/DatabaseWorker.scala](https://github.com/callistaenterprise/akka-cadec-2013/blob/master/server/src/main/scala/se/callista/loganalyzer/server/DatabaseWorker.scala)

Använd följande kommando för att verifiera att DatabaseWorkern fungerar som förväntat:
`sbt 'server/test-only se.callista.loganalyzer.server.DatabaseWorkerSuite'`

[*Exempel på lösning*](https://github.com/callistaenterprise/akka-cadec-2013/blob/task3/server/src/main/scala/se/callista/loganalyzer/server/DatabaseWorker.scala)

### Testa hela flödet
Kompilera om och starta server och agent igen. 

Gå in på [localhost:8080/logs](http://localhost:8080/logs) för att verifiera att logglistan uppdateras.

Uppgift 4: Hantera fel
---------------------
Notera att databasen ibland missar att spara logg-meddelanden och fel uppstår. Detta vill vi kunna hantera.

På servern vill vi tillämpa strategin "let it crash". Det innebär att om databasen returnerar ett fel ska vi helt enkelt starta om DatabaseWorkern och sedan fortsätta hantera loggar utan att påverka LogServer-actorn. Detta åstakommer vi genom att sätta en *supervisionStrategy* i LogServer-actorn som är DatabaseWorkern:s parent actor:
```scala
override val supervisorStrategy = OneForOneStrategy() {
  case d: DatabaseFailureException => Restart
}
```

[*Exempel på lösning*](https://github.com/callistaenterprise/akka-cadec-2013/blob/task4/server/src/main/scala/se/callista/loganalyzer/server/LogServer.scala)

Om fel uppstår i databasen på serversidan eller om loggmeddelanden försvinner på väg till servern vill vi på agent-sidan ha möjlighet att skicka om dessa. Detta kan göras genom att inom en viss tidsperiod kontrollera om ett bekräftelsemeddelande (ConfirmationMessage) för ett loggmeddelande inkommit från servern. Om detta inte skett, skicka om loggmeddelandet med samma löpnummer. Vi vill att alla meddelanden ska skickas **minst** en gång ( *at-least-once* ) . 

Tips på lösning:
* Alla loggmeddelanden som skickas kan läggas till i en Map med löpnumret som nyckel. När sedan ett ConfirmationMessage kommer in kan man plocka bort loggen.

```scala
// skapa (mutable) map
val map = Map[Int, String]()

// lägg till
map += 1 -> "value"

// ta bort
map -= 1

// iterera igenom en map och kör en funktion med varje nyckel och värde 
map.foreach { case (key, value) => funktion(key, value) }
```

* Ett schemalagt jobb kan sättas upp med hjälp av en scheduler som skickar meddelanden till actorn inom ett visst tidsintervall för att trigga omsändning av loggar: 

```scala
  context.system.scheduler.schedule(2 seconds, 2 seconds, self, HandleUnprocessedLogs)
```

Använd följande kommando för att verifiera att LogAgent skickar om meddelanden inom fem sekunder:
`sbt 'agent/test-only se.callista.loganalyzer.agent.LogAgentResendSuite'`

[*Exempel på lösning*](https://github.com/callistaenterprise/akka-cadec-2013/blob/task4/agent/src/main/scala/se/callista/loganalyzer/agent/LogAgent.scala)

(Extra) Uppgift 5: Räkna inte omsändningar av logg-meddelanden
---------------------

När en skrivning till databasen fallerar och omsändning sker från LogAgenten kommer StatusCountern räkna anropet två gånger. Detta måste hanteras av StatusCountern så den tar hänsyn till om samma loggmeddelande kommer in flera gånger. Utgå ifrån att ett loggmeddelandes *hostname* tillsammans med *id* (löpnummer) är unikt.

Använd följande kommando för att verifiera att StatusCountern nu inte räknar upp samma loggmeddelande två gånger:
`sbt 'server/test-only se.callista.loganalyzer.server.StatusCounterIdempotentSuite'`

[*Exempel på lösning*](https://github.com/callistaenterprise/akka-cadec-2013/blob/task5/server/src/main/scala/se/callista/loganalyzer/server/StatusCounter.scala)

---
*Tutorialen är skapad av Albert Örwall och Pär Wenåker för Cadec 2013 som arrangeras av [Callista Enterprise AB](http://callistaenterprise.se/).*
