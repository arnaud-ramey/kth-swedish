package Lessons.Prepositions;

import Asker.Question;
import Lessons.VocParser.Word;
import Lessons.VocParser.WordPicker;

public class Prepositions {
	static String[] PREPOSITIONS = new String[] { "i", "på", "vid", "om",
			"hos", "från", "till", "med" };

	/**
	 * @param w
	 *            a chosen {@link Word}
	 * @return the index of the found preposition if any, -1 otherwise
	 */
	private static int containsParticle(Word w) {
		for (int i = 0; i < PREPOSITIONS.length; i++) {
			if (w.get1().contains(addSpaces(PREPOSITIONS[i])))
				return i;
		}
		return -1;
	}

	/**
	 * @param s
	 *            a {@link String}
	 * @return " " + s + " "
	 */
	private static String addSpaces(String s) {
		return " " + s + " ";
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

		String question = w.get1();
		for (String s : PREPOSITIONS)
			question = question.replace(addSpaces(s),
					addSpaces(Question.UNKNOWN));
		// question = question.replace(addSpaces(PREPOSITIONS[pronoun_index]),
		// addSpaces(Question.UNKNOWN));
		question = question + " (" + w.get0() + ")";

		String answer = w.get1();
		// answer = answer.replace(addSpaces(PREPOSITIONS[pronoun_index]),
		// addSpaces(
		// PREPOSITIONS[pronoun_index]).toUpperCase());
		// answer = w.get0() + " | " + answer;

		Question q = new Question("Prepositions", question, answer);
		return q;
	}

	public static void main(String[] args) {
		System.out.println(randomQuestion());
	}
}
