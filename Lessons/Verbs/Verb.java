package Lessons.Verbs;

import Asker.Question;
import Kvtml.IO.IO;

public class Verb {
	public String imperativ = "";

	public String infinitiv = "";

	public String presens = "";

	public String preteritum = "";

	public String supinum = "";

	public String english_infinitive = "";

	/**
	 * read the values from a line
	 * 
	 * @param line
	 *            the line
	 */
	public void paseFromLine(String line) {
		String[] words = line.split("\t");

		/* test lengths */
		if (words.length < 5 || words.length > 6) {
			System.out.println("!!! The line '" + line
					+ "' does not contain five fields !");
			return;
		}
		/* get each word */
		english_infinitive = words[0];
		infinitiv = words[1];
		presens = words[2];
		preteritum = words[3];
		supinum = words[4];

		/* generate imperative */
		if (words.length == 5) {
			if (infinitiv.endsWith("a") && infinitiv.length() > 2)
				imperativ = infinitiv.substring(0, infinitiv.length() - 1)
						+ " !";
			else
				imperativ = infinitiv + "!";
		} else
			imperativ = words[5];
	}

	/**
	 * @return a random field of the verb
	 */
	public String randomField() {
		int index = (int) (Math.random() * 5);
		switch (index) {
		case 1:
			return infinitiv;
		case 2:
			return presens;
		case 3:
			return preteritum;
		case 4:
			return supinum;
		case 5:
			return imperativ;
		default:
			return english_infinitive;
		}
	}

	/**
	 * string version
	 */
	public String toString() {
		String rep = english_infinitive.toUpperCase() + " : ";
		rep += infinitiv + ", ";
		rep += presens + ", ";
		rep += preteritum + ", ";
		rep += supinum;
		rep += " (" + imperativ + ")";
		return rep;
	}

	public static Question randomVerb() {
		// URL url = Verb.class.getResource("/Lessons/Verbs/verbs.txt");
		// System.out.println(url.getPath());
		// String[] lines = Tc.lignesDeFichier( url.getPath() );

		String[] lines = IO.readFile("/Lessons/Verbs/verbs.txt");

		int index;
		do
			index = (int) (Math.random() * lines.length);
		while (lines[index].startsWith("#"));
		Verb verb = new Verb();
		verb.paseFromLine(lines[index]);
		return new Question("Irregular verbs", verb.randomField(), verb
				.toString());
	}

	public static void main(String[] args) {
		System.out.println(randomVerb().toString());
	}
}
