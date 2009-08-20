package Lessons.Numbers;

import Asker.Question;

public class RankingTranslator {
	static String numbers[] = { "", "första", "andra", "tredje", "fjärde", "femte", "sjätte",
		"sjunde", "åttonde", "nionde", "tionde", "elfte", "tolfte"};

	static String translate(int n) {
		String rep = "";

		/* <= 12 */
		if (n <= 12)
			return numbers[n];

		/* 12 < x < 20 */
		if (n > 12 && n < 20)
			return NumberTranslator.translate( n ) + "de";

		/* 20 <= x < 100 */
		int t = 10;
		if (n >= t && n < 100) {
			if (n % t != 0)
				return NumberTranslator.translate(t * (int) (n / t)) + " " + translate(n%t);
			else 
				return NumberTranslator.translate(n) + "nde";
		}

		/* 100 <= x < 1000 */
		t = 100;
		if (n >= t && n < 10*t) {
			if (n % t != 0)
				return NumberTranslator.translate(t * (int) (n / t)) + " " + translate(n%t);
			else 
				return NumberTranslator.translate(n) + "de";
		}

		/* 1000 <= x < 10000 */
		t = 1000;
		if (n >= t && n < 10*t) {
			if (n % t != 0)
				return NumberTranslator.translate(t * (int) (n / t)) + " " + translate(n%t);
			else
				return NumberTranslator.translate(n) + "de";
		}

		return rep;
	}

	public static Question randomQuestion(int max) {
		int n = 1 +(int) ((max-1) * Math.random());
		String q = "the #" + n;
		String r = "den " + translate(n);
		if (Math.random() < .5)
			return new Question("Rankings", q, r);
		return new Question("Rankings", r, q);
	}

	public static void main(String[] args) {
		for (int i = 0; i < 6; i++)
			System.out.println(randomQuestion(10000));
//		System.out.println(translate(125));
	}

}