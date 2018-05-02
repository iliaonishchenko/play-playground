package models

import akka.http.scaladsl.model.DateTime

case class StoredCounts(when: DateTime, username: String, followersCount: Long, friendsCount: Long)
