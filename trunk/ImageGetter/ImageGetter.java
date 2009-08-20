package ImageGetter;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

import IO.IO;
import IO.Image_IO;

public class ImageGetter {
	static String DEFAULT_DIR = "Images/";

	public static int MAX_WIDTH = 300;

	public static int MAX_HEIGHT = 300;

	private String search = "";

	private LinkedList<String> results;

	private BufferedImage image;

	private int chosen_index = 0;

	/**
	 * set the new search word
	 * 
	 * @param search
	 *            a {@link String}
	 */
	public void setSearch(String search) {
		this.search = search;
		refreshResults();
	}
	
	private static String removeIfStarts(String string, String prefix) {
		if (string.startsWith(prefix)) 
			string = string.replaceFirst(prefix, "");
		return string;
	}

	/**
	 * remove bad characters from the search
	 * @return the new search
	 */
	private String cleanSearch() {
		String search_clean = search;
		search_clean = removeIfStarts(search_clean, "the ");
		search_clean = removeIfStarts(search_clean, "a ");
		search_clean = search_clean.replaceAll(", -s", "");
		search_clean = search_clean.replaceAll("(s)", "");
		search_clean = search_clean.replace(' ', '+');
		search_clean = search_clean.replace("/", "");
		return search_clean;
	}

	/**
	 * get the results from google image
	 */
	private void refreshResults() {
		String path = "http://images.google.com/images?hl=en&q=" + cleanSearch();
		String line = IO.getWebPageContent(path);
		results = IO.extractTagsFromLine(line, IMAGE_BEGIN_TAG, IMAGE_END_TAG);

		/* reset the index */
		goToImage(0);
	}

	private void goToImage(int index) {
		chosen_index = minMax(index, 0, results.size() - 1);
		downloadImage(chosen_index);
	}

	public void goToNextImage() {
		goToImage(chosen_index + 1);
	}

	public void goToPreviousImage() {
		goToImage(chosen_index - 1);
	}

	/**
	 * get a rescaled version of the image
	 * 
	 * @param index_in_results
	 */
	private void downloadImage(int index_in_results) {
		/* download the image and get its dimension */
		BufferedImage imageBig = Image_IO.getImageFromURL(results.get(index_in_results));
		if (imageBig == null)
			return;
		/* resize if needed */
		if (Image_IO.doesImageNeedResizing(imageBig, MAX_WIDTH, MAX_HEIGHT))
			image = Image_IO.resizeImage(imageBig, MAX_WIDTH, MAX_HEIGHT, false);
		else image = imageBig;
	}
	
	/**
	 * @param image the image to set
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	/**
	 * @return the image
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * save the current image
	 */
	public void saveImage() {
		Image_IO.saveImage(image, clean_filename());
	}

	/**
	 * @return a clean version of the image filename
	 */
	public String clean_filename() {
		String filename_clean = search;
		// clean
		filename_clean = filename_clean.replace(',', '_');
		filename_clean = filename_clean.replace('(', '_');
		filename_clean = filename_clean.replace(')', '_');
		filename_clean = filename_clean.replace(' ', '_');
		filename_clean = filename_clean.replace("/", "");
		// add prefix and suffix
		filename_clean = DEFAULT_DIR + filename_clean + ".jpg";
		return filename_clean;
	}

	public static String IMAGE_BEGIN_TAG = "imgurl=";

	public static String IMAGE_END_TAG = "&";

	

	/**
	 * @param v
	 *            the actual value
	 * @param min
	 *            the min value
	 * @param max
	 *            the max value
	 * @return v if it between the bounds, the bound otherwise
	 */
	public static int minMax(int v, int min, int max) {
		if (v > max)
			return max;
		if (v < min)
			return min;
		return v;
	}

	public static void main(String[] args) {
		ImageGetter ig = new ImageGetter();
		ig.setSearch("curtains");
		ig.goToNextImage();
		ig.saveImage();

		// String[] lines = getPageContent("http://www.google.es");
		// for (String s : lines) System.out.println(s);
	}

}
