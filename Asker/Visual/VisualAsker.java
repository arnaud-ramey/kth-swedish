package Asker.Visual;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import Asker.Question;
import Asker.QuestionQueue;
import Kvtml.IO.IO;
import Kvtml.IO.LibUtils;
import Kvtml.VocParser.ListOfWords;
import Kvtml.VocParser.WordPicker;
import Kvtml.VocParser.Lessons.LessonSelection;
import Kvtml.VocParser.Lessons.LessonTree;

public class VisualAsker extends JPanel {
	private static final long serialVersionUID = 1L;

	public static Color YELLOW = new Color(255, 255, 178);

	public static Color BLUE = new Color(210, 255, 255);

	/*
	 * Questions, etc
	 */
	private QuestionQueue queue = new QuestionQueue();

	private Question currentQuestion;

	/*
	 * graphical Swing components
	 */
	private JTextField lessonTitle_field = new JTextField("");
	private JButton unknownWord_field = new JButton("");
	private JButton answer_field = new JButton("");
	private JTextField info_field = new JTextField("");

	private JButton goToMainTitleButton = new JButton();
	private JButton knowButton = new JButton();
	private JButton doNotKnowButton = new JButton();

	/**
	 * constructor
	 */
	public VisualAsker() {
		goToMainPage(false);
	}

	/**
	 * return to main page
	 */
	public void goToMainPage(boolean resetSelection) {
		getQueue().setTypeOfQuestion(QuestionQueue.TYPE_OF_QUESTION_UNDEFINED);
		// reset selection if there is one
		if (getQueue().wp != null && resetSelection)
			getQueue().wp.getSelection().allowAllLessons();
		makeButtons();
	}

	/**
	 * get and display the content of the question
	 */
	private void displayQuestion() {
		debug("displayQuestion()");
		// System.out.println(queue);

		// get the question
		currentQuestion = queue.getQuestion();

		// unknown word
		unknownWord_field.removeAll();
		// case if it is a text -> display the text
		if (currentQuestion.is_image == false) {
			String text = currentQuestion.question;
			int size = 8;
			if (text.length() > 30)
				size = 6;
			text = "<font face=Arial size=" + size + ">" + text + "</font>";
			text = "<center>" + text + "</center>";
			text = "<html>" + text + "</html>";
			unknownWord_field.setBackground(YELLOW);
			unknownWord_field.setText(text);
			unknownWord_field.setIcon(null);
		}
		// case if it is an image -> load the image;
		else {
			unknownWord_field.setText("");
			unknownWord_field.setBackground(Color.white);
			int w = Math.max(100, (int) (0.9f * unknownWord_field.getWidth()));
			int h = Math.max(100, (int) (0.9f * unknownWord_field.getHeight()));
			unknownWord_field.setIcon(new ImageIcon(currentQuestion.getImage(w,
					h)));
		}

		// display the lesson
		lessonTitle_field.setText(currentQuestion.lesson.toUpperCase());

		// display the stats
		String infos = queue.info();
		info_field.setText(infos);
	}

	/**
	 * display the content of the answer
	 */
	private void displayAnswer() {
		debug("displayAnswer()");

		String text = currentQuestion.answer;
		text = "<center>" + text + "</center>";
		text = "<html>" + text + "</html>";
		answer_field.setText(text);
	}

