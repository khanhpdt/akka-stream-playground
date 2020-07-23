name := "akka-stream-playground"

version := "0.1"

scalaVersion := "2.13.3"

val AkkaVersion = "2.6.8"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
