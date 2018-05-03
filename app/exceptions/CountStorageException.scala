package exceptions

import models.StoredCounts

case class CountStorageException(counts: StoredCounts) extends RuntimeException
