package Lessons.Articles;

import Asker.Question;
import Lessons.VocParser.Word;
import Lessons.VocParser.WordPicker;

public class Articles {
	private static Word getRandomWord() {
		WordPicker wp = WordPicker.defaultWordPicker();
		Word w = null;
		while (true) {
			w = wp.getRandomWord();
			String swe = w.get1();
			// ends with a point
			if (!swe.startsWith("en ") && !swe.startsWith("ett "))
				continue;
			break;
		}

		return w;
	}
	
	public static Question randomQuestion() {
		Word w = getRandomWord();
		String only_word = w.get1();
		only_word = only_word.replace("en ", Question.UNKNOWN + " ");
		only_word = only_word.replace("ett ", Question.UNKNOWN + " ");
		
		Question q = new Question();
		q.question = only_word;
		q.answer = w.toString_onlyWords();
		q.lesson = "articles";
		return q;
	}

	public static void main(String[] args) {
		System.out.println(getRandomWord());
		System.out.println(randomQuestion());
	}
}
