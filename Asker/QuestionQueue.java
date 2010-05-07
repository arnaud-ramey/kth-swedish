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
	public static int TYPE_OF_QUESTION_UNDEFINED = -1;
	public static int TYPE_OF_QUESTION_RANDOM = 0;
	public static int TYPE_OF_QUESTION_VOCABULARY = 1;

	private LinkedList<Question> queue = new LinkedList<Question>();

	public WordPicker wp = null;

	private int typeOfQuestion = TYPE_OF_QUESTION_UNDEFINED;

	private int nbQuestionsDone, nbQuestionsKnown;

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
		typeOfQuestion = TYPE_OF_QUESTION_UNDEFINED;
	}

	public void checkWordPickerStarted() {
		if (wp == null)
			setDefaultWordPicker(true);
	}

	/**
	 * fill the queue with askable words
	 */
	private void repopulateQueue() {
		debug("repopulateQueue()");
		// System.out.println("type_of_question:" + type_of_question);

		while (queue.size() < WORD_QUEUE_SIZE) {
			int this_type_of_question = typeOfQuestion;
			if (this_type_of_question == TYPE_OF_QUESTION_UNDEFINED
					|| this_type_of_question == TYPE_OF_QUESTION_RANDOM)
				this_type_of_question = 1 + (int) ((possible_type_of_questions.length - 1) * Math
						.random());
			debug("Finding a question of type " + this_type_of_question + " ("
					+ possible_type_of_questions[this_type_of_question] + ")");
			Question new_question = null;
			boolean isQuestionOK = false;

			// choose a new question and look if it is new
			while (!isQuestionOK) {
				if (this_type_of_question == TYPE_OF_QUESTION_VOCABULARY) {
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
				isQuestionOK = (queue.contains(new_question) == false);
				if (!isQuestionOK)
					debug("The question " + new_question.toString(false)
							+ " was already in the queue ! Getting a new one.");
			} // end of OK loop

			queue.addLast(new_question);
		} // end of while queue not long enough
	}

	/**
	 * clear the queue and repopulate it
	 */
	public void clearQueueAndRepopulate() {
		debug("clearQueueAndRepopulate()");
		resetStats();
		queue.clear();
		repopulateQueue();
	}

	/**
	 * change the loaded lesson
	 */
	public void setTypeOfQuestion(int newType) {
		// System.out.println("setLesson()" + l);
		typeOfQuestion = newType;
		// clearQueueAndRepopulate();
	}

	/**
	 * get the type of questions
	 */
	public int getTypeOfQuestion() {
		return typeOfQuestion;
	}

	/**
	 * load a default word picker
	 */
	public void setDefaultWordPicker(boolean allowAll) {
		debug("setDefaultWordPicker()");
		setWordPicker(WordPicker.defaultWordPicker(allowAll));
	}

	/**
	 * change the current {@link WordPicker}
	 * 
	 * @param wp
	 *            the new one
	 */
	public void setWordPicker(WordPicker wp) {
		debug("setWordPicker() - " + wp.toString(false));
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
		nbQuestionsDone++;
		nbQuestionsKnown++;
		if (typeOfQuestion == TYPE_OF_QUESTION_VOCABULARY) {
			int wordIdx = Integer.parseInt(queue.peekFirst().userObject
					.toString());
			wp.setWordKnown(wordIdx);
		}

		queue.removeFirst();
		repopulateQueue();
	}

	/**
	 * declare the {@link Question} as unknown, and remove it from the queue
	 * 
	 */
	public void declareQuestion_unknown() {
		nbQuestionsDone++;
		if (typeOfQuestion == TYPE_OF_QUESTION_VOCABULARY) {
			int wordIdx = Integer.parseInt(queue.peekFirst().userObject
					.toString());
			wp.setWordUnknown(wordIdx);
		}

		Question q = queue.pollFirst();
		queue.addLast(q);
	}

	public static void debug(String s) {
		IO.debug("QuestionQueue::" + s);
	}

	/**
	 * @return the percentage of known answers
	 */
	private int shareKnown() {
		if (nbQuestionsDone == 0)
			return 100;
		return (int) (100f * nbQuestionsKnown / nbQuestionsDone);
	}

	public void resetStats() {
		debug("resetStats()");
		nbQuestionsDone = 0;
		nbQuestionsKnown = 0;
		if (typeOfQuestion == TYPE_OF_QUESTION_VOCABULARY)
			wp.resetSelectionCounts();
	}

	/**
	 * @return a {@link String} with some stats
	 */
	public String info() {
		String rep = "";
		rep += "Done:" + nbQuestionsDone;
		rep += ", Known:" + nbQuestionsKnown;
		rep += " = " + shareKnown() + "%";
		if (typeOfQuestion == TYPE_OF_QUESTION_VOCABULARY)
			rep += ", " + wp.toString(false);
		return rep;
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
		qq.setDefaultWordPicker(false);
		qq.wp.getSelection().setLesson("Aug.", true);
		// qq.setTypeOfQuestion(TYPE_OF_QUESTION_VOCABULARY);
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
