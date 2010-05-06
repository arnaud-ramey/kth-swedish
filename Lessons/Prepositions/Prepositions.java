package Lessons.Prepositions;

import Asker.Question;
import Kvtml.IO.LibUtils;
import Kvtml.VocParser.Word;
import Kvtml.VocParser.WordPicker;

public class Prepositions {
	static String[] PREPOSITIONS = new String[] { //
	"i", //
			"på", //
			"om", //
			"hos", //
			"vid", //
			"från", //
			"till", //
			"med", //
			"att", //
			"av", //
			"upp", //
			"över", //
			"för" //
	};

	/**
	 * @param w
	 *            a chosen {@link Word}
	 * @return the index of the found preposition if any, -1 otherwise
	 */
	private static boolean containsParticle(Word w, int index) {
		String[] words = w.get1().split(" ");
		for (String word : words) {
			// for (int index = 0; index < PREPOSITIONS.length; index++) {
			if (word.equals(PREPOSITIONS[index]))
				return true;
			// return index;
			// }
		}
		return false;
	}

	/**
	 * @return a {@link Question} where the preposition was hidden
	 */
	public static Question randomQuestion(WordPicker wp) {
		Word w = null;
		int pronoun_index = (int) (Math.random() * PREPOSITIONS.length);

		while (true) {
			w = wp.getSelection().getRandomWord();
			if (containsParticle(w, pronoun_index))
				break;
		}

		// replace the preposition with the "____"
		String[] words = w.get1().split(" ");
		String question = "", answer = "";
		for (String word : words) {
			String word_q = word, word_a = word;
			for (String s : PREPOSITIONS) {
				if (word.equals(s)) {
					word_q = Question.UNKNOWN;
					word_a = LibUtils.emphasize(word);
					break;
				}
			}
			question = question + (question.length() > 0 ? " " : "") + word_q;
			answer = answer + (answer.length() > 0 ? " " : "") + word_a;
		}

		// add the translation
		question = question + " (" + w.get0() + ")";
		answer = answer + " (" + w.get0() + ")";

		Question q = new Question("Prepositions", question, answer);
		return q;
	}

	public static void main(String[] args) {
		WordPicker wp = WordPicker.defaultWordPicker(true);
		for (int i = 0; i < 10; i++)
			System.out.println(randomQuestion(wp));
	}
}
