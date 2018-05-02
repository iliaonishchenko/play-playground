package services

import scala.concurrent.{ExecutionContext, Future}

trait StatisticsService {

  def createUserStatistics(username: String)(implicit ec: ExecutionContext): Future[Unit]

}
