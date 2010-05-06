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
		debug("setSelection(Lessons:" + selection.getNbAllowedLessons() + ")");
		this.selection = selection;
		selection.addObserver(this);
		resetSelectionCounts();
		computeProbas();
	}

	private void resetSelectionCounts() {
		debug("resetSelectionCounts()");
		for (Word w : getSelection().getWords()) {
			// System.out.println();
			// w.printLines();
			w.setCount(0);
			w.setErrorCount(0);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		debug("update() : has been notified !");
		// the selection has been modified
		// => reset the counters
		resetSelectionCounts();
		// => recompute the probas
		computeProbas();
	}

	/**
	 * compute the proba of every word
	 */
	private void computeProbas() {
		debug("computeProbas()");

		// update the max and total word counts
		maxWordCount = 0;
		totalWordsCounts = 0;
		for (Word w : selection.getWords()) {
			int thisCount = w.getCount();
			maxWordCount = Math.max(maxWordCount, thisCount);
			totalWordsCounts += thisCount;
		}

		// once the indices are updated, update the probas
		for (Word w : selection.getWords()) {
			w.computeProba(this);
		}
	}

	/**
	 * @return the average count of a {@link Word} in the list
	 */
	private double getAverageCount() {
		int nbWords = selection.getNbAllowedLessons();
		if (nbWords == 0)
			return 0;
		return getTotalWordsCount() / nbWords;
	}

	public int getMaxWordCount() {
		return maxWordCount;
	}

	public int getTotalWordsCount() {
		return totalWordsCounts;
	}

	/**
	 * @return the number of words known before
	 */
	public int getNbKnownWords() {
		int ans = 0;
		for (Word w : getSelection().getWords()) {
			int count = w.getCount();
			if (count >= 1 && w.getErrorCount() < count)
				ans++;
		}
		return ans;
	}

	int lastQuestionWordIndex;

	/**
	 * get a random {@link Word} according to the proba algorithms
	 * 
	 * @return this {@link Word}
	 */
	public Word getRandomWord_with_probas() {
		debug("getRandomWord_with_probas()");

		if (selection.areAllLessonsForbidden()) {
			System.out
					.println("All the lessons were fordidden, allowing everything.");
			selection.allowAllLessons();
		}

		// compute the sum of the probas
		double sumProba = 0;
		for (Word w : selection.getWords())
			sumProba += w.proba;

		// System.out.println("sum proba:" + sumProba);
		// selection.displayWords();

		// choose a proba
		double choice = Math.random() * sumProba;

		// find the corresponding word
		sumProba = 0;
		for (Word w : selection.getWords()) {
			sumProba += w.proba;
			if (sumProba > choice) {
				lastQuestionWordIndex = w.getIndex();
				return w;
			}
		}

		debug("We didn't find a word matching.");
		return null;
	}

	/**
	 * @return a random {@link Question}
	 */
	public Question getRandomQuestion() {
		debug("getRandomQuestion()");

		Word w = getRandomWord_with_probas();

		// determine the type of question
		int this_chosen_language = chosenLanguage;
		if (chosenLanguage == LANGUAGE_RANDOM)
			this_chosen_language = new Random().nextInt(w.numberOfLanguages);

		// determine the question
		String lessonName = "VOC:" + w.getLessonName();
		String question = w.getForeignWord(this_chosen_language);
		// empty question => next question
		if (question.length() == 0) {
			debug("The word '" + w.toString_onlyWords()
					+ "' makes an empty question.");
			return getRandomQuestion();
		}
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
	public String toString(boolean longVersion) {
		String rep = "";
		String endl = (longVersion ? "\n" : ", ");
		if (longVersion) {
			rep += "Word Picker :\n";
			rep += " - Chosen language : " + chosenLanguage + "\n";
		}
		rep += (longVersion ? " - Number of lessons : " : "Lessons:");
		rep += selection.getNbAllowedLessons() + endl;

		int known = getNbKnownWords();
		int nbWords = selection.getNbAllowedWords();
		rep += (longVersion ? " - Number of words : " : "Words:");
		rep += nbWords + endl;

		if (longVersion) {
			rep += " - Counts: ";
			rep += "total=" + getTotalWordsCount();
			rep += ", max=" + getMaxWordCount();
			rep += " (avg=" + (int) (100f * getAverageCount()) + "%)"
					+ endl;
			rep += " - ";
		}
		rep += "Knonwn:" + known + " = "
				+ (int) (100f * known / nbWords) + "%";
		return rep;
	}

	public void setLastWordKnown() {
		debug("setLastWordKnown()");
		selection.getWord(lastQuestionWordIndex).know(this);
	}

	public void setLastWordUnknown() {
		debug("setLastWordUnknown()");
		selection.getWord(lastQuestionWordIndex).unknow(this);
	}

	public String toString() {
		return toString(true);
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
		// System.out.println(wp.toString(false));
	}
}
