package actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.http.scaladsl.model.DateTime
import akka.stream.ConnectionException
import messages.{ReachStored, StoreReach}
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.LastError

class Storage extends Actor with ActorLogging {
	val Database: String = "twitterService"
	val ReachCollection: String = "ComputedReach"

	implicit val executionContext = context.dispatcher

	val driver: MongoDriver = new MongoDriver()
	var connection: MongoConnection = _
	var db: DefaultDB = _
	var collection: BSONCollection = _
	var currentWrites = Set.empty[BigInt]

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

	import akka.pattern.pipe
	import reactivemongo.api.commands.WriteResult

	override def receive: Receive = {
		case StoreReach(tweetId, score) =>
			log.info(s"Storing reach for tweet $tweetId")
			if(!currentWrites.contains(tweetId)){
				currentWrites = currentWrites + tweetId
				val originalSender = sender()
				collection
					.insert(StoredReach(DateTime.now, tweetId, score))
			  	.map{ lastError =>
						LastStorageError(WriteResult.lastError(lastError).get, tweetId, originalSender)
					}
					.recover{
						case _ => currentWrites = currentWrites - tweetId
					}
			  	.pipeTo(self)
			}
			case LastStorageError(error, tweetId, client) =>
			if (error.ok) {
				currentWrites = currentWrites - tweetId
			} else {
				client ! ReachStored(tweetId)
			}
//			sender() ! ReachStored(tweetId)
	}

	private def obtainConnection(): Unit = {
		connection = driver.connection(List("localhost"))
		db = connection.db(Database)
		collection = db.collection[BSONCollection](ReachCollection)
	}
}

case class StoredReach(when: DateTime, tweetId: BigInt, score: Int)
case class LastStorageError(error: LastError, tweetId: BigInt, client: ActorRef)
