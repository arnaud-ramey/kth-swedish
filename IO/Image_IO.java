package IO;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

public class Image_IO {
	/**
	 * test if an image is bigger than the limits
	 * 
	 * @param i
	 *            the {@link RenderedImage}
	 * @param maxW
	 *            the max width
	 * @param maxH
	 *            the max height
	 * @return true if it need to be resized
	 */
	public static boolean doesImageNeedResizing(RenderedImage i, int maxW,
			int maxH) {
		if (i.getWidth() > maxW)
			return true;
		if (i.getHeight() > maxH)
			return true;
		return false;
	}

	/**
	 * resize an image
	 * 
	 * @param imageBig
	 *            the {@link BufferedImage} to be resized
	 * @param maxW
	 *            the max width
	 * @param maxH
	 *            the max height
	 * @return the resiezd {@link BufferedImage}
	 */
	public static BufferedImage resizeImage(BufferedImage imageBig, int maxW,
			int maxH, boolean nice) {
		int w = imageBig.getWidth(), h = imageBig.getHeight();
		double ratioX = 1.0f * maxW / w;
		double ratioY = 1.0f * maxH / h;
		// System.out.println("rX:" + ratioX + " - rY:" + ratioY);

		double ratio = Math.min(ratioX, ratioY);
		Object type = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
		if (!nice)
			type = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

		BufferedImageOp op = new AffineTransformOp(AffineTransform
				.getScaleInstance(ratio, ratio), new RenderingHints(
				RenderingHints.KEY_INTERPOLATION, type));
		// image = (BufferedImage) imageBig.getScaledInstance(
		// (int) (w * ratio), (int) (h * ratio), Image.SCALE_FAST);
		BufferedImage image = op.filter(imageBig, null);
		return image;
	}

	/**
	 * save an image in a file
	 * 
	 * @param i
	 *            the file
	 * @param filename
	 *            the filename
	 */
	public static void saveImage(BufferedImage i, String filename) {
		/* save the image */
		File output_file = new File(filename);
		try {
			if (filename.endsWith("png"))
				ImageIO.write(i, "png", output_file);

			if (filename.endsWith("jpg")) {
				// Encode as a JPEG
				// FileOutputStream fos = new FileOutputStream(filename);
				// JPEGImageEncoder jpeg = JPEGCodec.createJPEGEncoder(fos);
				// jpeg.encode(i);
				// fos.close();
				ImageIO.write(i, "jpg", output_file);
			}
		} catch (IOException e) {
			System.out.println("Impossible to write the file.");
		}
	}

	/**
	 * 
	 * get an image from an url
	 * 
	 * @param filename_or_url
	 *            the url of the image (http or file)
	 * @param maxW
	 *            max width
	 * @param maxW
	 *            max height
	 * @return the {@link BufferedImage}
	 */
	public static BufferedImage getImageFromURL(String filename_or_url,
			int maxW, int maxH, boolean nice) {
		BufferedImage imageBig = getImageFromURL(filename_or_url);
		if (doesImageNeedResizing(imageBig, maxW, maxH)) {
			return resizeImage(imageBig, maxW, maxH, nice);
		} else
			return imageBig;
	}

	/**
	 * get an image from an url
	 * 
	 * @param filename_or_url
	 *            the url of the image (http or file)
	 * @return the {@link BufferedImage}
	 */
	public static BufferedImage getImageFromURL(String filename_or_url) {
		URL u = null;

		// http
		if (filename_or_url.startsWith("http://")) {
			try {
				u = new URL(filename_or_url);
			} catch (MalformedURLException e) {
				System.out.println("The URL '" + u + "' is balky.");
			}
		}
		// file
		else {
			if (!filename_or_url.startsWith("/"))
				filename_or_url = "/" + filename_or_url;
			u = Image_IO.class.getResource(filename_or_url);
		}

		// System.out.println("url:" + filename_or_url);

		BufferedImage i = new BufferedImage(1, 1, 1);
		try {
			i = ImageIO.read(u);
		} catch (Exception e) {
			System.out.println("!!! Cannot read the image " + filename_or_url + " !!!");
		}
		return i;
	}
}
