package Kvtml.VocParser;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Asker.Question;
import Asker.Visual.VisualAsker;
import Kvtml.IO.LibUtils;

public class WordPicker {
	public ListOfWords listOfWords;

	private Vector<Boolean> allowedLessons = new Vector<Boolean>();

	public static int LANGUAGE_RANDOM = -1;

	private int chosenLanguage = LANGUAGE_RANDOM;

	public void setQuestiontLanguage(int chosenLanguage) {
		this.chosenLanguage = chosenLanguage;
	}

	/**
	 * change the current list of words
	 * 
	 * @param filename
	 *            the filename
	 */
	public void setListofWords(String filename) {
		listOfWords = new ListOfWords();
		listOfWords.readFile(filename);
		setListofWords(listOfWords);
	}

	/**
	 * change the current list of words
	 * 
	 * @param w
	 *            the new {@link ListOfWords}
	 */
	public void setListofWords(ListOfWords w) {
		listOfWords = w;
		allowedLessons.setSize(listOfWords.lessons.size());
		allowAllLessons();
		compute_probas();
	}

	/**
	 * activate a lesson
	 * 
	 * @param i
	 *            the lesson number
	 */
	public void setLesson(int i, boolean activated) {
		allowedLessons.set(i, activated);
	}

	/**
	 * activate all lesson
	 */
	public void allowAllLessons() {
		for (int i = 0; i < allowedLessons.size(); i++)
			setLesson(i, true);
	}

	/**
	 * forbid all lesson
	 */
	public void forbidAllLessons() {
		for (int i = 0; i < allowedLessons.size(); i++)
			setLesson(i, false);
	}

	/**
	 * @return true if all the lessons are forbidden
	 */
	public boolean areAllLessonsForbidden() {
		for (boolean b : allowedLessons) {
			if (b == true)
				return false;
		}
		return true;
	}

	/**
	 * @param w
	 *            a {@link Word}
	 * @return true if the lesson of w is compatible with the selected ones
	 */
	public boolean words_match_lessons(Word w) {
		return allowedLessons.get(w.getLessonNumber());
	}

	/**
	 * compute the proba of every word
	 */
	private void compute_probas() {
		// probas.setSize(nbWords());
		for (Word w : listOfWords.getWords())
			w.computeProba();
	}

	private double getProba(int i) {
		// return probas.elementAt(i);
		return listOfWords.getWord(i).proba;
	}

	/**
	 * get a random {@link Word} according to the proba algorithms
	 * 
	 * @return this {@link Word}
	 */
	private Word getRandomWord_all_lessons_with_probas() {
		double sumProba = 0;
		for (Word w : listOfWords.getWords())
			sumProba += w.proba;

		double choice = Math.random() * sumProba;

		sumProba = 0;
		int chosenWord;
		for (chosenWord = 0; chosenWord < listOfWords.nbWords(); chosenWord++) {
			sumProba += getProba(chosenWord);
			if (sumProba > choice)
				break;
		}

		return listOfWords.getWord(chosenWord);
	}

	/**
	 * @return a random {@link Word} in the list
	 */
	public Word getRandomWord() {
		return listOfWords.getRandomWord();
	}

	/**
	 * get a random {@link Word} according to the probas and the allowed lessons
	 * 
	 * @return this {@link Word}
	 */
	public Word getRandomWord_selected_lessons_with_probas() {
		if (areAllLessonsForbidden()) {
			System.out
					.println("All the lessons were fordidden, allowing everything.");
			allowAllLessons();
		}

		Word w;
		do {
			w = getRandomWord_all_lessons_with_probas();
			// System.out.println("w:" + w);
		} while (!words_match_lessons(w));
		return w;
	}

	/**
	 * @return a random {@link Question}
	 */
	public Question getRandomQuestion() {
		Word w = getRandomWord_selected_lessons_with_probas();

		// determine the type of question
		int this_chosen_language = chosenLanguage;
		if (chosenLanguage == LANGUAGE_RANDOM)
			this_chosen_language = new Random().nextInt(w.numberOfLanguages);

		// determine the question
		String question = w.getForeignWord(this_chosen_language);
		if (question.length() == 0)
			return getRandomQuestion();
		Question rep = new Question("VOC " + w.lesson_name, question, w
				.toString_onlyWords());

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
		for (int i = 0; i < allowedLessons.size(); i++) {
			rep += " * " //
					// + i + "=" //
					+ listOfWords.lessons.elementAt(i)
					+ " : "
					+ (allowedLessons.elementAt(i) ? "1" : "0") + "\n";
		}
		return rep;
	}

	/**
	 * @return the default {@link WordPicker}
	 */
	public static WordPicker defaultWordPicker() {
		WordPicker wp = new WordPicker();
		wp.setListofWords(ListOfWords.defaultListOfWords());
		return wp;
	}

