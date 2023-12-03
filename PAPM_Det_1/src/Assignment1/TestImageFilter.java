package Assignment1;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.IIOException;

import javax.imageio.ImageIO;

public class TestImageFilter {

	public static void main(String[] args) throws Exception {
		
		BufferedImage image = null;
		String srcFileName = null;
		
		try {
			srcFileName = args[0];
			File srcFile = new File(srcFileName);
			image = ImageIO.read(srcFile);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Usage: java TestAll <image-file>");
			System.exit(1);
		}
		catch (IIOException e) {
			System.out.println("Error reading image file " + srcFileName + " !");
			System.exit(1);
		}

		System.out.println("Source image: " + srcFileName);

		int w = image.getWidth();
		int h = image.getHeight();
		System.out.println("Image size is " + w + "x" + h);
		System.out.println();
	
		int[] src = image.getRGB(0, 0, w, h, null, 0, w);
		int[] dst = new int[src.length];

		System.out.println("Starting sequential image filter.");

		long startTime = System.currentTimeMillis();
		ParallelFilter filter0 = new ParallelFilter(src, dst, w, h);
		filter0.applyParallel(16);
		long endTime = System.currentTimeMillis();

		long tSequential = endTime - startTime;
		System.out.println("Sequential image filter took " + tSequential + " milliseconds.");
		
		BufferedImage dstImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		dstImage.setRGB(0, 0, w, h, dst, 0, w);

		String dstName = "ParallelFiltered40" + srcFileName;
		File dstFile = new File(dstName);
		ImageIO.write(dstImage, "jpg", dstFile);
		
		System.out.println("Output image: " + dstName);	
//		System.out.println("Starting parallel image filter.");
//		
//		src = image.getRGB(0, 0, w, h, null, 0, w);
//		dst = new int[src.length];
//		int threadCount = 4;
//		startTime = System.currentTimeMillis();
//		ParallelFilter filter1 = new ParallelFilter(src, dst, w, h);
//		filter1.applyParallel(threadCount);
//		endTime = System.currentTimeMillis();
//
//		long tParallel = endTime - startTime;
//		System.out.println("Parallel image filter with " + threadCount + " threads took " + tParallel + " milliseconds.");
//
//		BufferedImage dstImage1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//		dstImage.setRGB(0, 0, w, h, dst, 0, w);
//
//		String dstName1 = "ParallelFiltered3" + srcFileName;
//		File dstFile1 = new File(dstName1);
//		ImageIO.write(dstImage1, "jpg", dstFile1);
//
//		System.out.println("Output image: " + dstName1);	
	}
}
