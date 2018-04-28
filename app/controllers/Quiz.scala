package controllers

import play.api.i18n.Lang
import javax.inject._

import actors.QuizActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.libs.streams.ActorFlow
import play.api.mvc.{Action, WebSocket}
import play.api.mvc.Results._
import services.VocabularyService

@Singleton
class Quiz @Inject() (vService: VocabularyService) (implicit system: ActorSystem, mat: Materializer) {
	def quiz(sourceLang: Lang, targetLang: Lang) = Action {
		vService.findRandomVocabulary(sourceLang, targetLang) match {
			case Some(_) => Ok
			case None => Conflict
		}
	}

	def check(sourceLang: Lang, word: String, targetLang: Lang, translation: String) = Action { request =>
		val isCorrect = vService.verify(sourceLang, word, targetLang, translation)
		val correctScore = request.session.get("correct").map(_.toInt).getOrElse(0)
		val wrongScore = request.session.get("wrong").map(_.toInt).getOrElse(0)
		if(isCorrect) Ok.withSession(
			"correct" -> (correctScore + 1).toString,
			"wrong" -> wrongScore.toString
		) else NotAcceptable.withSession(
			"correct" -> correctScore.toString,
			"wrong" -> (wrongScore +1).toString
		)
	}

	def quizEndpoint(sourceLang: Lang, targetLang: Lang): WebSocket = {
		WebSocket.accept[String, String] { request =>
			ActorFlow.actorRef { out =>
				QuizActor.props(out, sourceLang, targetLang, vService)
			}
		}
	}
}
