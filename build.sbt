name := """armada"""
organization := "com.hashmapinc"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

libraryDependencies += guice
libraryDependencies += "net.kaliber" %% "play-s3" % "9.0.0"
libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-s3" % "0.20"
libraryDependencies += "com.github.seratch" %% "awscala-ec2" % "0.8.0"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-ec2" % "1.11.342"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.hashmapinc.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.hashmapinc.binders._"
