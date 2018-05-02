package services

import models.TwitterCounts

import scala.concurrent.{ExecutionContext, Future}

trait TwitterService {

  def fetchRelationshipCounts(username: String)(implicit ec: ExecutionContext): Future[TwitterCounts]
  def postTweet(message: String)(implicit ec: ExecutionContext): Future[Unit]

}
