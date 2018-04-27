package actors

import akka.actor.{Actor, ActorRef, Props}
import play.api.i18n.Lang
import services.VocabularyService

class QuizActor(out: ActorRef,
								sourceLang: Lang,
								targetLang: Lang,
								vService: VocabularyService) extends Actor{
	private var word = ""

	override def preStart(): Unit = sendWord()
	override def receive: Receive = {
		case translation: String if vService.verify(sourceLang, word, targetLang, translation) =>
			out ! "Correct"
		case _ => out ! "Incorrect, try again"
	}

	def sendWord() = {
		vService.findRandomVocabulary(sourceLang, targetLang).map{ v =>
			out ! s"Please, translate this: ${v.word}"
			word = v.word
		}.getOrElse{
			out ! s"I don't know any words for ${sourceLang.code}"
		}
	}
}

object QuizActor {
	def props(out: ActorRef,
						sourceLang: Lang,
						targetLang: Lang,
						vSerivce: VocabularyService): Props = {
		Props(classOf[QuizActor], out, sourceLang, targetLang, vSerivce)
	}
}
