package services

import javax.inject.Singleton

import model.Vocabulary
import play.api.i18n.Lang

@Singleton
class VocabularyService {

	private var allVocabulary = List (
		Vocabulary(Lang("en"), Lang("fr"), "hello", "bonjour"),
		Vocabulary(Lang("en"), Lang("fr"), "play", "jouer")
	)

	def addVocabulary(v: Vocabulary): Boolean = {
		if(!allVocabulary.contains(v)) {
			allVocabulary = v :: allVocabulary
			true
		} else false
	}

}
