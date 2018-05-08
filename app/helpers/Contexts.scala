package helpers

import javax.inject.{Inject, Singleton}
import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext

object Contexts /*@Inject() (implicit system: ActorSystem)*/ {
	def database()(implicit system: ActorSystem): ExecutionContext =
		system.dispatchers.lookup("contexts.database")
}
