package Lessons.Numbers;

import Asker.Question;

public class NumberTranslator {
	static String numbers[] = { "", "ett", "två", "tre", "fyra", "fem", "sex",
			"sju", "åtta", "nio", };

	static String ten_to_twenty[] = { "tio", "elva", "tolv", "tretton",
			"fjorton", "femton", "sexton", "sjutton", "arton", "nitton", };

	static String multiples_of_ten[] = { "", "", "tjugo", "trettio", "fyrtio",
			"femtio", "sextio", "sjuttio", "åttio", "nittio", };

	static String translate(int n) {
		if (n == 0)
			return "noll";

		String rep = "";
		String n_s = "" + n;

		/* thousands */
		if (n_s.length() >= 4) {
			int thousands = Integer.parseInt("" + n_s.charAt(n_s.length() - 4));
			if (thousands > 0)
				rep += numbers[thousands] + " tusen";
		}

		/* hundreds */
		if (n_s.length() >= 3) {
			int hundreds = Integer.parseInt("" + n_s.charAt(n_s.length() - 3));
			if (hundreds > 0)
				rep += (rep.length()>0?" ":"") + numbers[hundreds] + " hundra";
		}

		/* dozens and units */
		if (n_s.length() >= 2) {
			int dozen = Integer.parseInt("" + n_s.charAt(n_s.length() - 2));
			int units = Integer.parseInt("" + n_s.charAt(n_s.length() - 1));
			if (dozen == 0) { /* 0 to 9 */
				rep += (rep.length()>0 && units > 0 ? " " : "") + numbers[units];
			} else if (dozen == 1) { /* 10 to 20 */
				rep += (rep.length()>0?" ":"") + ten_to_twenty[units];
			} else {
				rep += multiples_of_ten[dozen] + (units != 0 ? " " + numbers[units] : "");
			}
		}

		/* only a unit */
		if (n_s.length() == 1)
			rep = numbers[Integer.parseInt("" + n_s.charAt(n_s.length() - 1))];

		return rep;
	}

	public static Question randomQuestion(int max) {
		int n = (int) (max * Math.random());
		String n_s = translate(n);
		if (Math.random() < .5)
			return new Question("Numbers", "" + n, n_s);
		return new Question("Numbers", n_s, "" + n);
	}

	public static void main(String[] args) {
		System.out.println("'" + translate(200) + "'");
	}

}