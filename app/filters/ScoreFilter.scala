package filters

import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.http.HttpEntity
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.collection.immutable.Seq
import scala.concurrent.Future

class ScoreFilter extends Filter{
	override implicit def mat: Materializer = ???

	override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
		val result = f(rh)
		import play.api.libs.concurrent.Execution.Implicits._
		result.map { res =>
			if(res.header.status == 200 || res.header.status == 406) {
				val correct = res.session(rh).get("correct").getOrElse(0)
				val wrong = res.session(rh).get("wrong").getOrElse(0)
				val score = s"\nYour current score is $correct correct answres and $wrong wrong answers."
				val (contentLen, contentType) = (res.body.contentLength, res.body.contentType)
				//				val value: Enumerator[Array[Byte]] = Enumerator(score.getBytes("UTF-8"))
				val string: Seq[ByteString] = Seq(ByteString.apply(score.getBytes("UTF-8")))
				val streamedSource = Source.s
				val newBody = HttpEntity.Streamed(streamedSource, contentLen, contentType)
				res.copy(body = newBody)
			} else res
		}
	}
}
