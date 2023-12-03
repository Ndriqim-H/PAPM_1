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
		
		
		int[] src0 = image.getRGB(0, 0, w, h, null, 0, w);
		int[] dst0 = new int[src0.length];

		System.out.println("Starting sequential image filter.");

		long startTime = System.currentTimeMillis();
		ImageFilter filter0 = new ImageFilter(src0, dst0, w, h);
		filter0.apply();
		long endTime = System.currentTimeMillis();

		long tSequential = endTime - startTime;
		System.out.println("Sequential image filter took " + tSequential + " milliseconds.");
		
		BufferedImage dstImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		dstImage.setRGB(0, 0, w, h, dst0, 0, w);

		String dstName = "SequentialFiltered" + srcFileName;
		File dstFile = new File(dstName);
		ImageIO.write(dstImage, "jpg", dstFile);
		
		System.out.println("Output image: " + dstName);
		
		
        // Get the runtime object
        Runtime runtime = Runtime.getRuntime();

        // Get the number of available processors
        int processors = runtime.availableProcessors();

        System.out.println("Number of available processors: " + processors);
		
		boolean notDone = true;
		int i = 0;
		//for (int i = 0; i < 5; i++) {
        while (notDone) {
			
			System.out.println();
			System.out.println();
			
			int threadCount = (int) Math.pow(2, i);
			String thr = i == 0 ? " thread." : " threads.";
			if(threadCount > processors)
			{
				notDone = false;
				threadCount = processors;
			}
			
			i++;
			
			
			int[] src = image.getRGB(0, 0, w, h, null, 0, w);
			int[] dst = new int[src.length];

			System.out.println("Starting parallel image filter with " + threadCount +thr);

			startTime = System.currentTimeMillis();
			ParallelFilter filter1 = new ParallelFilter(src, dst, w, h);
			filter1.applyParallel(threadCount);
			endTime = System.currentTimeMillis();

			long tParallel = endTime - startTime;
			System.out.println("Sequential image filter with " + threadCount +" threads took " + tParallel + " milliseconds.");
			
			
			BufferedImage dstImage1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			dstImage1.setRGB(0, 0, w, h, dst, 0, w);

			String dstName1 = "ParallelFiltered" + "_" + threadCount + srcFileName ;
			File dstFile1 = new File(dstName1);
			ImageIO.write(dstImage1, "jpg", dstFile1);
			
			System.out.println("Output image: " + dstName1);
			
			
			boolean validation = areImagesIdentical(dstImage, dstImage1);
			
			if(validation) {
				System.out.println("Output image verified successfully!");
			}
			else {
				System.out.println("Output image failed verification!");
			}
			
			double speedUp = (double) tSequential / tParallel;
			String isOk = speedUp > threadCount * 0.7 ? " Ok " : " Not ok ";
			String comp = (speedUp > threadCount * 0.7 ? "(>= " : "(< " ) + threadCount * 0.7 + ")";
			
			System.out.println("Speedup: " + String.format("%.7f", speedUp) + isOk + comp);
			
		}
	}

	private static boolean areImagesIdentical(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false; // Images have different dimensions
        }

        for (int y = 0; y < img1.getHeight(); y++) {
            for (int x = 0; x < img1.getWidth(); x++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false; // Pixels are different
                }
            }
        }

        return true; // All pixels are identical
    }

}


