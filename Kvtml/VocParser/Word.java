package Kvtml.VocParser;

import java.util.Vector;

import Kvtml.VocParser.Lessons.LessonTree;

public class Word {
	public static int ENGLISH = 0, SWEDISH = 1, SPANISH = 2, GERMAN = 3;

	/** the line where the word is beginning in the kvtml file */
	int beginningLine;

	ListOfWords fatherList;

	/** the index of the word in the father list */
	int index;

	/** the name of the lesson */
	// String lesson_name;
	private LessonTree lessonTree;

	/** the number of available languages */
	int numberOfLanguages;

	double proba;

	double[] probasLanguages = new double[] { .25, .25, .5 };

	/**
	 * constructor
	 * 
	 * @param father
	 * @param begin_line
	 * @param indx
	 */
	public Word(ListOfWords father, int begin_line, int indx) {
		fatherList = father;
		beginningLine = begin_line;
		index = indx;
	}

	public ListOfWords getFatherList() {
		return fatherList;
	}

	/**
	 * add a new field to the word
	 * 
	 * @param lineNb
	 *            the number where to insert the line
	 * @param tag
	 *            the name of the tag
	 * @param value
	 *            the value of this tag
	 */
	public void addField(int lineNb, String tag, String value) {
		String line = value;
		line = "<" + tag + ">" + line + "</" + tag + ">";
		line = indentLine(lineNb - 1, line, 0);
		fatherList.addLine(lineNb, line);
	}

	public String indentLine(int lineNb, String line, int addedSpaces) {
		// add the good number of spaces for a correct indentation
		String lineBefore = fatherList.getLine(lineNb);

		int nb_spaces_to_add = 0;
		while (nb_spaces_to_add < lineBefore.length()
				&& lineBefore.charAt(nb_spaces_to_add) == ' ') {
			nb_spaces_to_add++;
		}
		nb_spaces_to_add += addedSpaces;

		for (int i = 0; i < nb_spaces_to_add; i++)
			line = " " + line;
		return line;
	}

	/**
	 * the proba of being chosen - between 1 and 100
	 */
	public void computeProba(WordPicker wp) {
		double newProba = 30;
		int thisCount = getCount();
		int maxCount = wp.getMaxWordCount();

		if (getCount() > 0) {
			// word already asked ?
			double alreadyAsked = 1.0f * (maxCount - thisCount) / maxCount;
			double alreadyMistaken = 1.0f * getErrorCount() / thisCount;
			// System.out.println("alreadyAsked:" + alreadyAsked + " -
			// alreadyMistaken:" + alreadyMistaken);

			newProba = 20 * alreadyAsked + 80 * alreadyMistaken;
			newProba = Math.max(1, Math.min(newProba, 100));
		}

		this.proba = newProba;
	}

	public boolean containsLine(String line) {
		return (getLineContaining(beginningLine, line) != -1);
	}

	public boolean containsField(String tag) {
		return containsLine("<" + tag);
	}

	public boolean containsLanguage(int id) {
		String line = "translation id=\"" + id + "\"";
		String line_bad = "<" + line + " />";
		return containsLine(line) && !containsLine(line_bad);
	}

	/**
	 * returns <code>true</code> if there is already a picture
	 */
	public boolean containsPicture() {
		return containsField("image");
	}

	public void decreaseErrorCount() {
		if (getErrorCount() > 0)
			setErrorCount(-1 + getErrorCount());
	}

	/**
	 * compute the number of languages
	 */
	public void detectNumberOfLanguages() {
		numberOfLanguages = -1;
		int line = beginningLine;
		do {
			numberOfLanguages++;
			line = getLineContaining(1 + line, "translation id=\"");
			// System.out.println("line:" + line);
		} while (line != -1);
		// System.out.println("numberOfLanguages:" + numberOfLanguages);
	}

	public String get0() {
		return getForeignWord(0);
	}

	public String get1() {
		return getForeignWord(1);
	}

	public String get2() {
		return getForeignWord(2);
	}

	/**
	 * @return the number of times the word was asked
	 */
	public int getCount() {
		return getFieldInt(beginningLine, "count");
	}

	/**
	 * @return the end line number
	 */
	public int getEndLineIndex() {
		int rep = beginningLine - 1;
		String s;
		do {
			rep++;
			s = fatherList.getLine(rep);
		} while (!s.contains("</entry>"));
		return rep;
	}

	public int getErrorCount() {
		return getFieldInt(beginningLine, "errorcount");
	}

