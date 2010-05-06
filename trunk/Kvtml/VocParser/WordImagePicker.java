package Kvtml.VocParser;

import Asker.Question;

public class WordImagePicker {
	/**
	 * @return a random {@link Question} coming from the voc lessons, with a
	 *         picture
	 */
	public static Question randomQuestion(WordPicker wp) {
		Word w;

		while (true) {
			w = wp.getSelection().getRandomWord();
			if (w.containsPicture())
				break;
		}

		Question q = new Question("VOC [IMAGES]", w.get0(), w
				.toString_onlyWords());
		q.setImage_question(w.getPictureFilename());
		return q;
	}

	public static void main(String[] args) {
		System.out.println(randomQuestion(WordPicker.defaultWordPicker(true)));
	}
}
