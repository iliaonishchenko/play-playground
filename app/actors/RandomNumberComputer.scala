package actors

import akka.actor.{Actor, Props}

import scala.util.Random

class RandomNumberComputer extends Actor {
  import actors.RandomNumberComputer._

  override def receive: Receive = {
    case ComputeRandomNumber(max) =>
      sender() ! RandomNumber(Random.nextInt(max))
  }
}

object RandomNumberComputer {
  def props = Props[RandomNumberComputer]
  case class ComputeRandomNumber(max: Int)
  case class RandomNumber(n:Int)
}
