package IO;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.Scanner;

import Lessons.Verbs.Verb;

public class IO {

	/**
	 * Fetch the HTML content of the page as simple text.
	 * 
	 * @param fURL
	 *            the URL
	 * @return the content
	 */
	public static String getWebPageContent(String path) {
		String END_OF_INPUT = "\\Z";

		String result = null;
		URLConnection connection = null;
		URL fURL = null;
		try {
			fURL = new URL(path);
			connection = fURL.openConnection();
			connection.addRequestProperty("User-Agent", "Mozilla/4.76");
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter(END_OF_INPUT);
			result = scanner.next();
		} catch (IOException ex) {
			System.out.println("Cannot open connection to " + fURL.toString());
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * extract the url of the images from the Google Image results
	 * 
	 * @param page_content
	 *            the content of the page
	 * @return a {@link LinkedList} of url
	 */
	public static LinkedList<String> extractTagsFromLine(String page_content,
			String begin_tag, String end_tag) {
		LinkedList<String> rep = new LinkedList<String>();
		int beginIndex = 0, endIndex = 0;
		// System.out.println("*** content:" + page_content);

		while (true) {
			beginIndex = page_content.indexOf(begin_tag, beginIndex);
			if (beginIndex == -1)
				break;
			beginIndex += begin_tag.length();
			endIndex = page_content.indexOf(end_tag, beginIndex);
			if (endIndex == -1)
				break;
			String img_url = page_content.substring(beginIndex, endIndex);
			rep.add(img_url);

			// System.out.println("*** beginIndex :" + beginIndex);
			// System.out.println("*** endIndex :" + endIndex);
			// System.out.println("*** SUB : " + img_url);
		}

		return rep;
	}

	/**
	 * read a file and return the list of the lines
	 * 
	 * @param filename
	 *            the name of the file
	 * @return
	 */
	public static String[] readFile(String filename) {
		InputStream stream = Verb.class.getResourceAsStream(filename);
		Reader r = new InputStreamReader(stream);
		try {
			r = new InputStreamReader(stream, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
		}
		StringBuffer buf = new StringBuffer();
		while (true) {
			int c = -1;
			try {
				c = r.read();
			} catch (IOException e) {
			}
			if (c == -1)
				break;
			buf.append((char) c);
		}
		String[] lines = buf.toString().split("\n");

		// System.out.println("lines:");
		// for (String s : lines)
		// System.out.println(" * " + s);
		return lines;
	}

	/**
	 * Reads a integer from the console
	 * 
	 * @return the read value
	 */
	public static int readInt() {
		Scanner in = new Scanner(System.in);
		int choice = in.nextInt();
		// in.close();
		return choice;
	}

	/**
	 * tests
	 */
	public static void main(String[] args) {
		System.out.print("Value of c ? ");
		int c = readInt();
		System.out.println("c:" + c);
	}
}
