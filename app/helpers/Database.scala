package helpers


import javax.inject.Inject

import akka.actor.ActorSystem
import org.jooq.{DSLContext, SQLDialect}
import org.jooq.impl.DSL

import scala.concurrent.Future

class Database @Inject() (db: play.api.db.Database)(implicit system: ActorSystem) {

	def query[A](block: DSLContext => A): Future[A] = Future {
		db.withConnection { connection =>
			val sql = DSL.using(connection, SQLDialect.POSTGRES_9_4)
			block(sql)
		}
	}(Contexts.database)

	def withTransaction[A](block: DSLContext => A): Future[A] = Future {
		db.withTransaction { connection =>
			val sql = DSL.using(connection, SQLDialect.POSTGRES_9_4)
			block(sql)
		}
	}(Contexts.database)

}
