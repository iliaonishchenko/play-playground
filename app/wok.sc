//import akka.actor.{Actor, ActorSystem, Props}
//
//import scala.concurrent.Await
//import scala.concurrent.duration._
//
//val sys = ActorSystem("exampleSystem")
//
//case object Die
//case object Message
//case object Revive
//
//class Actor1 extends Actor {
//
//	override def preStart(): Unit = {
//		println("preStart method")
//		super.preStart()
//	}
//
//	override def receive: Receive = {
//		case Die =>
//			println(s"I got message $Die")
//			context.become(deadState)
//		case Message =>
//			println("I'm alive ang got message")
//		case Revive =>
//			println(s"I should not get this message $Revive")
//	}
//
//	def deadState: Receive = {
//		case Die =>
//			println(s"I'm already dead, I can not $Die")
//		case Message =>
//			println(s"No messages pls, I'm dead")
//		case Revive =>
//			println(s"$Revive message")
//			context.unbecome()
//	}
//}
//
//val actor = sys.actorOf(Props[Actor1])
//
//actor ! Message
//actor ! Revive
//actor ! Die
//actor ! Die
//actor ! Revive
//actor ! Message