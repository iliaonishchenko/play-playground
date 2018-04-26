package controllers

import javax.inject.Inject

import model.Vocabulary
import play.api.i18n.Lang
import play.api.mvc._
import services.VocabularyService

class Import @Inject() (vService: VocabularyService) extends Controller {
	def importWord(sourceLanguage: Lang,
								 targetLanguage: Lang,
								 word: String,
								 translation: String) =
		Action { _ =>
			val added = vService.addVocabulary(Vocabulary(sourceLanguage, targetLanguage, word, translation))
			if (added) Ok else Conflict
		}
}
