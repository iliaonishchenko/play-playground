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
val jooq = "org.jooq" % "jooq" % "3.10.7"
val jooqMeta = "org.jooq" % "jooq-codegen-maven" % "3.10.7"
val jooqCodegenmaven = "org.jooq" % "jooq-codegen-maven" % "3.10.7"
val db = "com.h2database" % "h2" % "1.4.197"

libraryDependencies ++= Seq(
	jdbc,
	ehcache,
	ws,
	specs2 % Test,
	guice,
	akkaPersistance,
	akkaSlf4j,
	jodaTime,
	akkaActors,
	jooq,
	jooqMeta,
	jooqCodegenmaven,
	db
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  
