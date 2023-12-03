package Assignment1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ImageFilterParallel{

    private int[] src;
    private int[] dst;
    private int width;
    private int height;

    private final int NRSTEPS = 100;

    private ExecutorService executor;

    public ImageFilterParallel(int[] src, int[] dst, int w, int h) {
        this.src = src;
        this.dst = dst;
        width = w;
        height = h;
    }

    public void apply(int threadNumber) {
        executor = Executors.newFixedThreadPool(threadNumber);

        for (int steps = 0; steps < NRSTEPS; steps++) {
            for (int i = 1; i < height - 1; i++) {
                final int row = i;
                executor.submit(new Runnable() {
					public void run() {
						processRow(row);
					}
				});
            }
            // Ensure all threads are completed before combining results
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Combine results
            combineResults();
        }
    }

    private void processRow(int i) {
        int[] localDst = new int[width];
        for (int j = 1; j < width - 1; j++) {
            float rt = 0, gt = 0, bt = 0;
            for (int k = i - 1; k <= i + 1; k++) {
                int index = k * width + j - 1;
                int pixel = src[index];
                rt += (float) ((pixel & 0x00ff0000) >> 16);
                gt += (float) ((pixel & 0x0000ff00) >> 8);
                bt += (float) ((pixel & 0x000000ff));

                index = k * width + j;
                pixel = src[index];
                rt += (float) ((pixel & 0x00ff0000) >> 16);
                gt += (float) ((pixel & 0x0000ff00) >> 8);
                bt += (float) ((pixel & 0x000000ff));

                index = k * width + j + 1;
                pixel = src[index];
                rt += (float) ((pixel & 0x00ff0000) >> 16);
                gt += (float) ((pixel & 0x0000ff00) >> 8);
                bt += (float) ((pixel & 0x000000ff));
            }
            // Re-assemble destination pixel.
            //int index = i * width + j;
            int dpixel = (0xff000000) | (((int) rt / 9) << 16) | (((int) gt / 9) << 8) | (((int) bt / 9));
            localDst[j] = dpixel;
        }
        // Store the local results in the dst array
        System.arraycopy(localDst, 1, dst, i * width + 1, width - 2);
    }

    private void combineResults() {
        // No need to copy the entire array, as each thread only modifies a specific region
        // You might need to adjust the start and end indices based on your specific requirements
        System.arraycopy(dst, 0, src, 0, src.length);
    }
}
