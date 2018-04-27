name := "play_example"
 
version := "1.0" 
      
lazy val `play_example` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

val iterateesReactive = "com.typesafe.play" %% "play-iteratees-reactive-streams" % "2.6.1"
val iterateesExtra = "com.typesafe.play.extras" % "iteratees-extras_2.11" % "1.6.0"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice, iterateesReactive, iterateesExtra, filters)

routesImport += "binders.PathBinders._"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )