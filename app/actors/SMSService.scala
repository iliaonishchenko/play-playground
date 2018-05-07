package actors


import javax.inject.Inject

import akka.actor.{Actor, ActorLogging, Props}
import play.api.db.Database

class SMSService @Inject()(database: Database) extends Actor with ActorLogging {

	override def preStart(): Unit = {
		context.actorOf(Props[SMSServer])
	}

	override def receive: Receive = {
		case msg => println(msg)
	}
}

