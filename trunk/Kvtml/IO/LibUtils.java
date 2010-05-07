package Kvtml.IO;

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

	/**
	 * @param s
	 *            a {@link String}
	 * @return s with the first letter in upper case, and everything else lower
	 *         case
	 */
	public static String firstLetterUpperCase_otherLowerCase(String s) {
		String low = s.toLowerCase();
		return firstLetterUpperCase(low);
	}

	public static String breakInTwo(String msg, boolean html) {
		int length = msg.length();
		int pos = msg.indexOf(' ', length / 2);
		if (pos == -1)
			return msg;
		String ans = msg.substring(0, pos) + (html ? "<br>" : "\n")
				+ msg.substring(pos + 1);
		return ans;
	}

	public static void main(String[] args) {
		System.out.println(breakInTwo("allo allo", true));
		System.out.println(breakInTwo("al llololoallo", true));
		System.out.println(breakInTwo("alllololoal lo", true));
	}
}
