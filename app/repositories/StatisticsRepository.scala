package repositories

import models.StoredCounts

import scala.concurrent.{ExecutionContext, Future}

trait StatisticsRepository {

  def storeCounts(counts: StoredCounts)(implicit ec: ExecutionContext): Future[Unit]

  def retriveLatestCounts(username: String)(implicit ec: ExecutionContext): Future[StoredCounts]
}
