package actors

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging, Props}
import akka.io.{IO, Tcp}
import akka.io.Tcp._

class SMSServer extends Actor with ActorLogging {
	import context.system

	println("started")

	IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", 6666))

	override def receive: Receive = {
		case Bound(localAddress) =>
			log.info(s"SMS server listening on $localAddress")
		case CommandFailed(_: Bind) =>
			context.stop(self)
		case Connected(remote, local) =>
			val connection = sender()
			val handler = context.actorOf(Props(classOf[SMSHandler], connection))
			connection ! Register(handler)
	}
}
