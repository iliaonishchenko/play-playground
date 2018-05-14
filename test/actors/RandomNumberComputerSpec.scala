package actors

import actors.RandomNumberComputer._
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike}
import org.specs2.matcher.ShouldMatchers

import scala.concurrent.duration._

class RandomNumberComputerSpec(_system: ActorSystem)
    extends TestKit(_system)
    with ImplicitSender
    with FlatSpecLike
    with ShouldMatchers
    with BeforeAndAfterAll {
  def this() = this(ActorSystem("RandomNumberComputerSpec"))

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A RandomNumberComputerSpec" should "send back a random number" in {
    val randomNumberComputer = system.actorOf(RandomNumberComputer.props)

    within(100.millis) {
      randomNumberComputer ! ComputeRandomNumber(100)
      expectMsgType[RandomNumber]
    }
  }
}
