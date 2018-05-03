package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import messages._

import scala.concurrent.Future
import scala.util.control.NonFatal

class TweetReachComputer (userFollowersCounter: ActorRef,
													storage: ActorRef) extends Actor with ActorLogging {
	implicit val executionContext = context.dispatcher

	var followersCountsByRetweet = Map.empty[FetchedRetweets, List[FollowerCount]]

	import akka.pattern.pipe

	override def receive: Receive = {
		case ComputeReach(tweetId) =>
			val originalSender = sender()
			fetchRetweets(tweetId, sender())
				.recover {
					case NonFatal(t) => RetweetFetchingFailed(tweetId, t, originalSender)
				}
				.pipeTo(self)
		case fetchedRetweets: FetchedRetweets =>
			followersCountsByRetweet += fetchedRetweets -> List.empty
			fetchedRetweets.retweeters.foreach{ rt =>
				userFollowersCounter ! FetchFollowerCount(
					fetchedRetweets.tweetId, rt
				)
			}
		case count @ FollowerCount(tweetId, _) =>
			log.info(s"Received followers count for tweet $tweetId")
			fetchedRetweetsFor(tweetId).foreach { fetchedRetweets =>
				updateFollowersCount(tweetId, fetchedRetweets, count)
			}
		case ReachStored(tweetId) =>
			followersCountsByRetweet.keys.find(_.tweetId == tweetId)
		  	.foreach{key =>
					followersCountsByRetweet = followersCountsByRetweet.filterNot(_._1 == key)
				}
	}

	def fetchedRetweetsFor(tweetId: BigInt): Option[FetchedRetweets] =
		followersCountsByRetweet.keys.find(_.tweetId == tweetId)

	def updateFollowersCount(tweetId: BigInt,
													 fetchedRetweets: FetchedRetweets,
													 count: FollowerCount) = {
		val existingCounts = followersCountsByRetweet(fetchedRetweets)
		followersCountsByRetweet = followersCountsByRetweet.updated(fetchedRetweets, count :: existingCounts)
		val newCounts = followersCountsByRetweet(fetchedRetweets)
		if(newCounts.lengthCompare(fetchedRetweets.retweeters.length) == 0){
			log.info(s"received all retweeters followers count for tweet $tweetId, computing sum")
		}
		val score = newCounts.map(_.followersCount).sum
		fetchedRetweets.client ! TweetReach(tweetId, score)
		storage ! StoreReach(tweetId, score)
	}

	def fetchRetweets(int: BigInt, ref: ActorRef): Future[FetchedRetweets] = ???

}

object TweetReachComputer {
	def props(followerCounter: ActorRef, storage: ActorRef) =
		Props(classOf[TweetReachComputer], followerCounter, storage)
}

case class FetchedRetweets(tweetId: BigInt, retweeters: List[String], client: ActorRef)
case class RetweetFetchingFailed(tweetId: BigInt, cause: Throwable, client: ActorRef)
