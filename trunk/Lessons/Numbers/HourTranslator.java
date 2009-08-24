package Lessons.Numbers;

import Asker.Question;

public class HourTranslator {
	static String translate(int hour, int minute) {
		String rep = "";
		int hour_mod = hour % 12;
		int hour_plusone_mod = (hour + 1) % 12;
		if (hour_mod == 0)
			hour_mod = 12;
		if (hour_plusone_mod == 0)
			hour_plusone_mod = 12;

		/* o clock */
		if (minute == 0)
			rep = NumberTranslator.translate(hour_mod);

		/* 0 < x < 25 */
		else if (minute < 25) {
			if (minute == 15)
				rep = "kvart";
			else
				rep = NumberTranslator.translate(minute);
			rep += " över " + NumberTranslator.translate(hour_mod);
		}

		/* 25 <= x <= 35 */
		else if (25 <= minute && minute <= 35) {
			if (minute < 30)
				rep = NumberTranslator.translate(30 - minute) + " i ";
			else if (minute > 30)
				rep = NumberTranslator.translate(minute - 30) + " över ";
			rep += "halv " + NumberTranslator.translate(hour_plusone_mod);
		}

		/* 35 < x < 60 */
		else {
			if (minute == 45)
				rep = "kvart";
			else
				rep = NumberTranslator.translate(60 - minute);
			rep += " i " + NumberTranslator.translate(hour_plusone_mod);
		}
		return rep;
	}

	/**
	 * @return a random hour question
	 */
	public static Question randomQuestion() {
		int hour = (int) (Math.random() * 24);
		int minute = 5 * (int) (Math.random() * 12);
		String hour_string = hour + "." + (minute < 10 ? "0" : "") + minute;
		String que = "Vad är klockan ? " + hour_string +" !";
		String ans = hour_string + " : " + translate(hour, minute);
		return new Question("Hours", que, ans);
	}

	public static void main(String[] args) {
		System.out.println(randomQuestion());
	}
}
