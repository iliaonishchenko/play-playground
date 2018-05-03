package actors

import javax.inject.Inject

import akka.actor.ActorSystem
import com.google.inject.AbstractModule

trait ApplicationActors

class Actors @Inject()(system: ActorSystem) extends ApplicationActors{
	val providerRef = system.actorOf(
		props = StatisticsProvider.props,
		name = "statisticsProvider"
	)
}

class ActorsModule extends AbstractModule {
	override def configure(): Unit = {
		bind(classOf[ApplicationActors]).to(classOf[Actors]).asEagerSingleton()
	}
}