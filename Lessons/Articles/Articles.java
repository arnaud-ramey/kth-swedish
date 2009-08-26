package Lessons.Articles;

import Asker.Question;
import IO.LibUtils;
import Lessons.VocParser.Word;
import Lessons.VocParser.WordPicker;

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
		String article = ARTICLES[article_index] + " ";
		while (true) {
			w = wp.getRandomWord();
			String swe = w.get1();
			// is a sentence
			if (swe.startsWith(article))
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
		String question = w.get1();
		String answer = w.toString_onlyWords();
		// replace the articles
		for (String art : ARTICLES) {
			question = question.replace(art + " ", Question.UNKNOWN + " ");
			answer = answer.replace(art + " ", LibUtils.emphasize(art) + " ");
		}

		Question q = new Question("articles", question, answer);
		return q;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++)
			// System.out.println(getRandomWord());
			System.out.println(randomQuestion());
	}
}
