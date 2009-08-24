package Lessons.Articles;

import Asker.Question;
import IO.LibUtils;
import Lessons.VocParser.Word;
import Lessons.VocParser.WordPicker;

public class Articles {
	static String[] ARTICLES = new String[] { "en", "ett" };

	private static Word getRandomWord() {
		WordPicker wp = WordPicker.defaultWordPicker();
		Word w = null;
		while (true) {
			w = wp.getRandomWord();
			String swe = w.get1();
			// is a sentence
			for (String art : ARTICLES)
			if (swe.startsWith(art + " "))
				return w;
		}
	}

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
		System.out.println(getRandomWord());
		System.out.println(randomQuestion());
	}
}
