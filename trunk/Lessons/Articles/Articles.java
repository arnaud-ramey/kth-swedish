package Lessons.Articles;

import Asker.Question;
import Kvtml.IO.LibUtils;
import Kvtml.VocParser.Word;
import Kvtml.VocParser.WordPicker;

public class Articles {
	static String[] ARTICLES = new String[] { "en", "ett" };

	/**
	 * 
	 * @param article_index
	 *            the index of the chosen article
	 * @return a random {@link Word} starting with the chosen article
	 */
	private static Word getRandomWordWithArticle(int article_index) {
		WordPicker wp = WordPicker.defaultWordPicker();
		Word w = null;
		String article = ARTICLES[article_index];
		while (true) {
			w = wp.getRandomWord();
			String[] swe_words = w.get1().split(" ");
			// is a sentence
			if (swe_words.length > 0 && swe_words[0].equals(article))
				return w;
		}
	}

	/**
	 * @return a random {@link Word} starting with a random article
	 */
	private static Word getRandomWord() {
		return getRandomWordWithArticle((int) (Math.random() * ARTICLES.length));
	}

	/**
	 * @return a random {@link Question} about an article
	 */
	public static Question randomQuestion() {
		Word w = getRandomWord();
		String[] words = w.get1().split(" ");
		String question = "", answer = "";

		// replace the articles
		for (String word : words) {
			String word_q = word, word_a = word;
			for (String art : ARTICLES) {
				// replace the articles
				if (word.equals(art)) {
					word_q = Question.UNKNOWN;
					word_a = LibUtils.emphasize(art);
					break;
				}
			}
			question = question + (question.length() > 0 ? " " : "") + word_q;
			answer = answer + (answer.length() > 0 ? " " : "") + word_a;
		}

		// add the translation
		answer = w.get0() + " | " + answer;

		Question q = new Question("articles", question, answer);
		return q;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++)
			// System.out.println(getRandomWord());
			System.out.println(randomQuestion());
	}
}
