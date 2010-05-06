package Asker.Visual;

import java.io.IOException;

import Asker.Question;
import Asker.QuestionQueue;
import Kvtml.IO.IO;

public class ConsoleAsker {
	Question currentQuestion;

	QuestionQueue queue = new QuestionQueue();

	static int chooseLesson() {
		System.out.println("Choose your lesson :");
		for (int i = 0; i < QuestionQueue.possible_lessons.length; i++)
			System.out.println(QuestionQueue.possible_lessons[i]);
		System.out.print("Choice ? ");
		int choice = IO.readInt();
		return choice;
	}

	/**
	 * ctor
	 */
	public ConsoleAsker() {
		System.out.println("*** Starting interrogation... ***");
		queue.setLesson(chooseLesson());
		changeQuestion();
		loop();
	}

	/**
	 * load a new question
	 */
	public void changeQuestion() {
		queue.declareQuestion_known();
		currentQuestion = queue.getQuestion();
	}

	/**
	 * the main loop
	 */
	void loop() {
		while (true) {
			System.out.print("-> " + currentQuestion.lesson.toUpperCase()
					+ " : \"" + currentQuestion.question + "\" ? ");
			// wait for keyboard input
			try {
				int c = 0;
				while (c != 10)
					c = System.in.read();
			} catch (IOException e) {
				System.out.println(e.toString());
			}

			// display answer
			System.out.println(currentQuestion.answer);

			changeQuestion();
		}
	}

	public static void main(String[] args) {
		new ConsoleAsker();
	}
}
