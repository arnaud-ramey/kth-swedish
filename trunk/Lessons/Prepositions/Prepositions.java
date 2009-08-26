package Lessons.Prepositions;

import Asker.Question;
import IO.LibUtils;
import Lessons.VocParser.Word;
import Lessons.VocParser.WordPicker;

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
	private static int containsParticle(Word w) {
		String[] words = w.get1().split(" ");
		for (String word : words) {
			for (int i = 0; i < PREPOSITIONS.length; i++) {
				if (word.equals(PREPOSITIONS[i]))
					return i;
			}
		}
		return -1;
	}

	/**
	 * @return a {@link Question} where the preposition was hidden
	 */
	public static Question randomQuestion() {
		WordPicker wp = WordPicker.defaultWordPicker();
		Word w = null;
		int pronoun_index;
		while (true) {
			w = wp.getRandomWord();
			pronoun_index = containsParticle(w);
			if (pronoun_index != -1)
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
		for (int i = 0; i < 10; i++)
			System.out.println(randomQuestion());
	}
}
