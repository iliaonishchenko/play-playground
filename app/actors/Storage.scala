package actors

import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.model.DateTime
import akka.stream.ConnectionException
import messages.{ReachStored, StoreReach}
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.api.collections.bson.BSONCollection

class Storage extends Actor with ActorLogging {
	val Database: String = "twitterService"
	val ReachCollection: String = "ComputedReach"

	implicit val executionContext = context.dispatcher

	val driver: MongoDriver = new MongoDriver()
	var connection: MongoConnection = _
	var db: DefaultDB = _
	var collection: BSONCollection = _

	override def postRestart(reason: Throwable): Unit = {
		reason match {
			case ce: ConnectionException => obtainConnection()
		}
		super.postRestart(reason)
	}

	override def postStop(): Unit = {
		connection.close()
		super.postStop()
	}

	override def receive: Receive = {
		case StoreReach(tweetId, score) =>
			collection.insert(StoredReach(DateTime.now, tweetId, score))
			sender() ! ReachStored(tweetId)
	}

	private def obtainConnection(): Unit = {
		connection = driver.connection(List("localhost"))
		db = connection.db(Database)
		collection = db.collection[BSONCollection](ReachCollection)
	}
}

case class StoredReach(when: DateTime, tweetId: BigInt, score: Int)
