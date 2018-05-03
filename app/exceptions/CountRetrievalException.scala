package exceptions

case class CountRetrievalException(username: String, throwable: Throwable)
	extends RuntimeException(s"Could not read counts for $username")
