package Asker;

import java.util.LinkedList;

import Kvtml.IO.IO;
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
	private static int TYPE_OF_QUESTION_RANDOM = -1;

	private LinkedList<Question> queue = new LinkedList<Question>();

	public WordPicker wp = null;

	private int type_of_question = TYPE_OF_QUESTION_RANDOM;

	public static int VOCABULARY = 1;

	public static String[] possible_type_of_questions = {
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
	 * constructor
	 */
	public QuestionQueue() {
	}

	private void checkWordPickerStarted() {
		if (wp == null)
			setDefaultWordPicker();
	}

	/**
	 * fill the queue with askable words
	 */
	private void repopulateQueue() {
		debug("repopulateQueue()");
		// System.out.println("type_of_question:" + type_of_question);

		while (queue.size() < WORD_QUEUE_SIZE) {
			int this_type_of_question = type_of_question;
			if (this_type_of_question == 0
					|| this_type_of_question == TYPE_OF_QUESTION_RANDOM)
				this_type_of_question = 1 + (int) ((possible_type_of_questions.length - 1) * Math
						.random());
			debug("Finding a question of type " + this_type_of_question + " ("
					+ possible_type_of_questions[this_type_of_question] + ")");
			Question new_question = new Question();

			// choose a new question and look if it is new
			do {
				if (this_type_of_question == VOCABULARY) {
					checkWordPickerStarted();
					new_question = wp.getRandomQuestion();
				}
				if (this_type_of_question == 2) {
					checkWordPickerStarted();
					new_question = WordImagePicker.randomQuestion(wp);
				}
				if (this_type_of_question == 3) {
					checkWordPickerStarted();
					new_question = Sentences.randomQuestion(wp);
				}
				if (this_type_of_question == 4) {
					checkWordPickerStarted();
					new_question = Sentences.randomShuffleSentence(wp);
				}
				if (this_type_of_question == 5)
					new_question = NumberTranslator.randomQuestion(20);
				if (this_type_of_question == 6)
					new_question = NumberTranslator.randomQuestion(2100);
				if (this_type_of_question == 7)
					new_question = RankingTranslator.randomQuestion(31);
				if (this_type_of_question == 8)
					new_question = RankingTranslator.randomQuestion(1000);
				if (this_type_of_question == 9)
					new_question = HourTranslator.randomQuestion();
				if (this_type_of_question == 10)
					new_question = Verb.randomVerb();
				if (this_type_of_question == 11)
					new_question = Pronouns.randomQuestion();
				if (this_type_of_question == 12) {
					checkWordPickerStarted();
					new_question = Prepositions.randomQuestion(wp);
				}
				if (this_type_of_question == 13) {
					checkWordPickerStarted();
					new_question = Articles.randomQuestion(wp);
				}

				// System.out.println(new_question);

			} while (queue.contains(new_question));
			queue.addLast(new_question);
		}
	}

	/**
	 * clear the queue and repopulate it
	 */
	public void clearQueueAndRepopulate() {
		debug("clearQueueAndRepopulate()");
		queue.clear();
		repopulateQueue();
	}

	/**
	 * change the loaded lesson
	 */
	public void setTypeOfQuestion(int newType) {
		// System.out.println("setLesson()" + l);
		type_of_question = newType;
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
		setWordPicker(WordPicker.defaultWordPicker(true));
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
		// clearQueueAndRepopulate();
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

	public static void debug(String s) {
		IO.debug("QuestionQueue::" + s);
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
		qq.wp.getSelection().forbidAllLessons();
		qq.wp.getSelection().setLesson("Aug", true);
		qq.clearQueueAndRepopulate();

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
