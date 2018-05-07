package actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.io.Tcp.{PeerClosed, Received, Write}

class SMSHandler(connection: ActorRef) extends Actor with ActorLogging {

	override def receive: Receive = {
		case Received(data) =>
			log.info(s"Received message: ${data.utf8String}")
			connection ! Write(data)
		case PeerClosed =>
			context.stop(self)
	}

}
