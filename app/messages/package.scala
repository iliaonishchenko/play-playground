package object messages {

	case class ComputeReach(tweetId: BigInt)
	case class TweetReach(tweetId: BigInt, score: Int)

	case class FetchFollowerCount(tweetId: BigInt, user: String)
	case class FollowerCount(tweetId: BigInt, followersCount: Int)

	case class StoreReach(tweetId: BigInt, score: Int)
	case class ReachStored(tweetId: BigInt)

}