	/**
	 * build the buttons to choose the type of exercice
	 * 
	 * @param lessons
	 *            the name of the lessons
	 */
	private void chooseQuestionType_makeButtons() {
		debug("chooseQuestionType_makeButtons()");

		int ITEMS_PER_LINE = 3;

		/* create panel */
		removeAll();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;
		c.gridy = 0;
		c.gridx = 0;

		/* create button */
		String[] lessons = QuestionQueue.possible_type_of_questions;
		for (int i = 0; i < lessons.length; i++) {
			final int i_ptr = i;
			// delete the number at the beginnings
			String text = lessons[i];
			text = text.substring(text.indexOf(" ") + 1);
			text = LibUtils.firstLetterUpperCase(text);
			// create the button
			JButton butt = new JButton(text);
			butt.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
			// add the action listener
			butt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getQueue().setTypeOfQuestion(i_ptr);

					if (i_ptr == QuestionQueue.TYPE_OF_QUESTION_VOCABULARY) {
						chooseLesson_makeButtons();
					} else {
						getQueue().clearQueueAndRepopulate();
						makeButtons();
					}
				}
			});
			// add it to the panel
			add(butt, c);
			if ((i + 1) % ITEMS_PER_LINE == 0) {
				c.gridx = 0;
				c.gridy++;
			} else {
				c.gridx++;
			}
		} // end for button
		validate();
	}

	/**
	 * make the buttons to show a question
	 */
	private void showQuestion_makeButtons() {
		debug("showQuestion_makeButtons()");

		answer_field.setBackground(BLUE);
		answer_field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
		answer_field.setHorizontalAlignment(JTextField.CENTER);

		lessonTitle_field.setEditable(false);
		lessonTitle_field.setBackground(BLUE);
		lessonTitle_field.setFont(new Font(Font.DIALOG, Font.ITALIC, 14));
		lessonTitle_field.setHorizontalAlignment(JTextField.RIGHT);

		info_field.setEditable(false);
		info_field.setBackground(Color.LIGHT_GRAY);
		info_field.setFont(new Font(Font.DIALOG, Font.ITALIC, 12));
		info_field.setHorizontalAlignment(JTextField.CENTER);

		/* create actions */
		Action knowAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent arg0) {
				debug("knowAction.actionPerformed()");
				displayAnswer();
				queue.declareQuestion_known();
				displayQuestion();
			}
		};
		Action unknowAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent arg0) {
				debug("unknowAction.actionPerformed()");
				displayAnswer();
				queue.declareQuestion_unknown();
				displayQuestion();
			}
		};

		/* set actions to buttons */
		knowButton.setAction(knowAction);
		knowButton.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("F1"), "know");
		knowButton.getActionMap().put("know", knowAction);
		knowButton.setText("I know. (F1)");

		doNotKnowButton.setAction(unknowAction);
		doNotKnowButton.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("F2"), "unknow");
		doNotKnowButton.getActionMap().put("unknow", unknowAction);
		doNotKnowButton.setText("I dunno. (F2)");

		goToMainTitleButton.setAction(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				goToMainPage(true);
			}
		});
		goToMainTitleButton.setText("-> Main title.");

		/* layout */
		removeAll();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 100;

		c.gridheight = 4;
		c.weightx = 600;
		c.gridy = 0;
		c.gridx = 0;
		add(unknownWord_field, c);

		c.gridheight = 1;
		c.weightx = 100;
		c.gridx = 1;

		c.gridy = 0;
		c.weighty = 10;
		add(goToMainTitleButton, c);
		c.gridy++;
		c.weighty = 100;
		add(lessonTitle_field, c);
		c.gridy++;
		add(knowButton, c);
		c.gridy++;
		add(doNotKnowButton, c);

		c.gridy++;
		c.weighty = 100;
		c.gridwidth = 3;
		c.gridx = 0;
		add(answer_field, c);

		c.gridy++;
		c.weighty = 10;
		add(info_field, c);

		displayQuestion();
	}

	/**
	 * display the buttons to choose the lesson
	 */
	private void chooseLesson_makeButtons() {
		debug("chooseLesson_makeButtons()");
		removeAll();
		getQueue().checkWordPickerStarted();
		final WordPicker wp = getQueue().wp;
		final LessonTree tree = wp.getSelection().getLessonTree();

		/* create the lesson selector */
		final LessonSelector selector = new LessonSelector(tree, wp
				.getSelection());

		/* create the question language list */
		ListOfWords words = tree.getWords();
		Vector<String> possibilities = new Vector<String>();
		for (String language : words.getLanguages())
			possibilities.add(language + "-> other languages");
		possibilities.add("Random language");

		final JComboBox languageList = new JComboBox(possibilities);
		languageList.setSelectedIndex(languageList.getItemCount() - 1);

		/* create the ok button */
		JButton okButton = new JButton();
		okButton.setAction(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent arg0) {
				// set the new selection
				debug("Defining new selection...");
				LessonSelection selection = selector.getLessonSelection();
				getQueue().wp.setSelection(selection);

				// set the new language
				wp.setQuestiontLanguage(languageList.getSelectedIndex());
				// if random item selected => set random
				if (languageList.getSelectedIndex() == languageList
						.getItemCount() - 1)
					wp.setQuestiontLanguage(WordPicker.LANGUAGE_RANDOM);

				// repopulate
				getQueue().clearQueueAndRepopulate();
				makeButtons();
			}
		});
		okButton.setText("Start !");

		/* put everything */
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;

		c.gridy = 0;
		c.weighty = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		add(languageList, c);
		c.gridx = 1;
		add(okButton, c);

		c.gridy++;
		c.weighty = 10;
		c.gridwidth = 2;
		c.gridx = 0;
		add(selector, c);
		validate();
		repaint();
	}

	/**
	 * construct the buttons
	 */
	public void makeButtons() {
		debug("makeButtons()");
		setBackground(BLUE);

		if (queue.getTypeOfQuestion() == QuestionQueue.TYPE_OF_QUESTION_UNDEFINED)
			chooseQuestionType_makeButtons();
		else
			showQuestion_makeButtons();
		validate();
		repaint();
	}

	/**
	 * @return the queue
	 */
	public QuestionQueue getQueue() {
		return queue;
	}

	public static void debug(String s) {
		IO.debug("VisualAsker::" + s);
	}

	public static void window() {
		VisualAsker jp = new VisualAsker();

		// WordPicker wp = WordPicker.defaultWordPicker(true);
		// // System.out.println(wp.getRandomQuestion());
		// jp.getQueue().setWordPicker(wp);
		// System.out.println(jp.queue.wp.toString());

		// create window
		JFrame jf = new JFrame();
		jf.add(jp);
		jf.setTitle("Asker");
		jf.setSize(800, 400);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}

	/**
	 * tests
	 */
	public static void main(String[] args) {
		window();
	}
}
