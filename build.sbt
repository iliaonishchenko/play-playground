name := "play_example"
 
version := "1.0" 
      
lazy val `play_example` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % Test
val scalaTestPlus = "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % "2.5.12" % Test

libraryDependencies ++= Seq(
  jdbc,
  ehcache,
  ws,
  specs2 % Test,
  scalaTest,
  scalaTestPlus,
  akkaTestkit,
  guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )