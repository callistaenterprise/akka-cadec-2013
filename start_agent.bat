@echo off

set USER_HOME=C:\Users\Admin
set PROJECT_HOME=%USER_HOME%\Projects\akka-cadec-2013

set SBT_HOME=%USER_HOME%\.sbt
set IVY_HOME=%USER_HOME%\.ivy2

set MAINCLASS=se.callista.loganalyzer.agent.LogAgentApplication

java %JAVA_OPTS% -cp "%PROJECT_HOME%\agent\target\scala-2.9.2\classes;%PROJECT_HOME%\common\target\scala-2.9.2\classes;%SBT_HOME%\boot\scala-2.9.2\lib\scala-library.jar;%IVY_HOME%\cache\com.typesafe.akka\akka-actor\jars\akka-actor-2.0.5.jar;%IVY_HOME%\cache\com.typesafe\config\bundles\config-0.3.1.jar;%IVY_HOME%\cache\com.typesafe.akka\akka-remote\jars\akka-remote-2.0.5.jar;%IVY_HOME%\cache\io.netty\netty\bundles\netty-3.5.4.Final.jar;%IVY_HOME%\cache\com.google.protobuf\protobuf-java\jars\protobuf-java-2.4.1.jar;%IVY_HOME%\cache\net.debasishg\sjson_2.9.1\jars\sjson_2.9.1-0.15.jar;%IVY_HOME%\cache\net.databinder\dispatch-json_2.9.1\jars\dispatch-json_2.9.1-0.8.5.jar;%IVY_HOME%\cache\org.apache.httpcomponents\httpclient\jars\httpclient-4.1.jar;%IVY_HOME%\cache\org.apache.httpcomponents\httpcore\jars\httpcore-4.1.jar;%IVY_HOME%\cache\commons-logging\commons-logging\jars\commons-logging-1.1.1.jar;%IVY_HOME%\cache\commons-codec\commons-codec\jars\commons-codec-1.4.jar;%IVY_HOME%\cache\org.objenesis\objenesis\jars\objenesis-1.2.jar;%IVY_HOME%\cache\commons-io\commons-io\jars\commons-io-2.4.jar;%IVY_HOME%\cache\voldemort.store.compress\h2-lzf\jars\h2-lzf-1.0.jar" "%MAINCLASS%"