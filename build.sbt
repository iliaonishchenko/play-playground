name := "play_example"
 
version := "1.0" 
      
lazy val `play_example` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

scalaVersion := "2.12.2"

val akkaActors = "com.typesafe.akka" %% "akka-actor" % "2.5.12"
val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % "2.5.12"
val reactiveMongo = "org.reactivemongo" % "play2-reactivemongo_2.12" % "0.13.0-play26"

libraryDependencies ++= Seq( jdbc, ehcache, ws, specs2 % Test, guice, akkaActors, akkaSlf4j, reactiveMongo)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
