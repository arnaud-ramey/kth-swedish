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

	// /**
	// * sets in the {@link JButton} the number of chosen words
	// *
	// * @param validate
	// * the {@link JButton}
	// * @param boxes
	// * the {@link LinkedList} of {@link JCheckbox}es
	// * @param lessons_associated_with_boxes
	// * the index of the lessons corresponding to each
	// * {@link JCheckbox}
	// */
	// public void refreshButton(JButton validate, LinkedList<JCheckBox> boxes,
	// LinkedList<Integer> lessons_associated_with_boxes) {
	// int words_number = 0;
	// for (int i = 0; i < boxes.size(); i++) {
	// int lesson_nb = lessons_associated_with_boxes.get(i);
	// if (boxes.get(i).isSelected())
	// words_number += listOfWords.getNumberOfWordsInLesson(lesson_nb);
	// }
	// validate.setText("OK [" + words_number + " words]");
	// }
	//
	// /**
	// * make the buttons to choose the lesson
	// *
	// */
	// public void vocLesson_makeButtons(final VisualAsker jp) {
	// int ITEMS_PER_LINE = 3;
	// final JButton validate = new JButton();
	//
	// /* create JCheckBoxes */
	// final LinkedList<JCheckBox> boxes = new LinkedList<JCheckBox>();
	// final LinkedList<Integer> lessons_associated_with_boxes = new
	// LinkedList<Integer>();
	// int counter = 0;
	// Color color_bck = null;
	//
	// System.out.println(listOfWords.lessons);
	//
	// for (int i = 0; i < listOfWords.lessons.size(); i++) {
	// int nb_in_lesson = listOfWords.getNumberOfWordsInLesson(i);
	// if (nb_in_lesson == 0)
	// continue;
	// String title = listOfWords.lessons.get(i);
	// title = title.replace("[", "").replace("]", "");
	// title = title.replace("Lesson ", "L");
	// title = title + " [" + nb_in_lesson + "]";
	// title = LibUtils.firstLetterUpperCase_otherLowerCase(title);
	// JCheckBox jc = new JCheckBox(title, true);
	// jc.setSelected(true);
	// jc.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
	//
	// // add an action listener to change the OK button
	// jc.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent arg0) {
	// refreshButton(validate, boxes,
	// lessons_associated_with_boxes);
	// };
	// });
	//
	// // give a color per line
	// boolean color_idx = (counter++ % (2 * ITEMS_PER_LINE) < ITEMS_PER_LINE);
	// color_bck = (color_idx ? VisualAsker.BLUE : VisualAsker.YELLOW);
	// jc.setBackground(color_bck);
	//
	// // add the box
	// boxes.add(jc);
	// lessons_associated_with_boxes.add(i);
	// }
	//
	// /* put the good background color */
	// jp.setBackground(color_bck);
	//
	// /* create buttons to check and uncheck everything */
	// JButton uncheck_all = new JButton("Select all/none");
	// uncheck_all.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// int counter = 0;
	// for (JCheckBox j : boxes)
	// counter += (j.isSelected() ? 1 : -1);
	// boolean new_value = (counter < 0);
	// for (JCheckBox j : boxes)
	// j.setSelected(new_value);
	// refreshButton(validate, boxes, lessons_associated_with_boxes);
	// }
	// });
	//
	// /* create the question language list */
	// Vector<String> possibilities = new Vector<String>();
	// for (String language : listOfWords.getLanguages())
	// possibilities.add(language + "-> other languages");
	// possibilities.add("Random language");
	//
	// final JComboBox list = new JComboBox(possibilities);
	// list.setSelectedIndex(list.getItemCount() - 1);
	// list.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// setQuestiontLanguage(list.getSelectedIndex());
	// // if random item selected => set random
	// if (list.getSelectedIndex() == list.getItemCount() - 1)
	// setQuestiontLanguage(LANGUAGE_RANDOM);
	// }
	// });
	//
	// /* create the validate button */
	// validate.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// forbidAllLessons();
	// for (int i = 0; i < boxes.size(); i++) {
	// int lesson_nb = lessons_associated_with_boxes.get(i);
	// if (boxes.get(i).isSelected())
	// setLesson(lesson_nb, true);
	// }
	// // System.out.println(jp.getQueue().wp);
	// jp.getQueue().clearQueueAndRepopulate();
	// jp.makeButtons();
	// }
	// });
	//
	// /* add everything to a list */
	// LinkedList<JComponent> components = new LinkedList<JComponent>();
	// components.add(validate);
	// components.add(list);
	// components.add(uncheck_all);
	// components.addAll(boxes);
	//
	// /* add all the elements of the list to the panel */
	// JPanel jp_with_scroll = new JPanel();
	// JScrollPane scrollPane = new JScrollPane(jp_with_scroll);
	// scrollPane.getVerticalScrollBar().setUnitIncrement(20);
	//
	// jp_with_scroll.setLayout(new GridBagLayout());
	// GridBagConstraints c = new GridBagConstraints();
	// c.fill = GridBagConstraints.BOTH;
	// c.weighty = 1;
	// c.weightx = 1;
	// c.gridy = 0;
	// c.gridx = 0;
	//
	// // create button
	// for (int i = 0; i < components.size(); i++) {
	// int i_lessons = i - 2; // 1 for the first lesson
	// jp_with_scroll.add(components.get(i), c);
	// if (i_lessons % ITEMS_PER_LINE == 0) {
	// c.gridy++;
	// c.gridx = 0;
	// } else
	// c.gridx++;
	// } // end for button
	//
	// jp.removeAll();
	// jp.setLayout(new GridBagLayout());
	// jp.add(scrollPane, c);
	// refreshButton(validate, boxes, lessons_associated_with_boxes);
	// jp.validate();
	// }

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
