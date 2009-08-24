package IO;

public class LibUtils {
	public static String emphasize(String s) {
		return "<b>" + s + "</b>";
	}

	/**
	 * @param s
	 *            a {@link String}
	 * @return s with the first letter in upper case
	 */
	public static String firstLetterUpperCase(String s) {
		String rep = s.substring(0, 1).toUpperCase() + s.substring(1);
		return rep;
	}

}
