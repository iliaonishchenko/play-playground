name := "play_example"
 
version := "1.0" 
      
lazy val `play_example` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

val akkaPersistance = "com.typesafe.akka" %% "akka-persistence" % "2.5.12"
val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % "2.5.12"
val akkaActors = "com.typesafe.akka" %% "akka-actor" % "2.5.12"
val jodaTime = "joda-time" % "joda-time" % "2.9.9"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice, akkaPersistance, akkaSlf4j, jodaTime, akkaActors)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  
