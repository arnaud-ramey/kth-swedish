package Maintaining;

import Lessons.VocParser.ListOfWords;
import Lessons.VocParser.Word;

public class DoublesFinder {
	/**
	 * display the list of words which are in double
	 * @param list
	 */
	public static void findDouble(ListOfWords list) {
		for (int i = 0; i < list.nbWords(); i++) {
			for (int j = 0; j < list.nbWords(); j++) {
				if (i == j)
					continue;
				boolean isInclusion = false;
				Word w1 = list.getWord(i);
				Word w2 = list.getWord(j);
				// if (w1.get0().contains(w2.get0()))
				// isInclusion = true;
				int maxLang = w1.getNumberOfLanguages();
				maxLang = Math.min(w2.getNumberOfLanguages(), maxLang);
				for (int lg_idx = 0 ;  lg_idx < maxLang ; ++lg_idx) {
				if (w1.getForeignWord(lg_idx).equalsIgnoreCase(w2.getForeignWord(lg_idx)))
					isInclusion = true;
				}
				
				if (isInclusion) {
					System.out.println(" * " + w1.toString_onlyWords() + "\n * " + w2.toString_onlyWords() );
					System.out.println();
				}
			}
			
		}
	}
	
	public static void main(String[] args) {
		findDouble(ListOfWords.defaultListOfWords());
	}

}