	/**
	 * return the string of the value delimited by a chosen tag
	 * 
	 * @param lineNb
	 *            the beginning line
	 * @param tag
	 *            the name of the tag
	 * @return the string value
	 */
	public String getField(int lineNb, String tag) {
		int goodLine = getLineContaining(lineNb, tag);
		if (goodLine == -1)
			return "";
		String line = fatherList.getLine(goodLine);
		return extractFieldFromAnchors(line, tag);
	}

	/**
	 * @see getField
	 * @return the conversion in int
	 */
	public int getFieldInt(int lineNb, String tag) {
		String s = getField(lineNb, tag);
		if (s == "")
			return 0;
		return Integer.parseInt(s);
	}

	/**
	 * get the field containing the foreign word
	 * 
	 * @param langIndx
	 *            the number of the language
	 * @return the word
	 */
	public String getForeignWord(int langIndx) {
		// find the beginning and end line of the possible foreign word
		int beginning_line = getLineContaining(beginningLine,
				"translation id=\"" + langIndx);
		int end_line = getEndLineIndex();
		if (langIndx < numberOfLanguages - 1)
			end_line = getLineContaining(beginningLine, "translation id=\""
					+ (langIndx + 1));
		// System.out.println("numberOfLanguages:" + numberOfLanguages);
		// System.out.println("beginning_line:" + beginning_line);
		// System.out.println("end_line:" + end_line);

		// jump a line
		beginning_line = beginning_line + 1;

		// try to find <text>
		int text_line = getLineContaining(beginning_line, "<text>");
		// System.out.println("text_line:" + text_line);
		if (text_line < beginning_line || text_line > end_line) // out of bounds
			return "";
		String rep = getField(text_line, "text");

		// String rep = getField(beginning_line, "text");
		return rep;

	}

	public int getIndex() {
		return index;
	}

	// public int getLessonNumber() {
	// return fatherList.lessons.indexOf(lesson_name);
	// }
	//
	// public String getLessonName() {
	// return lesson_name;
	// }

	/**
	 * return a line containing a special text
	 * 
	 * @param lineNb
	 * @param tag
	 *            the searched text
	 * @return the line number
	 */
	public int getLineContaining(int lineNb, String tag) {

		int currentLineNb = lineNb;
		while (currentLineNb < fatherList.nbLines()) {
			String currentLine = fatherList.getLine(currentLineNb);
			if (currentLine.contains(tag))
				break;
			if (currentLine.contains("</entry>"))
				return -1;
			else
				currentLineNb++;
		}
		if (currentLineNb >= fatherList.nbLines())
			return -1;
		return currentLineNb;
	}

	/**
	 * @return the numberOfLanguages
	 */
	public int getNumberOfLanguages() {
		return numberOfLanguages;
	}

	/**
	 * @return the filename of the picture
	 */
	public String getPictureFilename() {
		if (!containsPicture())
			return "";
		return getField(getLineContaining(beginningLine, "<image>"), "image");
	}

	public int getRandomLanguage() {
		double choice = Math.random();
		double sumProba = 0;
		int chosenLanguage;
		for (chosenLanguage = 0; chosenLanguage < numberOfLanguages; chosenLanguage++) {
			sumProba += probasLanguages[chosenLanguage];
			if (sumProba > choice)
				return chosenLanguage;
		}
		return 0;
	}

	public String getLessonName() {
		return lessonTree.getLessonName();
	}

	public String getLessonFullName() {
		return lessonTree.getLessonFullName();
	}

	/**
	 * @return the success rate in %
	 */
	public double getSuccessRate() {
		int count = getCount();
		if (count == 0)
			return 100;
		return 100 * (count - getErrorCount()) / count;
	}

	public void increaseCount() {
		setCount(1 + getCount());
	}

	public void increaseErrorCount() {
		setErrorCount(1 + getErrorCount());
	}

	public void know(WordPicker wp) {
		increaseCount();
		decreaseErrorCount(); // only if needed
		computeProba(wp);
	}

	/**
	 * print only the lines of the {@link ListOfWords} concerning this
	 * {@link Word}
	 */
	public void printLines() {
		for (int i = beginningLine; i <= getEndLineIndex(); i++) {
			System.out.println(fatherList.getLine(i));
		}
	}

	/**
	 * remove the picture associated to the image
	 */
	public void removePicture() {
		// if there is no picture -> do nothing
		if (!containsPicture())
			return;

		int line_index = beginningLine;
		line_index = getLineContaining(line_index, "<image>");
		while (line_index != -1) {
			fatherList.removeLine(line_index);
			line_index = getLineContaining(line_index, "<image>");
		}
	}

