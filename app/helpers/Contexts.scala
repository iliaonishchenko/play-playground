package helpers

import play.api.Play.current
import play.api.libs.concurrent.Akka
import scala.concurrent.ExecutionContext

object Contexts {
	val database: ExecutionContext = Akka.actorSystem.dispatchers.lookup("contexts.database")
}
