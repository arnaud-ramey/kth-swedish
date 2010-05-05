package Kvtml.Editing.ImageGetter;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Asker.VisualAsker;
import Kvtml.IO.Image_IO;
import Kvtml.VocParser.ListOfWords;
import Kvtml.VocParser.Word;

public class ImageGetterGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	private static String PIC_PREFIX = "*PIC* ";

	ImageGetter ig = new ImageGetter();

	ListOfWords words;

	int current_word_index = 0;

	/* swing variables */

	JButton //
	next_word_button = new JButton("Next word"),
			save_and_next_word_button = new JButton("Save + next word"),
			prev_word_button = new JButton("Prev word"),
			next_image_button = new JButton("Next pic"),
			remove_image_button = new JButton("Remove pic"),
			prev_image_button = new JButton("Prev pic"),
			image_preview_button = new JButton();

	JTextField word_languages_field;

	JButton // 
			word_image_filename_field = new JButton(""),
			word_image_picture_field = new JButton("");

	JTextField perso_image_field;

	JButton perso_image_upload_button = new JButton("Upload !");

	// selector
	JComboBox word_selector_list = new JComboBox();

	JTextField word_selector_pattern = new JTextField();

	/**
	 * constructor
	 */
	public ImageGetterGUI() {
		makeButtons();
		setDefaultListOfWords();
	}

	public ListOfWords getWords() {
		return words;
	}

	/**
	 * change the current list of words
	 * 
	 * @param words
	 */
	public void setWords(ListOfWords words) {
		this.words = words;

		/* update the different things */
		refreshComboBox();

		// go to the end of the list
		changeWord(words.nbWords());
	}

	public void setDefaultListOfWords() {
		ListOfWords w = new ListOfWords();
		w.readFile("/voc.kvtml");
		setWords(w);
	}

	/**
	 * change the current {@link Word}
	 * 
	 * @param index
	 *            the index
	 */
	public void changeWord(int index) {
		if (index == current_word_index)
			return;
		current_word_index = ImageGetter.minMax(index, 0, words.getWords()
				.size() - 1);
		word_languages_field.setText(currentWord().toString_onlyWords());
		ig.setSearch(currentWord().get0());
		refreshGoogleImage();
		refreshWordImage();
	}

	public Word currentWord() {
		return words.getWord(current_word_index);
	}

	/**
	 * @return the name of the output file for the {@link ListOfWords}
	 */
	private String outputName() {
		String rep = words.getFilename();
		rep = rep.replace(".kvtml", "");
		rep = rep + "_out.kvtml";
		if (rep.startsWith("/"))
			rep = rep.substring(1);
		return rep;
	}

	private void refreshGoogleImage() {
		image_preview_button.setIcon(new ImageIcon(ig.getImage()));
	}

	/**
	 * print the filename of the word picture in the zone
	 * 
	 */
	private void refreshWordImage() {
		word_image_picture_field.setIcon(null);
		if (currentWord().containsPicture()) {
			// compute the dimensions
			String path = currentWord().getPictureFilename();
			int w = word_image_picture_field.getWidth() * 9 / 10;
			int h = word_image_picture_field.getHeight() * 9 / 10;

			// try to load the image
			BufferedImage i = new BufferedImage(1, 1, 1);
			try {
				i = Image_IO.getImageFromURL(path, w, h, true);
			} catch (ImagingOpException e) {
			}

			// refresh the fields
			word_image_picture_field.setIcon(new ImageIcon(i));
			word_image_filename_field.setText("Current picture : '"
					+ currentWord().getPictureFilename() + "'");
		} else {
			word_image_filename_field.setText("No current picture.");
		}
	}

	private ActionListener comboBoxActionListener = null;

	/**
	 * update the content of the {@link JComboBox}
	 * 
	 */
	private void refreshComboBox() {
		word_selector_list.removeAll();
		word_selector_list.removeAllItems();

		/* create the list of items */
		for (int i = 0; i < words.nbWords(); ++i) {
			Word w = words.getWord(i);
			String s = w.toString_onlyWords();
			if (!s.contains(word_selector_pattern.getText()))
				continue;
			if (w.containsPicture())
				s = PIC_PREFIX + s;
			// cut the string
			if (s.length() > 50)
				s = s.substring(0, 50) + "...";
			word_selector_list.addItem(s);
		}
		word_selector_list.setBounds(0, 0, 0, 0);

		/* add the action listener to move to the selected word */
		final ListOfWords words_ptr = words;
		comboBoxActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String chosen_entry = (String) word_selector_list
						.getSelectedItem();
				if (chosen_entry == null)
					return;
				chosen_entry = chosen_entry.replace(PIC_PREFIX, "");
				// System.out.println(chosen_entry);

				for (int i = 0; i < words_ptr.nbWords(); i++) {
					// System.out.println(words_ptr.getWord(i).toString());
					Word w = words_ptr.getWord(i);
					if (w.toString_onlyWords().contains(chosen_entry)) {
						changeWord(i);
						return;
					}
				} // end for
			}
		};

		// add it
		word_selector_list.removeActionListener(comboBoxActionListener);
		word_selector_list.addActionListener(comboBoxActionListener);
	}

	private void nextWord() {
		changeWord(current_word_index + 1);
	}

	private void previousWord() {
		changeWord(current_word_index - 1);
	}

	/**
	 * associate the new image to the current {@link Word} and save the
	 * {@link ListOfWords}
	 */
	private void saveImageInCurrentWord() {
		String filename = ig.clean_filename();
		ig.saveImage();
		currentWord().setPicture(filename);
		words.writeFile(outputName());
	}

	/**
	 * remove the image in the current {@link Word}
	 */
	private void removeImageInCurrentWord() {
		currentWord().removePicture();
		words.writeFile(outputName());
		refreshWordImage();
	}

	/**
	 * draw the buttons in the {@link JPanel}
	 * 
	 */
	private void makeButtons() {
		image_preview_button.setBackground(VisualAsker.BLUE);

		/* text fields */
		word_languages_field = new JTextField("");
		word_languages_field.setEditable(false);
		word_languages_field.setBackground(VisualAsker.YELLOW);
		word_languages_field.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		word_languages_field.setHorizontalAlignment(JTextField.CENTER);

		word_image_filename_field.setBackground(VisualAsker.YELLOW);
		word_image_filename_field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN,
				14));
		word_image_picture_field.setBackground(VisualAsker.YELLOW);

		/* previous and next buttons */
		prev_word_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				previousWord();
			}
		});
		next_word_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextWord();
			}
		});
		save_and_next_word_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveImageInCurrentWord();
				nextWord();
			}
		});

		/* previous and next image */
		prev_image_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ig.goToPreviousImage();
				refreshGoogleImage();
			}
		});
		next_image_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ig.goToNextImage();
				refreshGoogleImage();
			}
		});
		remove_image_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeImageInCurrentWord();
			}
		});

		/* list pattern */
		// word_selector_pattern.addCaretListener(new CaretListener() {
		// public void caretUpdate(CaretEvent arg0) {
		// // System.out.println(word_selector_pattern.getText());
		// refreshComboBox();
		// }
		// });
		word_selector_pattern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refreshComboBox();
			}
		});

		/* upload */
		perso_image_field = new JTextField("Link to the image");
		perso_image_upload_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String url = perso_image_field.getText();
				BufferedImage perso_image = Image_IO.getImageFromURL(url,
						ImageGetter.MAX_WIDTH, ImageGetter.MAX_HEIGHT, false);
				ig.setImage(perso_image);
				refreshGoogleImage();
			}
		});

		/* layout */
		removeAll();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;

		c.gridy = 0;
		c.gridx = 0;
		c.gridwidth = 1;
		add(word_selector_pattern, c);
		c.gridx = 1;
		c.gridwidth = 2;
		add(word_selector_list, c);
		c.gridwidth = 1;

		c.gridy = 1;
		c.gridx = 2;
		c.gridheight = 3;
		add(word_image_picture_field, c);
		c.gridheight = 1;

		c.gridy = 1;
		c.gridx = 0;
		add(prev_word_button, c);
		c.gridx = 1;
		add(next_word_button, c);

		c.gridy++;
		c.gridx = 0;
		add(remove_image_button, c);
		c.gridx = 1;
		add(save_and_next_word_button, c);

		c.gridy++;
		c.gridx = 0;
		add(prev_image_button, c);
		c.gridx = 1;
		add(next_image_button, c);

		c.gridwidth = 3;
		c.gridx = 0;

		c.gridy++;
		add(word_image_filename_field, c);

		c.gridy++;
		add(word_languages_field, c);

		c.gridy++;
		c.weighty = 10;
		add(image_preview_button, c);
		c.weighty = 1;

		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 1;
		add(perso_image_upload_button, c);
		c.gridx = 1;
		c.gridwidth = 2;
		add(perso_image_field, c);

		validate();
	}

	/**
	 * create window
	 */
	public static void window(String file) {
		ListOfWords w = new ListOfWords();
		w.readFile(file);

		ImageGetterGUI gui = new ImageGetterGUI();
		gui.setWords(w);

		JFrame jf = new JFrame();
		jf.add(gui);
		jf.setTitle("ImageGetterGUI");
		jf.setSize(600, 600);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}

	/**
	 * tests
	 */
	public static void main(String[] args) {
		ImageGetterGUI.window("/voc.kvtml");
		// ImageGetterGUI.window("/test.kvtml");
	}
}
