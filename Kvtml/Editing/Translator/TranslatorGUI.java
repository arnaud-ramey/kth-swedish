package Kvtml.Editing.Translator;

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
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import Kvtml.VocParser.ListOfWords;
import Kvtml.VocParser.Word;

public class TranslatorGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	ListOfWords words;

	int originLanguage;
	int targetLanguage;

	boolean use_lesson_filter = false;
	String lesson_filter;

	JCheckBox use_lesson_filter_switch = new JCheckBox("use_lesson_filter?");
	JTextArea lesson_filter_zone = new JTextArea("");
	JButton lesson_filter_refresh_button = new JButton("Refresh !");
	JButton save_JButton = new JButton("Save");
	JPanel list = new JPanel();
	JScrollPane scrollPane = new JScrollPane(list);

	/**
	 * @param t
	 */
	public TranslatorGUI(ListOfWords words) {
		this.words = words;

		// makeButtons();
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
		// determine if we need to translate
		boolean isNotEmpty = w.containsLanguage(originLanguage)
				&& w.getForeignWord(originLanguage).length() > 0;
		if (isNotEmpty == false)
			return;
		boolean hasAlreadyTraduction = w.containsLanguage(targetLanguage)
				&& w.getForeignWord(targetLanguage).length() > 0;
		final String translation_String;

		// translate if needed
		if (hasAlreadyTraduction) {
			translation_String = w.getForeignWord(targetLanguage);
		} else {
			translation_String = Translator.translate(w, originLanguage,
					targetLanguage);
		}

		final Border normalBorder = BorderFactory.createEmptyBorder();
		final Border underlinedBorder = BorderFactory.createLineBorder(
				Color.red, 1);

		final LinkedList<JComponent> j_components = new LinkedList<JComponent>();

		// the original
		final JTextField words_textField = new JTextField(w
				.getForeignWord(originLanguage));
		words_textField.setMinimumSize(new Dimension(10, 10));
		words_textField.setEditable(false);
		words_textField.setToolTipText(w.toString_onlyWords());
		words_textField.setBorder(normalBorder);
		words_textField.setHorizontalAlignment(JTextField.RIGHT);
		j_components.add(words_textField);

		// the translation
		final Color translation_color;
		final String tooltip_String;
		if (hasAlreadyTraduction) {
			translation_color = new Color(255, 200, 200);
			tooltip_String = "This is the translation saved in the file";
		} else {
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

	public void refreshList() {
		System.out.println("refreshList()");

		/* purge the list */
		list.removeAll();
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		list.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;

		/* loop through the words */
		System.out.println("*** Starting the translations...");
		for (int i = 0; i < words.nbWords(); i++) {
			final Word w = words.getWord(i);

			if (use_lesson_filter && !w.getLessonName().contains(lesson_filter)) {
				System.out.println("Current word:" + w.toString_onlyWords()
						+ "does not pass filter");
				continue;
			}
			System.out.println("-> Current word:" + w.toString_onlyWords());
			makeButtons(w, i, list, c);
		}
		System.out.println("*** Translations ended.");

		this.validate();
	}

	public void makeButtons() {
		/* button for the lesson filter */
		lesson_filter_zone.setText(lesson_filter);
		lesson_filter_refresh_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refreshList();
			}
		});
		use_lesson_filter_switch.setSelected(use_lesson_filter);
		use_lesson_filter_switch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Switch");
				use_lesson_filter = use_lesson_filter_switch.isSelected();
				refreshList();
			}
		});

		/* button for saving */
		save_JButton.setBackground(Color.orange);
		save_JButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("saving !");
				words.writeFile("out.kvtml");
			}
		});

		/* list */
		refreshList();

		/* layout */
		this.removeAll();
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;
		c.gridy = 0;
		c.weighty = 1;

		c.gridy++;
		add(lesson_filter_zone, c);

		c.gridy++;
		add(use_lesson_filter_switch, c);

		c.gridy++;
		add(lesson_filter_refresh_button, c);

		c.gridy++;
		add(save_JButton, c);

		c.gridy++;
		c.weighty = 100;
		add(scrollPane, c);

		this.validate();
	}

	/**
	 * create a nice window
	 */
	public static void window(ListOfWords words, int originLanguage,
			int targetLanguage, String lessonFilter) {
		TranslatorGUI trans = new TranslatorGUI(words);
		trans.originLanguage = originLanguage;
		trans.targetLanguage = targetLanguage;
		if (!lessonFilter.equals("")) {
			trans.use_lesson_filter = true;
			trans.lesson_filter = lessonFilter;
		}
		trans.makeButtons();

		JFrame jf = new JFrame("TranslatorGUI");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.add(trans);
		jf.setSize(900, 600);
		jf.setVisible(true);
	}

	public static void main(String[] args) {
		ListOfWords w = ListOfWords.defaultListOfWords();
		// ListOfWords w = new ListOfWords();
		// w.readFile("/test.kvtml");

		window(w, Word.SPANISH, Word.ENGLISH, "");
	}
}
