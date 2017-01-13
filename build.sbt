name := "scala_sbt"

version := "1.0"

scalaVersion := "2.11.4"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.5-SNAPSHOT"
//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5-SNAPSHOT"