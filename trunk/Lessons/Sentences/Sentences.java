package Lessons.Sentences;

import Asker.Question;
import Kvtml.VocParser.Word;
import Kvtml.VocParser.WordPicker;

public class Sentences {
	private static String[] endOfSentences = new String[] { ".", " !", " ?" };

	private static int MINIMUM_WORDS = 6;

	private static Word randomSentence(WordPicker wp) {
		Word w = null;
		boolean OK = false;

		while (!OK) {
			OK = false;
			w = wp.getSelection().getRandomWord();
			String swe = w.get1();

			// ends with a mark of end of sentence
			for (String s : endOfSentences) {
				if (swe.endsWith(s)) {
					OK = true;
					break;
				}
			}
			if (!OK)
				continue;

			// min number of words
			int nb_words = swe.split(" ").length;
			if (nb_words < MINIMUM_WORDS)
				OK = false;
		}

		return w;
	}

	/**
	 * shuffle an array (permutes all the things)
	 * 
	 * @param arr
	 *            the name of the array
	 */
	public static void shuffleArray(String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			int index1 = i % arr.length;
			int index2 = (int) (Math.random() * arr.length);
			String temp = arr[index1];
			arr[index1] = arr[index2];
			arr[index2] = temp;
		}
	}

	/**
	 * @return a {@link Question} with a sentence in swedish were the words have
	 *         been shuffled
	 */
	public static Question randomShuffleSentence(WordPicker wp) {
		Word word = randomSentence(wp);
		/* prepare the split /= */
		String answer = word.get1();
		String sentence = answer;
		sentence = sentence.replace("...", " ...");
		if (sentence.endsWith(".") && !sentence.endsWith("..."))
			sentence = sentence.substring(0, sentence.length() - 1) + " .";

		/* split and shuffle */
		String[] words = sentence.split(" ");
		shuffleArray(words);

		// make the question
		Question q = new Question();
		q.lesson = "Mixed words";
		q.answer = word.get0() + " | " + answer;
		for (String s : words)
			q.question += s + " ";
		return q;
	}

	/**
	 * @return a {@link Question} with the swedish to translate
	 */
	public static Question randomQuestion(WordPicker wp) {
		Word w = randomSentence(wp);
		Question q = new Question("Sentences", w.get0(), w.get1());
		return q;
	}

	public static void main(String[] args) {
		WordPicker wp = WordPicker.defaultWordPicker(true);
		// System.out.println(randomSentence().get1());
		System.out.println(randomShuffleSentence(wp));
		System.out.println();
		System.out.println(randomQuestion(wp));

		// String[] arr = new String[] { "1", "2", "3", "4", "5","6", "7", "8",
		// "9", "10" };
		// shuffleArray(arr);
		// for (String s : arr) System.out.println( s);
	}
}
