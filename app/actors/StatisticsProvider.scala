package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class StatisticsProvider extends Actor with ActorLogging {

	var reachComputer: ActorRef = _
	var storage: ActorRef = _
	var followersCounter: ActorRef = _

	override def preStart(): Unit = {
		log.info("Hello World with StatisticsProvider")
		followersCounter = context.actorOf(Props[UsersFollowersCounter], name = "userFollowersCounter")
		storage = context.actorOf(Props[Storage], name = "storage")
		reachComputer = context.actorOf(TweetReachComputer.props(followersCounter, storage),
			name = "tweetReachComputer")
	}
	override def receive: Receive = {
		case message => //do nothing
	}
}

object StatisticsProvider {
	def props = Props[StatisticsProvider]
}