	/**
	 * sets in the {@link JButton} the number of chosen words
	 * 
	 * @param validate
	 *            the {@link JButton}
	 * @param boxes
	 *            the {@link LinkedList} of {@link JCheckbox}es
	 * @param lessons_associated_with_boxes
	 *            the index of the lessons corresponding to each
	 *            {@link JCheckbox}
	 */
	public void refreshButton(JButton validate, LinkedList<JCheckBox> boxes,
			LinkedList<Integer> lessons_associated_with_boxes) {
		int words_number = 0;
		for (int i = 0; i < boxes.size(); i++) {
			int lesson_nb = lessons_associated_with_boxes.get(i);
			if (boxes.get(i).isSelected())
				words_number += listOfWords.getNumberOfWordsInLesson(lesson_nb);
		}
		validate.setText("OK [" + words_number + " words]");
	}

	/**
	 * make the buttons to choose the lesson
	 * 
	 */
	public void vocLesson_makeButtons(final VisualAsker jp) {
		int ITEMS_PER_LINE = 3;
		final JButton validate = new JButton();

		/* create JCheckBoxes */
		final LinkedList<JCheckBox> boxes = new LinkedList<JCheckBox>();
		final LinkedList<Integer> lessons_associated_with_boxes = new LinkedList<Integer>();
		int counter = 0;
		Color color_bck = null;

		System.out.println(listOfWords.lessons);

		for (int i = 0; i < listOfWords.lessons.size(); i++) {
			int nb_in_lesson = listOfWords.getNumberOfWordsInLesson(i);
			if (nb_in_lesson == 0)
				continue;
			String title = listOfWords.lessons.get(i);
			title = title.replace("[", "").replace("]", "");
			title = title.replace("Lesson ", "L");
			title = title + " [" + nb_in_lesson + "]";
			title = LibUtils.firstLetterUpperCase_otherLowerCase(title);
			JCheckBox jc = new JCheckBox(title, true);
			jc.setSelected(true);
			jc.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));

			// add an action listener to change the OK button
			jc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					refreshButton(validate, boxes,
							lessons_associated_with_boxes);
				};
			});

			// give a color per line
			boolean color_idx = (counter++ % (2 * ITEMS_PER_LINE) < ITEMS_PER_LINE);
			color_bck = (color_idx ? VisualAsker.BLUE : VisualAsker.YELLOW);
			jc.setBackground(color_bck);

			// add the box
			boxes.add(jc);
			lessons_associated_with_boxes.add(i);
		}

		/* put the good background color */
		jp.setBackground(color_bck);

		/* create buttons to check and uncheck everything */
		JButton uncheck_all = new JButton("Select all/none");
		uncheck_all.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int counter = 0;
				for (JCheckBox j : boxes)
					counter += (j.isSelected() ? 1 : -1);
				boolean new_value = (counter < 0);
				for (JCheckBox j : boxes)
					j.setSelected(new_value);
				refreshButton(validate, boxes, lessons_associated_with_boxes);
			}
		});

		/* create the question language list */
		Vector<String> possibilities = new Vector<String>();
		for (String language : listOfWords.getLanguages())
			possibilities.add(language + "-> other languages");
		possibilities.add("Random language");

		final JComboBox list = new JComboBox(possibilities);
		list.setSelectedIndex(list.getItemCount() - 1);
		list.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setQuestiontLanguage(list.getSelectedIndex());
				// if random item selected => set random
				if (list.getSelectedIndex() == list.getItemCount() - 1)
					setQuestiontLanguage(LANGUAGE_RANDOM);
			}
		});

		/* create the validate button */
		validate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				forbidAllLessons();
				for (int i = 0; i < boxes.size(); i++) {
					int lesson_nb = lessons_associated_with_boxes.get(i);
					if (boxes.get(i).isSelected())
						setLesson(lesson_nb, true);
				}
				// System.out.println(jp.getQueue().wp);
				jp.getQueue().clearQueueAndRepopulate();
				jp.makeButtons();
			}
		});

		/* add everything to a list */
		LinkedList<JComponent> components = new LinkedList<JComponent>();
		components.add(validate);
		components.add(list);
		components.add(uncheck_all);
		components.addAll(boxes);

		/* add all the elements of the list to the panel */
		JPanel jp_with_scroll = new JPanel();
		JScrollPane scrollPane = new JScrollPane(jp_with_scroll);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);

		jp_with_scroll.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;
		c.gridy = 0;
		c.gridx = 0;

		// create button
		for (int i = 0; i < components.size(); i++) {
			int i_lessons = i - 2; // 1 for the first lesson
			jp_with_scroll.add(components.get(i), c);
			if (i_lessons % ITEMS_PER_LINE == 0) {
				c.gridy++;
				c.gridx = 0;
			} else
				c.gridx++;
		} // end for button

		jp.removeAll();
		jp.setLayout(new GridBagLayout());
		jp.add(scrollPane, c);
		refreshButton(validate, boxes, lessons_associated_with_boxes);
		jp.validate();
	}

	/**
	 * tests
	 */
	public static void main(String[] args) {
		WordPicker wp = WordPicker.defaultWordPicker();
		wp.forbidAllLessons();
		wp.setLesson(0, true);
		System.out.println(wp);
	}
}