	/**
	 * change all the fields with a given tag
	 * 
	 * @param firstLine
	 *            the beginning line
	 * @param tag
	 *            the name of the tag
	 * @param newValue
	 *            the new value
	 */
	public void setAllFields(int firstLine, String tag, String newValue) {
		int line_index = firstLine;
		line_index = getLineContaining(line_index, "<" + tag + ">");
		while (line_index != -1) {
			setField(line_index, tag, newValue);
			line_index++;
			line_index = getLineContaining(line_index, "<" + tag + ">");
		}
	}

	/**
	 * change the number of times the word was asked
	 * 
	 * @param val
	 *            the new value
	 */
	public void setCount(int val) {
		setField(beginningLine, "count", "" + val);
	}

	public void setErrorCount(int val) {
		setField(beginningLine, "errorcount", "" + val);
	}

	public void setLessonTree(LessonTree lessonTree) {
		this.lessonTree = lessonTree;
	}

	/**
	 * change the value of a field
	 * 
	 * @param lineNb
	 *            the number of the line
	 * @param tag
	 *            the name of the tag
	 * @param value
	 *            the new value
	 */
	public void setField(int lineNb, String tag, String value) {
		// // //we add the "< />" around
		// // tag = '<' + tag + "/>";
		// System.out.println("setField(" + tag + ":" + value + ")");
		//		
		// // find the good line
		// int goodLineNb = getLineContaining(lineNb, tag);
		// if (goodLineNb == -1)
		// return;
		// String line = fatherList.getLine(goodLineNb);
		//
		//		
		// int caretPos = line.indexOf(tag) + tag.length();
		// String newLine = line.substring(0, caretPos);
		//
		// // copy the beginning of the line
		// while (true) {
		// char c = line.charAt(caretPos);
		// newLine += c;
		// if (c == '>')
		// break;
		// caretPos++;
		// }
		//
		// // substitution
		// newLine += value;
		//
		// // end of the line
		// while (line.charAt(caretPos) != '<')
		// caretPos++;
		// newLine += line.substring(caretPos);
		//
		// // System.out.println("line:" + newLine);
		// fatherList.setLine(goodLineNb, newLine);

		String line_oneline = "<" + tag + " />";
		String line_multi_begin = "<" + tag + ">";
		String line_multi_end = "</" + tag + ">";

		int lineBegin = 0;
		int nbLinesToErase = 0;

		/*
		 * find the line where to insert
		 */
		// first case : contains <tag />
		if (containsLine(line_oneline)) {
			// System.out.println("case 1");
			lineBegin = getLineContaining(beginningLine, line_oneline);
			nbLinesToErase = 1;
		}
		// second case : contains <tag> </tag>
		else if (containsLine(line_multi_begin)) {
			lineBegin = getLineContaining(beginningLine, line_multi_begin);
			int lineEnd = getLineContaining(lineBegin, line_multi_end);
			// System.out.println("case 2");
			// System.out.println("lineBegin: " + lineBegin);
			// System.out.println("lineEnd: " + lineEnd);
			if (lineEnd == -1)
				nbLinesToErase = 1;
			else
				nbLinesToErase = lineEnd - lineBegin + 1;
		}
		// last case : contains nothing
		else {
			lineBegin = getEndLineIndex();
			nbLinesToErase = 0;
		}

		// System.out.println("lineBegin:" + lineBegin + " - "
		// + fatherList.getLine(lineBegin));
		// System.out.println("nbLineToErase:" + nbLinesToErase);

		/*
		 * erase the old lines
		 */
		for (int i = 0; i < nbLinesToErase; i++)
			fatherList.removeLine(lineBegin);

		/*
		 * add new lines
		 */
		Vector<String> lines = new Vector<String>();
		String textLine = line_multi_begin + value + line_multi_end;
		lines.add(indentLine(lineBegin - 1, textLine, 0));

		fatherList.addLines(lineBegin, lines);
	}

