package services

import scala.concurrent.Future

trait RandomNumberService {
  def generateRandomNumber: Future[Int]
}

trait DiceService {
  def throwDice: Future[Int]
}

class DiceDrivenRandomNumberService(dice: DiceService) extends RandomNumberService {
  override def generateRandomNumber: Future[Int] = dice.throwDice
}

class RollingDiceService extends DiceService {
  override def throwDice: Future[Int] = Future.successful(4)
}

