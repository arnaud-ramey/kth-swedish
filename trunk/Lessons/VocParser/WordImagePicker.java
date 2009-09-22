package Lessons.VocParser;

import Asker.Question;

public class WordImagePicker {
	/**
	 * @return a random {@link Question} coming from the voc lessons,
	 * with a picture
	 */
	public static Question randomQuestion() {
		WordPicker wp = WordPicker.defaultWordPicker();
		wp.allowAllLessons();
		Word w;
		
		while (true) {
			w = wp.getRandomWord();
			if (w.containsPicture())
				break;
		}
		
		Question q = new Question("VOC [IMAGES]", w.get0(), w.toString_onlyWords());
		q.setImage_question(w.getPictureFilename());
		return q;
	}
	
	public static void main(String[] args) {
		System.out.println(randomQuestion());
	}
}
