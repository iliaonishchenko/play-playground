package controllers

import play.api.i18n.Lang
import javax.inject.Inject

import play.api.mvc.Action
import play.api.mvc.Results._
import services.VocabularyService

class Quiz @Inject() (vService: VocabularyService) {
	def quiz(sourceLang: Lang, targetLang: Lang) = Action {
		vService.findRandomVocabulary(sourceLang, targetLang) match {
			case Some(_) => Ok
			case None => Conflict
		}
	}

	def check(sourceLang: Lang, word: String, targetLang: Lang, translation: String) = Action {
		if(vService.verify(sourceLang, word, targetLang, translation)) Ok else NotAcceptable
	}
}
