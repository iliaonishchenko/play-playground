package actors

import javax.inject.Inject

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.google.inject.AbstractModule
import play.api.db.Database
import play.api.libs.concurrent.AkkaGuiceSupport

class SMSService @Inject() (system: ActorSystem, database: Database) extends Actor with ActorLogging {

	var smsService: ActorRef = _

	override def preStart(): Unit = {
		smsService = system.actorOf(Props(classOf[SMSServer]))
		super.preStart()
	}

	override def receive: Receive = {
		case msg =>
	}
}

class SMSServiceModule extends AbstractModule with AkkaGuiceSupport {
	override def configure(): Unit = bindActor[SMSService]("sms")
}
