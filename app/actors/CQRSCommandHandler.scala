package actors

import akka.actor._
import actors.Messages.{Command, Event, RegisterUser, UserRegistred}
import akka.persistence.{PersistentActor, RecoveryCompleted}


class CQRSCommandHandler extends PersistentActor with ActorLogging {
	override def receiveRecover: Receive = {
		case RecoveryCompleted =>
			log.info("Recovery Completed")
		case evt: Event => handleEvent(evt)
		case _ =>
			log.error("Failed to recover!")
	}

	override def receiveCommand: Receive = {
		case RegisterUser(phoneNUmber, username) =>
			persist(UserRegistred(phoneNUmber, username))(handleEvent)
		case command: Command =>
			context.child(command.phoneNumber).map { reference =>
				reference.forward(command)
			}.getOrElse(sender() ! "UserUnknown")
	}

	def handleEvent(event: Event): Unit = event match {
		case registred @ UserRegistred(phoneNumber, username, _) =>
			context.actorOf(
				Props(classOf[ClientCommandHandler], phoneNumber, username),
				name = phoneNumber
			)
			if (recoveryFinished) sender() ! registred
	}

	override def persistenceId: String = "CQRSCommandHandler"
}
