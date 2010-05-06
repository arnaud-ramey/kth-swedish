package Kvtml.VocParser;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import Asker.Question;
import Kvtml.IO.IO;
import Kvtml.VocParser.Lessons.LessonSelection;

public class WordPicker implements Observer {
	public static int LANGUAGE_RANDOM = -1;

	private LessonSelection selection;

	private int chosenLanguage = LANGUAGE_RANDOM;

	/** the max number of times a word was asked */
	private int maxWordCount;

	/** the sum of all the times the words were asked */
	private int totalWordsCounts;

	public void setQuestiontLanguage(int chosenLanguage) {
		debug("setQuestiontLanguage(" + chosenLanguage + ")");
		this.chosenLanguage = chosenLanguage;
	}

	public LessonSelection getSelection() {
		return selection;
	}

	public void setSelection(LessonSelection selection) {
		this.selection = selection;
		selection.addObserver(this);
		compute_probas();
	}

	@Override
	public void update(Observable o, Object arg) {
		debug("update() : has been notified !");
		// the selection has been modified => recompute the probas
		compute_probas();
	}

	/**
	 * compute the proba of every word
	 */
	private void compute_probas() {
		debug("compute_probas()");

		maxWordCount = 0;
		totalWordsCounts = 0;
		// probas.setSize(nbWords());
		for (Word w : selection.getWords()) {
			int thisCount = w.getCount();
			maxWordCount = Math.max(maxWordCount, thisCount);
			totalWordsCounts += thisCount;
			w.computeProba(this);
		}
	}

	/**
	 * @return the average count of a {@link Word} in the list
	 */
	private double getAverageCount() {
		return getTotalWordsCount() / selection.getNbAllowedWords();
	}

	public int getMaxWordCount() {
		return maxWordCount;
	}

	public int getTotalWordsCount() {
		return totalWordsCounts;
	}

	// private double getProba(int i) {
	// // return probas.elementAt(i);
	// return listOfWords.getWord(i).proba;
	// }

	/**
	 * get a random {@link Word} according to the proba algorithms
	 * 
	 * @return this {@link Word}
	 */
	public Word getRandomWord_with_probas() {
		if (selection.areAllLessonsForbidden()) {
			System.out
					.println("All the lessons were fordidden, allowing everything.");
			selection.allowAllLessons();
		}

		// compute the sum of the probas
		double sumProba = 0;
		for (Word w : selection.getWords())
			sumProba += w.proba;

		// choose a proba
		double choice = Math.random() * sumProba;

		// find the corresponding word
		sumProba = 0;
		for (Word w : selection.getWords()) {
			sumProba += w.proba;
			if (sumProba > choice)
				return w;
		}
		return null;
	}

	/**
	 * @return a random {@link Word} in the list
	 */
	public Word getRandomWord() {
		return selection.getRandomWord();
	}

	/**
	 * @return a random {@link Question}
	 */
	public Question getRandomQuestion() {
		Word w = getRandomWord_with_probas();

		// determine the type of question
		int this_chosen_language = chosenLanguage;
		if (chosenLanguage == LANGUAGE_RANDOM)
			this_chosen_language = new Random().nextInt(w.numberOfLanguages);

		// determine the question
		String lessonName = "VOC:" + w.getLessonName();
		String question = w.getForeignWord(this_chosen_language);
		// empty question => next question
		if (question.length() == 0)
			return getRandomQuestion();
		String ans = w.toString_onlyWords();
		Question rep = new Question(lessonName, question, ans);

		// if there is an image, take it
		if (this_chosen_language == 0 && w.containsPicture())
			rep.setImage_question(w.getPictureFilename());
		return rep;
	}

	/**
	 * the string version
	 */
	public String toString() {
		String rep = "Word Picker :\n";
		rep += " - Chosen language : " + chosenLanguage + "\n";
		rep += " - Number of lessons : " + selection.getNbAllowedLessons()
				+ "\n";
		rep += " - Number of words : " + selection.getNbAllowedWords() + "\n";
		rep += " - Counts: ";
		rep += "total=" + getTotalWordsCount();
		rep += ", max=" + getMaxWordCount();
		rep += " (avg=" + ((int) (100f * getAverageCount()) / 100f) + ")";
		return rep;
	}

	public static void debug(String s) {
		IO.debug("WordPicker::" + s);
	}

	/**
	 * @return the default {@link WordPicker}, with the default kvtml loaded
	 */
	public static WordPicker defaultWordPicker(boolean allowAll) {
		WordPicker wp = new WordPicker();
		wp.setSelection(LessonSelection.defaultLessonSelection(allowAll));
		return wp;
	}

	/**
	 * tests
	 */
	public static void main(String[] args) {
		WordPicker wp = WordPicker.defaultWordPicker(false);
		wp.getSelection().setLesson("Es", true);
		System.out.println(wp);
		wp.getSelection().setLesson("Sv", true);
		System.out.println(wp);
		wp.getSelection().setLesson("Sv", false);
		System.out.println(wp);
	}
}
