package Translator;

import IO.IO;
import Lessons.VocParser.ListOfWords;
import Lessons.VocParser.Word;

public class Translator {

	public static String ENGLISH = "English", SPANISH = "Spanish",
			SWEDISH = "Swedish", GERMAN = "German";

	public Translator() {
	}

	public String code_language_short(String code_language_long) {
		if (code_language_long.equalsIgnoreCase(ENGLISH))
			return "en";
		if (code_language_long.equalsIgnoreCase(SPANISH))
			return "es";
		if (code_language_long.equalsIgnoreCase(SWEDISH))
			return "sv";
		if (code_language_long.equalsIgnoreCase(GERMAN))
			return "de";
		return "";
	}

	public String translate(String query, String language_orig,
			String language_dest) {
		System.out.println("Query: '" + query + "'");
		/* prepair the url */
		// query = query.replace(" ", "%20");
		// query = query.replace("/", "");
		query = java.net.URLEncoder.encode(query);

		String scheme = "http";
		String authority = "ajax.googleapis.com";
		String path = "ajax/services/language/translate";
		String query2 = "v=1.0&q=" + query;
		query2 += "&langpair=" + code_language_short(language_orig) + "%7C"
				+ code_language_short(language_dest);

		String url = scheme + "://" + authority + "/" + path + "?" + query2;

		// get the url
		String page_content = IO.getWebPageContent(url);

		// System.out.println("Query: '" + query + "'");
		// System.out.println("Language orig: " +
		// code_language_short(language_orig));
		// System.out.println("Language dest: " +
		// code_language_short(language_dest));
		//System.out.println("url:" + url);
		// System.out.println("page_content:" + page_content);

		// get the translation
		String trans = "Not found";
		try {
			trans = IO.extractTagsFromLine(page_content, "translatedText\":\"",
					"\"}").getFirst();
		} catch (Exception e) {
		}

		// System.out.println("trans:" + trans);

		return trans;
	}

	public String translate(Word w, int id_language_orig, int id_language_dest) {
		String query = w.getForeignWord(id_language_orig);
		String language_orig = w.getFatherList().getLanguages().elementAt(
				id_language_orig);
		String language_dest = w.getFatherList().getLanguages().elementAt(
				id_language_dest);
		return translate(query, language_orig, language_dest);

	}

	public static void main(String[] args) {
		ListOfWords words = ListOfWords.defaultListOfWords();
		Translator t = new Translator();
		// t.translate(t.w.getWord(98));
		// System.out.println("\n\n***\n\n");

		// Word w = null;
		// while (true) {
		// w = t.w.getRandomWord();
		// if (!w.containsLanguage(2)) break;
		// }
		// t.translate(w);

		// for (Word w : words.getWords()) {
		// System.out.println();
		// t.translate(w,0,1);
		// }

		Word w = words.getRandomWord();
		System.out.println(w);
		System.out.println(t.translate(w, 1, 0));
	}
}
