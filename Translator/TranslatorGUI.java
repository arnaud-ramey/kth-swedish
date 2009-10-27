package Translator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import Lessons.VocParser.ListOfWords;
import Lessons.VocParser.Word;

public class TranslatorGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	Translator t = new Translator();
	ListOfWords words;

	int targetLanguage = Word.SPANISH;
	
	boolean use_lesson_filter = true;
	String lesson_filter = "Win.";

	/**
	 * @param t
	 */
	public TranslatorGUI(ListOfWords words) {
		this.words = words;

		makeButtons();
	}

	/**
	 * draw the buttons for a special {@link Word}
	 * 
	 * @param w
	 *            the {@link Word}
	 * @param line
	 *            the index of the line where to add in the {@link JPanel}
	 * @param panel_whereToAdd
	 *            the {@link JPanel} where to add
	 * @param c
	 *            the {@link GridBagConstraints} for adding
	 */
	public void makeButtons(final Word w, final int line,
			final JPanel panel_whereToAdd, final GridBagConstraints c) {
		boolean hasAlreadyTraduction = w.containsLanguage(targetLanguage);

		final Border normalBorder = BorderFactory.createEmptyBorder();
		final Border underlinedBorder = BorderFactory.createLineBorder(
				Color.red, 1);

		final LinkedList<JComponent> j_components = new LinkedList<JComponent>();

		// the original
		final JTextField words_textField = new JTextField(w.get0());
		words_textField.setMinimumSize(new Dimension(10, 10));
		words_textField.setEditable(false);
		words_textField.setToolTipText(w.toString_onlyWords());
		words_textField.setBorder(normalBorder);
		words_textField.setHorizontalAlignment(JTextField.RIGHT);
		j_components.add(words_textField);

		// the translation
		final String translation_String;
		final Color translation_color;
		final String tooltip_String;
		if (hasAlreadyTraduction) {
			translation_String = w.getForeignWord(targetLanguage);
			translation_color = new Color(255, 200, 200);
			tooltip_String = "This is the translation saved in the file";
		}

		else {
			translation_String = t.translate(w, 0, targetLanguage);
			translation_color = new Color(200, 255, 200);
			tooltip_String = "This is the translation coming from Google Translator";
		}

		final JTextField translation_textField = new JTextField();
		translation_textField.setEditable(true);
		translation_textField.setMinimumSize(new Dimension(10, 10));
		translation_textField.setText(translation_String);
		translation_textField.setBackground(translation_color);
		translation_textField.setToolTipText(tooltip_String);
		translation_textField.setBorder(normalBorder);
		j_components.add(translation_textField);

		// button to add
		final JButton add_JButton = new JButton();
		add_JButton.setText((hasAlreadyTraduction ? "Change" : "Add"));
		add_JButton.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseClicked(MouseEvent arg0) {
				w.setForeignWord(targetLanguage, translation_textField
						.getText());
				// remove the buttons
				for (JComponent t : j_components)
					panel_whereToAdd.remove(t);
				panel_whereToAdd.remove(add_JButton);
				// make some new
				makeButtons(w, line, panel_whereToAdd, c);
				panel_whereToAdd.validate();
			}

			public void mouseExited(MouseEvent arg0) {
				words_textField.setBorder(normalBorder);
				translation_textField.setBorder(normalBorder);
			}

			public void mouseEntered(MouseEvent arg0) {
				words_textField.setBorder(underlinedBorder);
				translation_textField.setBorder(underlinedBorder);
			}
		});
		j_components.add(add_JButton);

		// add the components
		c.gridy = line;
		for (int i = 0; i < j_components.size(); i++) {
			c.gridx = i;
			panel_whereToAdd.add(j_components.get(i), c);
		}
	}

	public void makeButtons() {
		/* button for saving */
		JButton save_JButton = new JButton("Save");
		save_JButton.setBackground(Color.orange);
		save_JButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				words.writeFile("out.kvtml");
			}
		});

		/* make the list */
		JPanel list = new JPanel();
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);

		list.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;

		System.out.println("*** Starting the translations...");
		for (int i = 0; i < words.nbWords(); i++) {
			final Word w = words.getWord(i);
			if (use_lesson_filter && !w.getLessonName().contains(lesson_filter))
				continue;
			makeButtons(w, i, list, c);
		}
		System.out.println("*** Translations ended.");

		/* layout */
		// add(list, c);
		removeAll();
		setLayout(new GridBagLayout());
		c.gridy = 0;
		c.weighty = .01;
		add(save_JButton, c);

		c.gridy = 1;
		c.weighty = 1;
		add(scrollPane, c);
	}

	/**
	 * create a nice window
	 */
	public static void window(ListOfWords words) {
		JFrame jf = new JFrame("Tran");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.add(new TranslatorGUI(words));
		jf.setSize(900, 600);
		jf.setVisible(true);
	}

	public static void main(String[] args) {
		ListOfWords w = ListOfWords.defaultListOfWords();
		// ListOfWords w = new ListOfWords();
		// w.readFile("/test.kvtml");

		window(w);
	}
}
