package Lessons.VocParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;

import IO.IO;

public class ListOfWords {
	private String filename = "";

	private Vector<String> languages = new Vector<String>();

	private Vector<String> lines = new Vector<String>();

	private Vector<Word> words = new Vector<Word>();

	public Vector<String> lessons = new Vector<String>();

	/** the max number of times a word was asked */
	int maxCount;

	/** the sum of all the times the words were asked */
	double totalCounts;

	public ListOfWords() {
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
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
		boolean record = false;
		Vector<String> current_lesson_name_queue = new Vector<String>();
		String current_lesson_name = "";

		for (; curr_line_index < lines.size(); curr_line_index++) {
			String l = lines.get(curr_line_index);
			if (l.contains("<lessons>"))
				record = true;
			if (l.contains("</lessons>"))
				break;

			if (record) {
				// beginning of lesson -> push the name
				if (l.contains("<name>")) {
					current_lesson_name_queue.add(Word.extractFieldFromAnchors(
							l, "name"));
					current_lesson_name = current_lesson_name_queue.toString();
					lessons.add(current_lesson_name);
				}
				// end of lesson -> remove the name
				if (l.contains("</container>")) {
					current_lesson_name_queue.remove(current_lesson_name_queue
							.size() - 1);
					current_lesson_name = current_lesson_name_queue.toString();
				}
				// word id -> push the lesson
				if (l.contains("<entry")) {
					String id_string = Word.extractFieldFromQuotes(l, "id");
					int id = Integer.parseInt(id_string);
					getWord(id).lesson_name = current_lesson_name;
					// System.out.println("id:" + id);
				}
			} // end of if record
		}
		// System.out.println(lessons);

		/* compute data for each word */
		compute_number_of_languages_for_each_word();
		compute_counts();
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
	 * @return the average count of a {@link Word} in the list
	 */
	private double getAverageCount() {
		return totalCounts / nbWords();
	}

	/**
	 * find the number of languages for each {@link Word}
	 */
	private void compute_number_of_languages_for_each_word() {
		for (Word w : words)
			w.detectNumberOfLanguages();
	}

	/**
	 * compute the proba of every word
	 */
	private void compute_counts() {
		double sum = 0;

		for (Word w : words) {
			int currCount = w.getCount();
			if (currCount > maxCount)
				maxCount = currCount;
			sum += currCount;
		}

		totalCounts = sum;
	}
	
	/**
	 * @param lesson the index of the lesson
	 * @return the number of words in this lesson
	 */
	public int getNumberOfWordsInLesson(int lesson) {
		int rep = 0;
		for (Word w : words)
			if (w.getLessonNumber() == lesson)
				++rep;
		return rep;
	}

	/**
	 * @return an info {@link String}
	 */
	public String infoString() {
		String rep = "";
		rep += "File:'" + filename + "'";
		rep += " - Languages:" + languages.toString();
		rep += " - Nb lines:" + lines.size();
		rep += " - Nb words:" + nbWords();
		rep += " - Counts:avg=" + ((int) (100f * getAverageCount()) / 100f)
				+ ", max=" + maxCount;
		return rep;
	}

	/**
	 * dispolay all the {@link Word} of words on {@link System}.out
	 */
	public void displayAllWords() {
		for (Word w : words)
			System.out.println(w);
	}

	public static void test() {
		ListOfWords l = new ListOfWords();
		l.readFile("/voc.kvtml");
		System.out.println(l.infoString());
		// l.displayAllWords();
		System.out.println(l.getRandomWord());
	}

	public static void test2() {
		ListOfWords l = new ListOfWords();
		l.readFile("/test_orig.kvtml");

		System.out.println(l.infoString());
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
		// test();
		test2();

		// for (int i = 0 ; i < 100 ; i++) System.out.println( m.getRandomWord()
		// );
	}
}
