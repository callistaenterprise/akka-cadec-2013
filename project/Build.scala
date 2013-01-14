import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtStartScript
import com.typesafe.sbtscalariform.ScalariformPlugin
import com.typesafe.sbtscalariform.ScalariformPlugin.ScalariformKeys
import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseKeys

object LogAnalyzerBuild extends Build {
  val Organization = "logAnalyzer"
  val Version      = "1.0-SNAPSHOT"
  val ScalaVersion = "2.9.2"

  lazy val akkademo = Project(
    id = "logAnalyzer",
    base = file("."),
    settings = defaultSettings ++
      Seq(SbtStartScript.stage in Compile := Unit),
    aggregate = Seq(common, server, agent)
  )

  lazy val common = Project(
    id = "common",
    base = file("common"),
    settings = defaultSettings ++ Seq(libraryDependencies ++= Dependencies.logAnalyzer)
  )

  lazy val server = Project(
    id = "server",
    base = file("server"),
    dependencies = Seq(common),
    settings = defaultSettings ++
    SbtStartScript.startScriptForClassesSettings ++
    Seq(libraryDependencies ++= Dependencies.logAnalyzer ++ Dependencies.unfiltered)
  )

  lazy val agent = Project(
    id = "agent",
    base = file("agent"),
    dependencies = Seq(common),
    settings = defaultSettings ++
    SbtStartScript.startScriptForClassesSettings ++
    Seq(libraryDependencies ++= Dependencies.logAnalyzer)
  )

  lazy val defaultSettings = Defaults.defaultSettings ++ Seq(
    resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",

    // compile options
    scalacOptions ++= Seq("-encoding", "UTF-8", "-optimise", "-deprecation", "-unchecked"),
    javacOptions  ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),

    // disable parallel tests
    parallelExecution in Test := false,

    EclipseKeys.withSource := true
  )

}

object Dependencies {
  import Dependency._
  val logAnalyzer = Seq(akkaActor, akkaRemote, scalaTest, jUnit, akkaTest, commonsIo)
  val unfiltered = Seq(unfilteredFilter, unfilteredNetty, unfilteredNettyServer, unfilteredWebsockets)
}

object Dependency {
  object Version {
    val Akka      = "2.0.5"
    val Scalatest = "1.8"
    val JUnit     = "4.5"
    val Unfiltered = "0.6.3"
  }

  // ---- Application dependencies ----

  val akkaActor   = "com.typesafe.akka"   % "akka-actor"              % Version.Akka
  val akkaRemote  = "com.typesafe.akka"   % "akka-remote"             % Version.Akka


  val unfilteredFilter = "net.databinder" % "unfiltered-filter_2.9.1" % Version.Unfiltered
  val unfilteredNetty = "net.databinder" % "unfiltered-netty_2.9.1" % Version.Unfiltered
  val unfilteredNettyServer = "net.databinder" % "unfiltered-netty-server_2.9.1" % Version.Unfiltered
  val unfilteredWebsockets = "net.databinder" % "unfiltered-netty-websockets_2.9.1" % Version.Unfiltered

  // ---- Commons IO ----

  val commonsIo = "commons-io" % "commons-io" % "2.4"

  // ---- Test dependencies ----

  val scalaTest   = "org.scalatest"       % "scalatest_2.9.2"          % Version.Scalatest  % "test"
  val jUnit       = "junit"               % "junit"                    % Version.JUnit      % "test"
  val akkaTest    = "com.typesafe.akka"   % "akka-testkit"             % Version.Akka       % "test"
}
