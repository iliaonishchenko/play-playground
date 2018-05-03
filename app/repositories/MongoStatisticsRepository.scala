package repositories

import javax.inject.Inject

import akka.http.scaladsl.model.DateTime
import exceptions.{CountRetrievalException, CountStorageException}
import models.StoredCounts
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class MongoStatisticsRepository @Inject() (reactiveMongo: ReactiveMongoApi) extends StatisticsRepository {

	private val StatisticsCollection = "UserStatistics"

	private lazy val collection =
		reactiveMongo.db.collection[BSONCollection](StatisticsCollection)

	override def storeCounts(counts: StoredCounts)(
		implicit ec: ExecutionContext): Future[Unit] = {
		collection.insert(counts).map{ lastError =>
			if(!lastError.ok) throw CountStorageException(counts)
		}
	}

	override def retriveLatestCounts(username: String)(
		implicit ec: ExecutionContext): Future[StoredCounts] = {
		val query = BSONDocument("username" -> username)
		val order = BSONDocument("_id" -> -1)
		collection.find(query)
	  	.sort(order)
	  	.one[StoredCounts]
	  	.map{ counts =>
				counts.getOrElse(StoredCounts(DateTime.now, username, 0, 0))
			}.recover{
			case NonFatal(t) =>
				throw CountRetrievalException(username, t)
		}
	}
}
