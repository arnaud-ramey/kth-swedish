package Asker;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import IO.LibUtils;
import Lessons.VocParser.WordPicker;

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
	private JTextField lessonTitle_field;

	private JButton unknownWord_field = new JButton("");

	private JButton answer_field = new JButton("");

	private JButton knowButton;

	private JButton doNotKnowButton;

	/**
	 * get and display the content of the question
	 */
	private void displayQuestion() {
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
	}

	/**
	 * display the content of the answer
	 */
	private void displayAnswer() {
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
	private void chooseLesson_makeButtons(String[] lessons) {
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
		for (int i = 0; i < lessons.length; i++) {
			final int i_bis = i;
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
					queue.setLesson(i_bis);
					if (i_bis == QuestionQueue.VOCABULARY)
						queue.wp.forbidAllLessons();
					makeButtons();
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
	 * constructor
	 */
	public VisualAsker() {
		makeButtons();
	}

	/**
	 * construct the buttons
	 */
	public void makeButtons() {
		setBackground(BLUE);
		repaint();

		if (queue.getTypeOfQuestion() == -1) {
			chooseLesson_makeButtons(QuestionQueue.possible_lessons);
			return;
		}

		if (queue.getTypeOfQuestion() == QuestionQueue.VOCABULARY
				&& queue.wp.areAllLessonsForbidden()) {
			queue.wp.vocLesson_makeButtons(this);
			return;
		}

		answer_field.setBackground(BLUE);
		answer_field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
		answer_field.setHorizontalAlignment(JTextField.CENTER);

		lessonTitle_field = new JTextField("");
		lessonTitle_field.setEditable(false);
		lessonTitle_field.setBackground(BLUE);
		lessonTitle_field.setFont(new Font(Font.DIALOG, Font.ITALIC, 14));
		lessonTitle_field.setHorizontalAlignment(JTextField.RIGHT);

		/* create actions */
		Action knowAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent arg0) {
				displayAnswer();
				queue.declareQuestion_known();
				displayQuestion();
			}
		};
		Action unknowAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent arg0) {
				displayAnswer();
				queue.declareQuestion_unknown();
				displayQuestion();
			}
		};
		
		/* set actions to buttons */
		knowButton = new JButton(knowAction);
		knowButton.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("F1"), "know");
		knowButton.getActionMap().put("know", knowAction);
		knowButton.setText("I know.");
		
		doNotKnowButton = new JButton(unknowAction);
		doNotKnowButton.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("F2"), "unknow");
		doNotKnowButton.getActionMap().put("unknow", unknowAction);
		doNotKnowButton.setText("???");

		/* layout */
		removeAll();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;

		c.gridheight = 3;
		c.weightx = 6;
		c.gridy = 0;
		c.gridx = 0;
		add(unknownWord_field, c);

		c.gridheight = 1;
		c.weightx = 1;
		c.gridx = 1;
		c.gridy = 0;
		add(lessonTitle_field, c);
		c.gridy++;
		add(knowButton, c);
		c.gridy++;
		add(doNotKnowButton, c);

		c.gridy++;
		c.gridwidth = 3;
		c.gridx = 0;
		add(answer_field, c);

		displayQuestion();
		validate();
	}

	/**
	 * @return the queue
	 */
	public QuestionQueue getQueue() {
		return queue;
	}

	public static void window() {
		// WordPicker wp = new WordPicker();
		// wp.setListofWords("/test.kvtml");
		
		WordPicker wp = WordPicker.defaultWordPicker();
		// System.out.println(wp.getRandomQuestion());

		VisualAsker jp = new VisualAsker();
		jp.queue.setWordPicker(wp);
		// jp.queue.wp.listOfWords.displayAllWords();

		// create window
		System.out.println("Creating window...");
		JFrame jf = new JFrame();
		jf.add(jp);
		jf.setTitle("Asker");
		jf.setSize(600, 200);
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
