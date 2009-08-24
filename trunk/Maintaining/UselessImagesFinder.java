package Maintaining;

import java.io.File;
import java.util.LinkedList;

import ImageGetter.ImageGetter;
import Lessons.VocParser.ListOfWords;
import Lessons.VocParser.Word;

public class UselessImagesFinder {

	/**
	 * @param path
	 *            the path to the files
	 * @param extension
	 *            the extension of the files
	 * @return the {@link LinkedList} of the filenames, in {@link String}
	 */
	static LinkedList<String> files(String path, String extension) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		LinkedList<String> rep = new LinkedList<String>();

		// add all the filenames if these are files
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String filename = listOfFiles[i].getName();
				if (filename.endsWith("." + extension))
					rep.add(path + filename);
			}
		}
		return rep;
	}

	/**
	 * find the images which are not a part of the {@link ListOfWords}
	 * 
	 * @param list
	 *            the {@link ListOfWords}
	 * @param path
	 *            the path to the images
	 */
	static void findUselessImages(ListOfWords list, String path) {
		LinkedList<String> filenames = files(path, "jpg");
		// for (String s : filenames)
		// System.out.println(s);
		System.out.println(" * Total number of images :" + filenames.size());

		// remove all the used filenames
		for (Word w : list.getWords()) {
			if (!w.hasPicture())
				continue;
			String pic_filename = w.getPictureFilename();
			filenames.remove(pic_filename);
			// filenames.remove(path + pic_filename);
		}

		// display the ones left
		System.out.println(" * Number of useless images : " + filenames.size());
		for (String s : filenames) {
			System.out.println(s);
		}
		
		// compute the instruction
		System.out.println("\n * Instruction :");
		String instr = "rm ";
		for (String s : filenames)
			instr = instr + s + " ";
		System.out.println(instr);
	}
	
	static void findMissingPictures(ListOfWords list, String path) {
		LinkedList<String> filenames = files(path, "jpg");
		System.out.println(" * Total number of images :" + filenames.size());

		// remove all the used filenames
		for (Word w : list.getWords()) {
			if (!w.hasPicture())
				continue;
			String pic_filename = w.getPictureFilename();
			boolean contains = filenames.contains(pic_filename);
			if (!contains) {
				System.out.println("Missing :\n - " + pic_filename);
				System.out.println(" - Word :" + w.toString_onlyWords());
			}
		}
	}

	public static void main(String[] args) {
		findUselessImages(ListOfWords.defaultListOfWords(),
				ImageGetter.DEFAULT_DIR);
	}

}
