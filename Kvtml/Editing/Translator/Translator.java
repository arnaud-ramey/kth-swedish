package Kvtml.Editing.Translator;

import java.io.UnsupportedEncodingException;

import Kvtml.IO.IO;
import Kvtml.VocParser.ListOfWords;
import Kvtml.VocParser.Word;

public class Translator {

	public static String ENGLISH = "English", SPANISH = "Spanish",
			SWEDISH = "Swedish", GERMAN = "German";

	public static String code_language_short(String code_language_long) {
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

	public static String translate(String query, String language_orig,
			String language_dest) {
		System.out.println("Query: '" + query + "'");

		/* prepair the query - adjustments depending of the languages */
		if (language_dest.equals(GERMAN)) {
			if (query.startsWith("a "))
				query = query.replaceFirst("a ", "the ");
			if (query.startsWith("an "))
				query = query.replaceFirst("an ", "the ");
			if (query.startsWith("to "))
				query = query.replace("to ", "");
		}
		if (language_dest.equals(SPANISH)) {
			if (query.startsWith("to "))
				query = query.replace("to ", "");
		}

		/* prepair the url */
		// query = query.replace(" ", "%20");
		// query = query.replace("/", "");
		try {
			query = java.net.URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
		}

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
		// System.out.println("url:" + url);
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

	public static String translate(Word w, int id_language_orig, int id_language_dest) {
		String query = w.getForeignWord(id_language_orig);
		String language_orig = w.getFatherList().getLanguages().elementAt(
				id_language_orig);
		String language_dest = w.getFatherList().getLanguages().elementAt(
				id_language_dest);
		return translate(query, language_orig, language_dest);

	}

	public static void main(String[] args) {
		ListOfWords words = ListOfWords.defaultListOfWords();
		Word w = words.getRandomWord();
		System.out.println(w);
		System.out.println(Translator.translate(w, Word.ENGLISH, Word.SWEDISH));
	}
}
