package Kvtml.VocParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;

import Kvtml.IO.IO;
import Kvtml.VocParser.Lessons.LessonTree;

public class ListOfWords {
	private String filename = "";

	private Vector<String> languages = new Vector<String>();

	private Vector<String> lines = new Vector<String>();

	private Vector<Word> words = new Vector<Word>();

	private LessonTree lessonTree = null;

	/**
	 * constructor
	 * 
	 * @param filename
	 *            the kvtml file
	 */
	public ListOfWords(String filename) {
		readFile(filename);
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	public Vector<String> getLanguages() {
		return languages;
	}

	/**
	 * read data from a file
	 * 
	 * @param f
	 *            the name of the file
	 */
	public void readFile(String f) {
		languages.clear();
		lines.clear();
		words.clear();

		/* reading file and adding all lines */
		filename = f;
		String[] lines_arr = IO.readFile(filename);
		for (String s : lines_arr)
			lines.add(s);

		/* finding languages */
		for (String line : lines) {
			if (line.contains("<name>")) {
				String language = Word.extractFieldFromAnchors(line, "name");
				// System.out.println("language:" + language);
				languages.add(language);
			}
			if (line.contains("</identifiers>"))
				break;
		}

		/* finding words */
		int curr_line_index;
		int nb_word = 0;
		for (curr_line_index = 0; curr_line_index < lines.size(); curr_line_index++) {
			String l = lines.get(curr_line_index);
			if (l.contains("<entry")) {
				words.add(new Word(this, curr_line_index, nb_word));
				nb_word++;
			}
			if (l.contains("</entries>"))
				break;
		}

		/* find lessons and associating with them */
		lessonTree = new LessonTree(this, "root");

		/* compute data for each word */
		compute_number_of_languages_for_each_word();
	}

	/**
	 * write the list in a file
	 * 
	 * @param filename
	 */
	public void writeFile(String filename) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			for (String s : lines) {
				out.write(s + "\n");
			}
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * a getter of the words
	 * 
	 * @return
	 */
	public Vector<Word> getWords() {
		return words;
	}

	/**
	 * get a line of the text
	 * 
	 * @param lineNb
	 * @return the chosen line
	 */
	public String getLine(int lineNb) {
		return lines.get(lineNb);
	}

	/**
	 * set a line of the text
	 * 
	 * @param lineNb
	 *            the line number
	 * @param newline
	 *            the new line
	 */
	public void setLine(int lineNb, String newline) {
		lines.set(lineNb, newline);
	}

	/**
	 * add a line of the text
	 * 
	 * @param lineNb
	 *            the line number
	 * @param newline
	 *            the new line
	 */
	public void addLine(int lineNb, String newline) {
		// shift the lines after
		for (Word w : words) {
			if (w.beginningLine >= lineNb)
				w.beginningLine++;
		}
		// add the line
		lines.add(lineNb, newline);
	}

	public void addLines(int lineNb, Vector<String> newLines) {
		// add the new lines backward
		for (int i = newLines.size() - 1; i >= 0; i--) {
			this.lines.add(lineNb, newLines.elementAt(i));
		}
		// shift the lines after
		for (Word w : words) {
			if (w.beginningLine >= lineNb)
				w.beginningLine += newLines.size();
		}
	}

	public void removeLine(int lineNb) {
		// shift the lines after
		for (Word w : words) {
			if (w.beginningLine >= lineNb)
				w.beginningLine--;
		}
		// remove the line
		lines.remove(lineNb);
	}

	/**
	 * @return the number of lines
	 */
	public int nbLines() {
		return lines.size();
	}

	/**
	 * @return the number of words
	 */
	public int nbWords() {
		return words.size();
	}

	// /**
	// * @return the number of lessons
	// */
	// public int nbLessons() {
	// return lessons.size();
	// }

	/**
	 * get the {@link Word} at a certain index in the list
	 * 
	 * @param i
	 *            the index
	 * @return the {@link Word}
	 */
	public Word getWord(int i) {
		return words.elementAt(i);
	}

	/**
	 * @return a totally random word
	 */
	public Word getRandomWord() {
		return getWord((int) (Math.random() * nbWords()));
	}

	/**
	 * @return the lessonTree
	 */
	public LessonTree getLessonTree() {
		return lessonTree;
	}

	/**
	 * find the number of languages for each {@link Word}
	 */
	private void compute_number_of_languages_for_each_word() {
		for (Word w : words)
			w.detectNumberOfLanguages();
	}

	/**
	 * @return an info {@link String}
	 */
	public String toString() {
		return toString(true);
	}

	/**
	 * @return an info {@link String}
	 */
	public String toString(boolean breakLines) {
		String rep = "";
		String endLine = (breakLines ? "\n" : "");
		rep += "File:'" + filename + "'" + endLine;
		rep += " - Languages : " + languages.toString() + endLine;
		rep += " - Nb lines : " + lines.size() + endLine;
		rep += " - Nb words : " + nbWords() + endLine;
		return rep;
	}

	/**
	 * display all the {@link Word} of words on {@link System}.out
	 */
	public void displayAllWords() {
		for (Word w : words)
			System.out.println(w);
	}

	/**
	 * @return the default {@link ListOfWords}
	 */
	public static ListOfWords defaultListOfWords() {
		ListOfWords l = new ListOfWords("/voc.kvtml");
		return l;
	}

	public static void test() {
		ListOfWords l = ListOfWords.defaultListOfWords();
		System.out.println(l.toString(true));
		// l.displayAllWords();
		System.out.println(l.getRandomWord());
	}

	public static void test2() {
		ListOfWords l = new ListOfWords("/test.kvtml");

		System.out.println(l.toString(true));
		l.displayAllWords();
		// l.removeLine(77);

		Word w = l.getWord(1);
		w.printLines();
		System.out.println("Filename : " + w.getPictureFilename());
		System.out.println();

		// System.out.println(w.hasPicture());
		w.setPicture("monkey.jpg");
		w.printLines();
		System.out.println("Filename : " + w.getPictureFilename());
		System.out.println();

		w.removePicture();
		w.printLines();
		System.out.println("Filename : " + w.getPictureFilename());

		l.displayAllWords();
		l.writeFile("test.kvtml");
	}

	/**
	 * tests
	 */
	public static void main(String[] args) {
		test();
		// test2();

		// for (int i = 0 ; i < 100 ; i++) System.out.println( m.getRandomWord()
		// );
	}
}
