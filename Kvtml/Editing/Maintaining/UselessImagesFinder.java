package Kvtml.Editing.Maintaining;

import java.io.File;
import java.util.LinkedList;

import Kvtml.Editing.ImageGetter.ImageGetter;
import Kvtml.VocParser.ListOfWords;
import Kvtml.VocParser.Word;

public class UselessImagesFinder {

	/**
	 * @param path
	 *            the path to the files
	 * @param extension
	 *            the extension of the files
	 * @return the {@link LinkedList} of the filenames, in {@link String}
	 */
	public static LinkedList<String> listFiles(String path, String extension) {
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
	public static void findUselessImages(ListOfWords list, String path) {
		LinkedList<String> filenames = listFiles(path, "jpg");
		// for (String s : filenames)
		// System.out.println(s);
		System.out.println(" * Total number of images :" + filenames.size());

		// remove all the used filenames
		for (Word w : list.getWords()) {
			if (!w.containsPicture())
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
		if (filenames.size() > 0) {
			System.out.println("\n * Instruction :");
			String instr = "rm ";
			for (String s : filenames)
				instr = instr + s + " ";
			System.out.println(instr);
		}
	}

	/**
	 * display the {@link Word}s with a picture which is not available on hard
	 * disk
	 * 
	 * @param list
	 *            the {@link ListOfWords}
	 * @param path
	 *            the path to the pictures
	 */
	public static void findMissingPictures(ListOfWords list, String path) {
		LinkedList<String> filenames = listFiles(path, "jpg");
		System.out.println(" * Total number of images :" + filenames.size());

		LinkedList<Word> missing = new LinkedList<Word>();

		// remove all the used filenames
		for (Word w : list.getWords()) {
			if (!w.containsPicture())
				continue;
			String pic_filename = w.getPictureFilename();
			boolean contains = filenames.contains(pic_filename);
			if (!contains)
				missing.add(w);
		}

		System.out.println(" * Number of words with a missing pic : "
				+ missing.size());

		if (missing.size() > 0) {
			for (Word w : missing) {
				System.out.println("Missing :\n - " + w.getPictureFilename());
				System.out.println(" - Word :" + w.toString_onlyWords());
			}
		}
	}

	/**
	 * display the {@link Word}s with a picture which is not available on hard
	 * disk
	 * 
	 * @param list
	 *            the {@link ListOfWords}
	 * @param path
	 *            the path to the pictures
	 */
	public static void checkPictures(ListOfWords list, String path) {
		System.out.println("USELESS PICS");
		findUselessImages(list, path);
		System.out.println();
		System.out.println("MISSING PICS");
		findMissingPictures(list, path);
	}

	public static void main(String[] args) {
		checkPictures(ListOfWords.defaultListOfWords(), ImageGetter.DEFAULT_DIR);

		// ListOfWords w = new ListOfWords();
		// w.readFile("/test.kvtml");
		// checkPictures(w, ImageGetter.DEFAULT_DIR);
	}

}
