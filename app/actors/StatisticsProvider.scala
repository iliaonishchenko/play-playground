package actors

import javax.naming.ServiceUnavailableException

import actors.StatisticsProvider.{ReviveStorage, ServiceUnavailable}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.stream.ConnectionException
import messages.ComputeReach

import scala.concurrent.duration._

class StatisticsProvider extends Actor with ActorLogging {

	var reachComputer: ActorRef = _
	var storage: ActorRef = _
	var followersCounter: ActorRef = _

	override def preStart(): Unit = {
		log.info("Hello World with StatisticsProvider")
		followersCounter = context.actorOf(Props[UsersFollowersCounter], name = "userFollowersCounter")
		storage = context.actorOf(Props[Storage], name = "storage")
		context.watch(storage)
		reachComputer = context.actorOf(TweetReachComputer.props(followersCounter, storage),
			name = "tweetReachComputer")
	}
	override def receive: Receive = {
		case reach: ComputeReach => reachComputer forward reach
		case Terminated(terminatedStorageRef) =>
			context.system.scheduler.scheduleOnce(1.minute, self, ReviveStorage)
			context.become(storageUnavailable)
		case message => //do nothing
	}

	def storageUnavailable: Receive = {
		case ComputeReach(_) => sender() ! ServiceUnavailable
		case ReviveStorage =>
			storage = context.actorOf(Props[Storage], name = "storage")
			context.unbecome()

	}

	override def supervisorStrategy: SupervisorStrategy =
		OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 2.minutes) {
			case _: ConnectionException => Restart
			case t: Throwable => super.supervisorStrategy.decider.applyOrElse(t, _ => Escalate)
		}
}

object StatisticsProvider {
	def props = Props[StatisticsProvider]
	case object ServiceUnavailable
	case object ReviveStorage
}

// unbecome and become do call prestart?
