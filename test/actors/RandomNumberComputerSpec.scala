package actors

import actors.RandomNumberComputer._
import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}
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

  it should "fail when the maximum is a negative number" in {

    class StepParent(target: ActorRef) extends Actor {
      override def supervisorStrategy: SupervisorStrategy =
        OneForOneStrategy() {
          case t: Throwable =>
            target ! t
            Restart
        }
      override def receive: Receive = {
        case props: Props => sender() ! context.actorOf(props)
      }
    }

    val parent = system.actorOf(Props(new StepParent(testActor)), name = "stepParent")

    parent ! RandomNumberComputer.props
    val actorUnderTest = expectMsgType[ActorRef]
    actorUnderTest ! ComputeRandomNumber(-1)
    expectMsgType[IllegalArgumentException]

  }
}