	/**
	 * set the translation of the word
	 * 
	 * @param idLanguage
	 * @param text
	 */
	public void setForeignWord(int idLanguage, String text) {
		String line_one_line = "<translation id=\"" + idLanguage + "\"/>";
		String line_multi_begin = "<translation id=\"" + idLanguage + "\" >";
		String line_multi_end = "</translation>";

		int lineBegin = 0;
		int nbLinesToErase = 0;
		Vector<String> lines = new Vector<String>();

		/*
		 * find the line where to insert
		 */
		// first case : contains <translation id="1" />
		if (containsLine(line_one_line)) {
			lineBegin = getLineContaining(beginningLine, line_one_line);
			nbLinesToErase = 1;
		}
		// second case : contains <translation id="1" >
		else if (containsLine(line_multi_begin)) {
			lineBegin = getLineContaining(beginningLine, line_multi_begin);
			int lineEnd = getLineContaining(lineBegin, line_multi_end);
			// System.out.println("lineBegin: " + lineBegin);
			// System.out.println("lineEnd: " + lineEnd);
			nbLinesToErase = lineEnd - lineBegin + 1;
		}
		// last case : contains nothing
		else {
			lineBegin = getEndLineIndex();
			nbLinesToErase = 0;
		}

		/*
		 * erase the old lines
		 */
		for (int i = 0; i < nbLinesToErase; i++)
			fatherList.removeLine(lineBegin);

		/*
		 * add new lines
		 */
		lines.add(indentLine(lineBegin - 1, line_multi_begin, 0));
		String textLine = "<text>" + text + "</text>";
		lines.add(indentLine(lineBegin - 1, textLine, 2));
		if (containsPicture()) {
			String pictureLine = "<image>" + getPictureFilename() + "</image>";
			lines.add(indentLine(lineBegin - 1, pictureLine, 2));
		}
		lines.add(indentLine(lineBegin - 1, line_multi_end, 0));

		fatherList.addLines(lineBegin, lines);
	}

	/**
	 * change the picture of a word
	 * 
	 * @param filename
	 *            the new picture
	 */
	public void setPicture(String filename) {
		if (containsPicture()) {
			setAllFields(beginningLine, "image", filename);
		}
		/* if has no picture, change the field */
		else {
			int line_index = beginningLine;
			line_index = getLineContaining(line_index, "<text>");
			while (line_index != -1) {
				line_index++;
				addField(line_index, "image", filename);
				line_index = getLineContaining(line_index, "<text>");
			}
		}
	}

	/**
	 * the string version
	 */
	public String toString() {
		String rep = "";
		rep += "#" + index;
		rep += " (line ";
		rep += beginningLine;
		rep += ", lesson:" + lessonTree.getLessonFullName();
		rep += ") ";
		rep += toString_onlyWords();
		rep += (containsPicture() ? " [" + getPictureFilename() + "]" : "");
		rep += " - " + (getCount() - getErrorCount()) + " of " + getCount();
		rep += " (" + (int) getSuccessRate() + "%)";
		rep += "->proba:" + (int) proba;
		return rep;
	}

	/**
	 * @return the short {@link String} version
	 */
	public String toString_onlyWords() {
		String rep = "";
		for (int i = 0; i < numberOfLanguages; i++) {
			rep += (i > 0 ? " | " : "")
					+ (containsLanguage(i) ? getForeignWord(i) : "");
		}
		return rep;
	}

	public void unknow(WordPicker wp) {
		increaseCount();
		increaseErrorCount();
		computeProba(wp);
	}

	/**
	 * extract a value surrounded by anchors <tag> </tag>
	 * 
	 * @param line
	 *            the string containing everything
	 * @param tag
	 *            the name of the tag
	 * @return the value
	 */
	public static String extractFieldFromAnchors(String line, String tag) {
		String rep = "";
		int caretPos = line.indexOf(tag) + tag.length();
		boolean adding = false;

		while (true) {
			char c = line.charAt(caretPos);
			// System.out.println(c);
			if (c == '<')
				break;
			if (adding)
				rep += c;
			if (c == '>')
				adding = true;
			caretPos++;
		}

		return rep;
	}

	/**
	 * extract a value surrounded by anchors <tag> </tag>
	 * 
	 * @param line
	 *            the string containing everything
	 * @param tag
	 *            the name of the tag
	 * @return the value
	 */
	public static String extractFieldFromQuotes(String line, String tag) {
		String search = tag + "=\"";
		int begin = line.indexOf(search) + search.length();
		int end = line.indexOf("\"", begin + 1);
		// System.out.println(line + " - " + begin + " - " + end);
		return line.substring(begin, end);
	}

	static void test1() {
		ListOfWords m = ListOfWords.defaultListOfWords();
		System.out.println(m.toString(true));

		// nbWord = 209;
		// Word w = m.getRandomWord();
		Word w = m.getWord(359);
		System.out.println(w);
		// w.unknow();
		// System.out.println(w);
		// w.unknow();
		// System.out.println(w);
		// w.unknow();
		// System.out.println(w);
		// w.know();
		// System.out.println(w);

		// System.out.println(w.getRandomLanguage());

		w.printLines();
		w.setForeignWord(2, "new");
		w.setCount(10);
		System.out.println();
		w.printLines();
	}

	static void test2() {
		ListOfWords m = ListOfWords.defaultListOfWords();
		System.out.println(m.toString(true));
		// Word w = m.getWord(3);
		Word w = m.getRandomWord();
		w.printLines();
		System.out.println(w.toString());
		System.out.println(w.toString_onlyWords());
	}

	public static void main(String[] args) {
		test2();
	}
}
