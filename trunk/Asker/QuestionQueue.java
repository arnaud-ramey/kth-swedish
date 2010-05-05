package Asker;

import java.util.LinkedList;

import Kvtml.VocParser.WordImagePicker;
import Kvtml.VocParser.WordPicker;
import Lessons.Articles.Articles;
import Lessons.Numbers.HourTranslator;
import Lessons.Numbers.NumberTranslator;
import Lessons.Numbers.RankingTranslator;
import Lessons.Prepositions.Prepositions;
import Lessons.Pronouns.Pronouns;
import Lessons.Sentences.Sentences;
import Lessons.Verbs.Verb;

public class QuestionQueue {
	static int WORD_QUEUE_SIZE = 5;

	private LinkedList<Question> queue = new LinkedList<Question>();

	public WordPicker wp;

	private int type_of_question = -1;

	public static int VOCABULARY = 1;

	public static String[] possible_lessons = {
			"0: random questions in all the lessons",//
			"1: vocabulary",//
			"2: vocabulary (only images)",//
			"3: vocabulary (only sentences)", //
			"4: shuffled sentences", //
			"5: numbers to 20",//
			"6: numbers to 2100", //
			"7: ranks to 31",//
			"8: ranks to 1000",//
			"9: hours", //
			"10: irregular verbs", //
			"11: pronouns",//
			"12: prepositions", //
			"13: articles", //

	};

	/**
	 * fill the queue with askable words
	 */
	private void repopulateQueue() {
		// System.out.println("repopulateQueue()");
		// System.out.println("type_of_question:" + type_of_question);

		while (queue.size() < WORD_QUEUE_SIZE) {
			int l = type_of_question;
			if (l == 0 || l == -1)
				l = 1 + (int) ((possible_lessons.length - 1) * Math.random());
			Question new_question = new Question();

			// choose a new question and look if it is new
			do {
				if (l == VOCABULARY)
					new_question = wp.getRandomQuestion();
				if (l == 2)
					new_question = WordImagePicker.randomQuestion();
				if (l == 3)
					new_question = Sentences.randomQuestion();
				if (l == 4)
					new_question = Sentences.randomShuffleSentence();
				if (l == 5)
					new_question = NumberTranslator.randomQuestion(20);
				if (l == 6)
					new_question = NumberTranslator.randomQuestion(2100);
				if (l == 7)
					new_question = RankingTranslator.randomQuestion(31);
				if (l == 8)
					new_question = RankingTranslator.randomQuestion(1000);
				if (l == 9)
					new_question = HourTranslator.randomQuestion();
				if (l == 10)
					new_question = Verb.randomVerb();
				if (l == 11)
					new_question = Pronouns.randomQuestion();
				if (l == 12)
					new_question = Prepositions.randomQuestion();
				if (l == 13)
					new_question = Articles.randomQuestion();

				// System.out.println(new_question);

			} while (queue.contains(new_question));
			queue.addLast(new_question);
		}
	}

	/**
	 * clear the queue and repopulate it
	 */
	public void clearQueueAndRepopulate() {
		// System.out.println("clearQueueAndRepopulate()");
		queue.clear();
		repopulateQueue();
	}

	/**
	 * constructor
	 */
	public QuestionQueue() {
	}

	/**
	 * change the loaded lesson
	 */
	public void setLesson(int l) {
		// System.out.println("setLesson()" + l);
		type_of_question = l;
		/* load the word picker if needed */
		if ((type_of_question == 1 || type_of_question == 0) && wp == null)
			setDefaultWordPicker();
		clearQueueAndRepopulate();
	}

	/**
	 * get the type of questions
	 */
	public int getTypeOfQuestion() {
		return type_of_question;
	}

	/**
	 * load a default word picker
	 */
	public void setDefaultWordPicker() {
		// System.out.println("setDefaultWordPicker()");
		setWordPicker(WordPicker.defaultWordPicker());
	}

	/**
	 * change the current {@link WordPicker}
	 * 
	 * @param wp
	 *            the new one
	 */
	public void setWordPicker(WordPicker wp) {
		// System.out.println("setWordPicker() - " + wp.toString());
		this.wp = wp;
		clearQueueAndRepopulate();
	}

	/**
	 * @return the first {@link Question} of the queue
	 */
	public Question getQuestion() {
		return queue.getFirst();
	}

	/**
	 * declare the {@link Question} as known, and remove it from the queue
	 * 
	 */
	public void declareQuestion_known() {
		queue.removeFirst();
		repopulateQueue();
	}

	/**
	 * declare the {@link Question} as unknown, and remove it from the queue
	 * 
	 */
	public void declareQuestion_unknown() {
		Question q = queue.pollFirst();
		queue.addLast(q);
	}

	/**
	 * the string version
	 */
	public String toString() {
		String rep = "";
		for (Question q : queue)
			rep += " * " + q + "\n";
		return rep;
	}

	/**
	 * tests
	 */
	public static void main(String[] args) {
		QuestionQueue qq = new QuestionQueue();
		qq.setDefaultWordPicker();
		qq.wp.forbidAllLessons();
		qq.wp.setLesson(0, true);
		// qq.clearQueueAndRepopulate();

		System.out.println(qq);
		System.out.println("-----------------------------");

		qq.declareQuestion_known();
		System.out.println(qq);
		System.out.println("-----------------------------");

		qq.declareQuestion_unknown();
		System.out.println(qq);
		System.out.println("-----------------------------");
	}
}
