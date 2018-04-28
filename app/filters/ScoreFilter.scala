package filters

import javax.inject.Inject

import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.http.HttpEntity
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.collection.immutable.Seq
import scala.concurrent.Future

class ScoreFilter @Inject() (implicit mater: Materializer) extends Filter{
	override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
		val result = f(rh)
		import play.api.libs.concurrent.Execution.Implicits._
		result.map { res =>
			if(res.header.status == 200 || res.header.status == 406) {
				val correct = res.session(rh).get("correct").getOrElse(0)
				println(s"correct: $correct")
				val wrong = res.session(rh).get("wrong").getOrElse(0)
				println(s"wrong: $wrong")
				val score = s"\nYour current score is $correct correct answres and $wrong wrong answers."
				val (contentLen, contentType) = (res.body.contentLength, res.body.contentType)
				val string: Seq[ByteString] = Seq(ByteString.apply(score.getBytes("UTF-8")))
				println(s"body before merge: ${res.body}")
				if(!contentLen.exists(_ != 0)){
					println("inside if")
					res.copy(body = HttpEntity.Streamed(Source(string), contentLen, contentType))
				} else {
					val streamedSource = Source.zipN(Seq(res.body.dataStream, Source(string))).mapConcat(identity)
					val newBody = HttpEntity.Streamed(streamedSource, contentLen, contentType)
					println(s"body after merge: $newBody")
					res.copy(body = newBody)
				}
			} else res
		}
	}

	override implicit def mat: Materializer = mater
}
