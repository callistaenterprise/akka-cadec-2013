Cadec Akka Tutorial 2013
====================

I denna tutorial kommer vi bygga ett enkelt system för central analys av loggar från webbservrar. Tanken är att man har en agent på varje webbserver som ligger och läser av den sk. [access-loggen](http://httpd.apache.org/docs/2.2/logs.html#accesslog) i Apache. Denna agent skickar sedan vidare dessa loggar till en central server som lagrar loggarna och gör realtidsanalys. All kommunikation som sker asynkront mellan agenterna och servern.

Tutorial är uppbyggd så att infrastukturen finns på plats, men logik måste skapas för de actors som ska användas.

Instruktioner för att sätta upp en utvecklingsmiljö finns [här].

Uppgift 1: Skicka logg-meddelanden från en agent till en server
---------------------

Första uppgiften går ut på att få upp ett flöde där en agent på en webbserver scannar loggar och skickar logg-objekt vidare till en server. Med hjälp av Akka kan detta ske helt utan att agent-actorn behöver veta var servern befinner sig utan agenten har bara en referens (ActorRef) till server-actorn.

### 1. Uppdatera LogAgent-actorn
1.  Ta emot AccessLog-objekt
2.  Generera ett löpnummer. Börja på 1 och plussa på ett för varje ny logg.
3.  Skapar ett nytt LogMessage-objekt med löpnummer, hostname och AccessLog-objektet
4.  Skicka LogMessage-objektet till server-actorn

LogAgent-actorn finns under: [agent/src/main/scala/se/callista/loganalyzer/agent/LogAgent.scala](https://github.com/callistaenterprise/akka-cadec-2013/blob/master/agent/src/main/scala/se/callista/loganalyzer/agent/LogAgent.scala)

Använd följande kommando för att verifiera att LogAgent fungerar enligt kraven ovan:
`sbt 'agent/test-only se.callista.loganalyzer.agent.LogAgentSuite'`

### 2. Uppdatera LogServer-actorn

1.  Ta emot LogMessage-objekt
2.  Skriv ut en logg om att objektet är mottaget (loggning görs med `log.info(...)`)

LogServer-actorn finns under: [server/src/main/scala/se/callista/loganalyzer/server/LogServer.scala](https://github.com/callistaenterprise/akka-cadec-2013/blob/master/server/src/main/scala/se/callista/loganalyzer/server/LogServer.scala)

Använd följande kommando för att verifiera att LogServer tar emot LogMessage-objekt:
`sbt 'server/test-only se.callista.loganalyzer.server.LogServerSuite'`

### 3. Testa hela flödet

Skapa först start-script genom att köra kommandot: `sbt start-script`

Kompilera koden med kommandot: `sbt compile` 

Starta sedan servern i ett terminal-fönster med kommandot: `server/target/start` (windows: )

Starta sedan agenten i ett annat terminal-fönster med kommandot: `agent/target/start` (windows: )

Agenten ska nu skicka logg-meddelanden över nätverket till servern där meddelanden ska visas i ett terminalfönster. Verifiera i terminalen på för servern att meddelanden kommer fram.


Uppgift 2: Räkna antal loggar beroende på HTTP Status
---------------------

I AccessLog-objektet anges den [HTTP Status](http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html) som varje anrop har. 
  - Om HTTP status är 200 betyder detta att anropet gått bra
  - Om HTTP status börjar på 400 betyder detta att anropet misslyckats p.g.a ett klientfel, t.ex 404 om klienten försöker nå en resurs som inte finns.
  - Om HTTP status börjar på 500 betyder detta att ett fel uppstod på servern.

För att se hur väl våra webbservrar fungerar vill vi sätta upp en dashboard som visar hur många lyckade anrop, felaktiga och misslyckade som gjorts. Detta kan åstakommas genom sätta upp actors som räknar varje typ av status.

### 1. Uppdatera LogServer-agenten
1.  Skapa [StatusCounter](https://github.com/callistaenterprise/akka-cadec-2013/blob/master/server/src/main/scala/se/callista/loganalyzer/server/StatusCounter.scala)-actors för varje typ av HTTP-status ([Success, ClientError och ServerError](https://github.com/callistaenterprise/akka-cadec-2013/blob/master/common/src/main/scala/se/callista/loganalyzer/Count.scala))
2.  Skicka logg-meddelandet till rätt StatusCounter beroende på HTTP-status:

    1. Success om HTTP-status är 200
    2. ClientError om HTTP-status är 400-499
    3. ServerError om HTTP-status är över 500

### 2. Uppdatera StatusCounter
1.  Ta emot LogMessage objekt
2.  Räkna upp med ett varje gång en logg kommer in
3.  Skapa ett [Count](https://github.com/callistaenterprise/akka-cadec-2013/blob/master/common/src/main/scala/se/callista/loganalyzer/Count.scala)-objekt och skicka till presenter-actorn

Använd följande kommando för att verifiera att StatusCountern fungerar:
`sbt 'server/test-only se.callista.loganalyzer.server.StatusCounterSuite'`
 
### 3. Testa hela flödet

Kompilera genom att köra kommandot: `sbt compile`

Starta återigen servern med: `server/target/start` (windows: )

...och agenten med: `agent/target/start` (windows: )

Gå in på [localhost:8080](http://localhost:8080) och verifiera att siffrorna räknar upp


Uppgift 3: Spara logg-meddelanden till databasen:
---------------------

Vi kommer i detta steg spara ner alla logg-meddelanden till en databas. Då databasen är något instabil och ibland returnerar exceptions vill vi inte att server-actorn själv ska spara meddelandena utan låta en egen actor, DatabaseWorker, ta hand om det något riskfyllda jobbet. 

### 1. Uppdatera DatabaseWorker
1.  Ta emot LogMessage objekt
2.  Spara logg-meddelanden(LogMessage) till databasen
3.  Skicka tillbaks ett bekräftelsemeddelande (ConfirmationMessage) med löpnumret (id) till actorn som skickade meddelandet

DatabaseWorker-actorn finns under: [server/src/main/scala/se/callista/loganalyzer/server/DatabaseWorker.scala](https://github.com/callistaenterprise/akka-cadec-2013/blob/master/server/src/main/scala/se/callista/loganalyzer/server/DatabaseWorker.scala)

Använd följande kommando för att verifiera att DatabaseWorkern fungerar som förväntat:
`sbt 'server/test-only se.callista.loganalyzer.server.DatabaseWorkerSuite'`

### 2. Uppdatera LogServer
1.  Skapa en DatabaseWorker-actor
2.  Forwarda alla logg-meddelanden till DatabaseWorker-actorn

### 3. Testa hela flödet
Kompilera om och starta server och agent igen. 

Gå in på [localhost:8080/logs](http://localhost:8080/logs) för att se att logglistan uppdateras.

Uppgift 4: Hantera fel
---------------------
Notera att databasen ibland missar att spara logg-meddelanden och fel uppstår. Detta vill vi kunna hantera på ett .

På agent-sidan vill vi köra strategin "let it crash", alltså om databasen returnerar ett fel ska DatabaseWorkern starta om men inte påverka LogServer-actorn.

Vi vill dels sätta en strategi på servern om att DatabaseWorkern ska startas om varje gång ett DatabaseFailureException kasats genom att ange en `supervisionStrategy`.

Om fel uppstår i databasen på serversidan eller om loggmeddelanden försvinner på väg till servern vi på agent-sidan ha möjlighet att skicka om loggmeddelanden. Detta kan göras genom att inom en viss tidsperiod kontrollera om ett bekräftelsemeddelande (ConfirmationMessage) för ett loggmeddelande inkommit från servern. Om detta inte skett, skicka om loggmeddelandet med samma löpnummer.

Använd följande kommando för att verifiera att LogAgent skickar om meddelanden inom fem sekunder:
`sbt 'agent/test-only se.callista.loganalyzer.agent.LogAgentResendSuite'`



---

Uppgift 5: Räkna inte omsändningar av logg-meddelanden
---------------------

Varje gång ett logg-meddelande skickas kommer nu StatusCountern att räkna upp ett steg till. Detta måste hanteras "idempotent", alltså varje loggmeddelande får bara räknas en gång. Utgå ifrån att ett loggmeddelandes nyckel [hostnamn + id] är unikt.

Använd följande kommando för att verifiera att StatusCountern nu inte räknar upp samma loggmeddelande två gånger:
`sbt 'server/test-only se.callista.loganalyzer.server.StatusCounterIdempotentSuite'`



