package services

import java.time.Period

import akka.http.scaladsl.model.DateTime
import exceptions.{CountRetrievalException, CountStorageException, StatisticsServiceFailed, TwitterServiceException}
import models.{StoredCounts, TwitterCounts}
import repositories.StatisticsRepository

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class DefaultStatisticsService(statisticsRepository: StatisticsRepository,
                               twitterService: TwitterService) extends StatisticsService {
  override def createUserStatistics(username: String)(
      implicit ec: ExecutionContext): Future[Unit] = {

    def storeCounts(counts: (StoredCounts, TwitterCounts)): Future[Unit] =
      counts match {
        case (previous, current) =>
          statisticsRepository.storeCounts(
            StoredCounts(
              DateTime.now,
              username,
              current.followersCount,
              current.friendsCount
            )
          )
      }

    def publishMessage(counts: (StoredCounts, TwitterCounts)): Future[Unit] =
      counts match {
        case (previous, current) =>
          val followersDiff = current.followersCount - previous.followersCount
          val friendsDiff = current.friendsCount - previous.friendsCount
          def phrasing(diff: Long) = if (diff > 0) "gaindes" else "lost"
          val durationInDays = new Period(previous.when, DateTime.now).getDays

          twitterService.postTweet(
            s"@$username in the past $durationInDays you have " +
              s"${phrasing(followersDiff)} $followersDiff " +
              s"followers and ${phrasing(friendsDiff)} $friendsDiff friends"
          )
      }

    val prevCounts = statisticsRepository.retriveLatestCounts(username)
    val currentCounts = twitterService.fetchRelationshipCounts(username)

    val counts = for {
      prev <- prevCounts
      curr <- currentCounts
    } yield {
      (prev, curr)
    }

    val storedCounts = counts.flatMap(storeCounts)
    val publishedMessage = counts.flatMap(publishedMessage)

    val result = for {
      _ <- storedCounts
      _ <- publishedMessage
    } yield {}

		result recoverWith {
			case CountStorageException(countsToStore) =>
				retryStoring(countsToStore, attemptNumber = 0)
		} recover {
			case CountStorageException(countsToStore) =>
				throw StatisticsServiceFailed(
					"we couldn't save the statistics to database"
				)
			case CountRetrievalException(username, cause) =>
				throw StatisticsServiceFailed(
					"Database Issues"
				)
			case TwitterServiceException(message) =>
				throw StatisticsServiceFailed(
					s"Issue with connection to Twitter: $message"
				)
			case NonFatal(t) =>
				throw StatisticsServiceFailed("Unknown Problem")
		}
  }

	private def retryStoring(counts: StoredCounts, attemptNumber: Int)(
													implicit ec: ExecutionContext): Future[Unit] = {
		if(attemptNumber < 3) {
			statisticsRepository.storeCounts(counts).recoverWith{
				case NonFatal(t) => retryStoring(counts, attemptNumber + 1)
			}
		} else Future.failed(CountStorageException(counts))
	}
}
