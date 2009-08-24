package Lessons.Pronouns;

import Asker.Question;
import IO.LibUtils;

public class Pronouns {
	static String[] nominative = { "jag", "du", "han", "hon", "den", "det",
			"vi", "ni", "de" };

	static String[] objective = { "mig", "dig", "honom", "henne", "den", "det",
			"oss", "er", "dem" };

	static String[] reflexive = { "mig", "dig", "sig", "sig", "sig", "sig",
			"oss", "er", "sig" };

	// /// possessive forms
	static String[] possessive_en = { "min", "din", "hans", "hennes", "dess",
			"", "vår", "er", "deras" };

	static String[] possessive_ett = { "mitt", "ditt", "hans", "hennes",
			"dess", "", "vårt", "ert", "deras" };

	static String[] possessive_plural = { "mina", "dina", "hans", "hennes",
			"dess", "", "våra", "era", "deras" };

	// /// possessive reflexive forms
	static String[] possessive_reflexive_en = { "min", "din", "sin", "sin",
			"sin", "", "vår", "er", "sin" };

	static String[] possessive_reflexive_ett = { "mitt", "ditt", "sitt",
			"sitt", "sitt", "", "vårt", "ert", "sitt" };

	static String[] possessive_reflexive_plural = { "mina", "dina", "sina",
			"sina", "sina", "", "våra", "era", "sina" };

	/**
	 * @param person
	 *            the number of the person
	 * @return
	 */
	static Question question_objective(int person) {
		int person_subject = person;
		while (person_subject == person) 
			person_subject = (int) (Math.random() * nominative.length);
		String radix = nominative[person_subject];
		radix = radix + " måsta se " + Question.UNKNOWN + " !";
		radix = LibUtils.firstLetterUpperCase(radix);
		
		String question = radix.replace(Question.UNKNOWN, Question.UNKNOWN
				+ " [" + nominative[person] + "]");
		String answer = radix.replace(Question.UNKNOWN, LibUtils
				.emphasize(objective[person]));
		return new Question("Objective pronouns", question, answer);
	}

	/**
	 * @param person
	 *            the number of the person
	 * @return
	 */
	static Question question_reflexive(int person) {
		String question = nominative[person] + " måste skynda "
				+ Question.UNKNOWN + " nu.";
		question = LibUtils.firstLetterUpperCase(question);
		String answer = question.replace(Question.UNKNOWN, LibUtils
				.emphasize(reflexive[person]));
		return new Question("Reflexive pronouns", question, answer);
	}

	/**
	 * @param person
	 *            the number of the person
	 * @return
	 */
	static Question question_possessive(int person) {
		String radix = "", question = "";
		if (Math.random() > .6) {
			radix = "Det är xxx bok, xxx radergummi och xxx pennor.";
			radix = radix.replaceAll("xxx", Question.UNKNOWN);
			question = "[" + nominative[person] + "] " + radix;
		} else {
			radix = nominative[person] + " är glad därför att xxx bror, "
					+ "xxx barn och xxx föräldrar ringde.";
			radix = radix.replaceAll("xxx", Question.UNKNOWN);
			radix = LibUtils.firstLetterUpperCase(radix);
			question = radix;
		}

		String answer = radix.replaceFirst(Question.UNKNOWN, LibUtils
				.emphasize(possessive_en[person]));
		answer = answer.replaceFirst(Question.UNKNOWN, LibUtils
				.emphasize(possessive_ett[person]));
		answer = answer.replaceFirst(Question.UNKNOWN, LibUtils
				.emphasize(possessive_plural[person]));
		return new Question("Possessive pronouns", question, answer);
	}

	/**
	 * @param person
	 *            the number of the person
	 * @return
	 */
	static Question question_possessive_reflexive(int person) {
		String radix = "", question = "";
		if (Math.random() > .6) {
			radix = nominative[person] + " leker med xxx hund, "
					+ "kammar xxx hår, och dansar med xxx föräldrar.";
			radix = radix.replaceAll("xxx", Question.UNKNOWN);
		} else {
			radix = nominative[person] + " är glad därför att "
					+ nominative[person] + " har xxx dator, xxx äpple "
					+ "och xxx lampor.";
			radix = radix.replaceAll("xxx", Question.UNKNOWN);
		}

		radix = LibUtils.firstLetterUpperCase(radix);
		question = radix;

		String answer = radix.replaceFirst(Question.UNKNOWN, LibUtils
				.emphasize(possessive_reflexive_en[person]));
		answer = answer.replaceFirst(Question.UNKNOWN, LibUtils
				.emphasize(possessive_reflexive_ett[person]));
		answer = answer.replaceFirst(Question.UNKNOWN, LibUtils
				.emphasize(possessive_reflexive_plural[person]));
		return new Question("Poss. refl. pronouns", question, answer);
	}

	/**
	 * @return a random {@link Question} among the above ones
	 */
	public static Question randomQuestion() {
		int type_of_question = (int) (Math.random() * 4);
		int person = (int) (Math.random() * nominative.length);

		if (type_of_question == 0)
			return question_objective(person);
		if (type_of_question == 1)
			return question_reflexive(person);
		if (type_of_question == 2 || type_of_question == 3) {
			while (person == 5)
				person = (int) (Math.random() * nominative.length);
			if (type_of_question == 2)
				return question_possessive(person);
			if (type_of_question == 3)
				return question_possessive_reflexive(person);
		}
		return null;
	}

	public static void main(String[] args) {
		// System.out.println(question_objective((int) (Math.random() * 9)));
		// System.out.println(question_possessive((int) (Math.random() * 9)));
		// System.out
		// .println(question_possessive_reflexive((int) (Math.random() * 9)));

		System.out.println(randomQuestion());
	}
}
