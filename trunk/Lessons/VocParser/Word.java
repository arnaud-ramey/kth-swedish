package Lessons.VocParser;


public class Word {
	ListOfWords fatherList;

	/** the line where the word is beginning in the kvtml file */
	int beginningLine;

	/** the index of the word in the father list */
	int index;

	/** the name of the lesson */
	String lesson_name;

	/** the number of available languages */
	int numberOfLanguages;

	double proba;

	double[] probasLanguages = new double[] { .25, .25, .5 };

	public Word(ListOfWords m, int l, int n) {
		fatherList = m;
		beginningLine = l;
		index = n;
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

	public int getLessonNumber() {
		return fatherList.lessons.indexOf(lesson_name);
	}

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
		int goodLineNb = getLineContaining(lineNb, tag);
		if (goodLineNb == -1)
			return;
		String line = fatherList.getLine(goodLineNb);

		int caretPos = line.indexOf(tag) + tag.length();
		String newLine = line.substring(0, caretPos);

		// beginning of the line
		while (true) {
			char c = line.charAt(caretPos);
			newLine += c;
			if (c == '>')
				break;
			caretPos++;
		}

		// substitution
		newLine += value;

		// end of the line
		while (line.charAt(caretPos) != '<')
			caretPos++;
		newLine += line.substring(caretPos);

		// System.out.println("line:" + newLine);
		fatherList.setLine(goodLineNb, newLine);
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
	 * get the field containing the foreign word
	 * 
	 * @param indx
	 *            the number of the language
	 * @return the word
	 */
	public String getForeignWord(int indx) {
		int l = getLineContaining(beginningLine, "translation id=\"" + indx);
		// System.out.println("line:" + l);

		// jump a line
		l = l + 1;
		String rep = getField(l, "text");
		return rep;
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
	 * change the number of times the word was asked
	 * 
	 * @param val
	 *            the new value
	 */
	public void setCount(int val) {
		fatherList.totalCounts -= getCount();
		setField(beginningLine, "count", "" + val);
		fatherList.maxCount = Math.max(fatherList.maxCount, val);
		fatherList.totalCounts += val;
	}

	public void increaseCount() {
		setCount(1 + getCount());
	}

	public int getErrorCount() {
		return getFieldInt(beginningLine, "errorcount");
	}

	public void setErrorCount(int val) {
		setField(beginningLine, "errorcount", "" + val);
	}

	public void increaseErrorCount() {
		setErrorCount(1 + getErrorCount());
	}

	public void decreaseErrorCount() {
		if (getErrorCount() > 0)
			setErrorCount(-1 + getErrorCount());
	}

	/**
	 * @return the success rate in %
	 */
	public double successRate() {
		int count = getCount();
		if (count == 0)
			return 100;
		return 100 * (count - getErrorCount()) / count;
	}

	/**
	 * the proba of being chosen - between 1 and 100
	 */
	public void computeProba() {
		double proba = 30;

		if (getCount() > 0) {
			// word already asked ?
			double alreadyAsked = 1.0f * (fatherList.maxCount - getCount())
					/ fatherList.maxCount;
			double alreadyMistaken = 1.0f * getErrorCount() / getCount();
			// System.out.println("alreadyAsked:" + alreadyAsked + " -
			// alreadyMistaken:" + alreadyMistaken);

			proba = 20 * alreadyAsked + 80 * alreadyMistaken;
			proba = Math.max(1, Math.min(proba, 100));
		}

		this.proba = proba;
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

	public void know() {
		increaseCount();
		decreaseErrorCount(); // only if needed
		computeProba();
	}

	public void unknow() {
		increaseCount();
		increaseErrorCount();
		computeProba();
	}

	/** hi level */

	public void addField(int lineNb, String tag, String value) {
		String line = value;
		line = "<" + tag + ">" + line + "</" + tag + ">";
		
		// add the good number of spaces for a correct indentation
		String lineBefore = fatherList.getLine(lineNb-1);
		int i = 0;
		while (i < lineBefore.length() && lineBefore.charAt(i++) == ' ')
			line = " " + line;
		
		fatherList.addLine(lineNb, line);
	}

	/**
	 * returns <code>true</code> if there is already a picture
	 */
	public boolean hasPicture() {
		return (getLineContaining(beginningLine, "<image>") != -1);
	}
	
	/**
	 * @return the filename of the picture
	 */
	public String getPictureFilename() {
		if (!hasPicture())
			return "";
		return getField(getLineContaining(beginningLine, "<image>"), "image");
	}

	/**
	 * change the picture of a word
	 * 
	 * @param filename
	 *            the new picture
	 */
	public void setPicture(String filename) {
		if (hasPicture()) {
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
	 * remove the picture associated to the image
	 */
	public void removePicture() {
		// if there is no picture -> do nothing
		if (!hasPicture())
			return;
		
		int line_index = beginningLine;
		line_index = getLineContaining(line_index, "<image>");
		while (line_index != -1) {
			fatherList.removeLine(line_index);
			line_index = getLineContaining(line_index, "<image>");
		}
	}

	/**
	 * @return the end line number
	 */
	public int endLineIndex() {
		int rep = beginningLine - 1;
		String s;
		do {
			rep++;
			s = fatherList.getLine(rep);
		} while (!s.contains("</entry>"));
		return rep;
	}

	/**
	 * print only the lines of the {@link ListOfWords} concerning this
	 * {@link Word}
	 */
	public void printLines() {
		for (int i = beginningLine; i <= endLineIndex(); i++) {
			System.out.println(fatherList.getLine(i));
		}
	}

	public String toString_onlyWords() {
		String rep = "";
		for (int i = 0; i < numberOfLanguages; i++) {
			rep += (i > 0 ? " | " : "") + getForeignWord(i);
		}
		return rep;
	}

	public String toString() {
		String rep = "";
		rep += "#" + index;
		rep += " (";
		rep += beginningLine;
		rep += ", " + lesson_name + "=" + getLessonNumber();
		rep += ") ";
		rep += toString_onlyWords();
		rep += (hasPicture() ? " [" + getPictureFilename() + "]" : "");
		rep += " - " + (getCount() - getErrorCount()) + " of " + getCount();
		rep += " (" + (int) successRate() + "%)";
		rep += "->proba:" + (int) proba;
		return rep;
	}

	public static void main(String[] args) {
		ListOfWords m = new ListOfWords();
		m.readFile("/voc.kvtml");
		System.out.println(m.infoString());

		// nbWord = 209;
		Word w = m.getRandomWord();
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
	}
}
