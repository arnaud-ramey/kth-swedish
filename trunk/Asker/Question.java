package Asker;

import java.awt.image.BufferedImage;

import Kvtml.IO.Image_IO;

public class Question {
	public static String UNKNOWN = "___";
	
	public String lesson = "";

	public String question = "";

	public String answer = "";

	public boolean is_image = false;
	
	public Object userObject;

	/**
	 * constructor
	 */
	public Question() {
	}

	/**
	 * constructor
	 * 
	 * @param lesson
	 *            the name of the lesson
	 * @param question
	 *            the text of the question
	 * @param answer
	 *            the text of the answer
	 */
	public Question(String lesson, String question, String answer) {
		this.lesson = lesson;
		this.question = question;
		this.answer = answer;
	}

	/**
	 * the {@link String} version
	 */
	public String toString() {
		String rep = lesson.toUpperCase() + ":\n";
		rep += " | Q:'";
		if (is_image)
			rep += "FILE:" + question;
		else
			rep += question;
		rep += "'\n";
		rep += " | A:'" + answer + "'";
		return rep;
	}

	@Override
	/**
	 * a comparison test
	 */
	public boolean equals(Object arg0) {
		Question q = (Question) arg0;
		if (!q.lesson.equalsIgnoreCase(lesson))
			return false;
		if (q.is_image != is_image)
			return false;
		if (!q.question.equalsIgnoreCase(question))
			return false;
		// if (!q.answer.equalsIgnoreCase(answer))
		// return false;
//		System.out.println(this + " == " + q);
		return true;
	}

	/**
	 * @param image_filename
	 *            the filename of the image
	 */
	public void setImage_question(String image_filename) {
		question = image_filename;
		is_image = true;
	}

	/**
	 * @return the image associated
	 */
	public BufferedImage getImage() {
		return getImage(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	public BufferedImage getImage(int maxW, int maxH) {
		if (!is_image)
		return null;
		BufferedImage i = Image_IO.getImageFromURL(question);
		if (Image_IO.doesImageNeedResizing(i, maxW, maxH))
			return Image_IO.resizeImage(i, maxW, maxH, true);
		return i;
	}
}
