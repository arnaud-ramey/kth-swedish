package Translator;

import IO.IO;
import Lessons.VocParser.ListOfWords;
import Lessons.VocParser.Word;

public class Translator {

	public Translator() {
	}

	public String translate(Word w, int languageIndex) {
		// prepair the url
		String query = w.get0();
		query = query.replace(" ", "%20");
		query = query.replace("/", "");
		String code_language = "";
		String code_language_long = w.getFatherList().getLanguages().elementAt(
				languageIndex);

		if (code_language_long.equalsIgnoreCase("Spanish"))
			code_language = "es";
		if (code_language_long.equalsIgnoreCase("Swedish"))
			code_language = "sv";

		String scheme = "http";
		String authority = "ajax.googleapis.com";
		String path = "ajax/services/language/translate";
		String query2 = "v=1.0&q=" + query + "&langpair=en%7C" + code_language;

		String url = scheme + "://" + authority + "/" + path + "?" + query2;

		// get the url
		String page_content = IO.getWebPageContent(url);

		// System.out.println("Word:" + w.toString_onlyWords());
		// System.out.println("code_language_long: " + code_language_long);
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

		// for (Word w : t.w.getWords()) {
		// System.out.println();
		// t.translate(w);
		// }

		t.translate(words.getRandomWord(), 1);
	}
}
