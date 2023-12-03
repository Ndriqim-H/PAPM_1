package Assignment1;

import java.util.concurrent.RecursiveAction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class ParallelFilter {
		
    private int[] src;
    private int[] dst;
    private int width;
    private int height;
    private int numThreads;
    
    private final int NRSTEPS = 100;

    public ParallelFilter(int[] src, int[] dst, int w, int h) {
        this.src = src;
        this.dst = dst;
        width = w;
        height = h;
    }
    
    private class ImageBlurTask extends RecursiveAction {
    	private int start;
        private int end;
        
        private int size;
        protected static int sThreshold = 3000;
        
        
        public ImageBlurTask(int start, int end) {
            this.start = start;
            this.end = end;
            this.size = end - start;
        }

        @Override
        protected void compute() {
    			if(size < sThreshold) {
    				computeDirectly();
    				return;
    			}
    			
    			int split = size / numThreads;
    			int remainder = size % numThreads;

    			List<ImageBlurTask> tasks = new ArrayList<>();

    			int currentStart = start;

    			for (int i = 0; i < numThreads; i++) {
    			    int currentEnd = currentStart + split + ((i < remainder) ? 1 : 0);
    			    currentEnd = Math.min(currentEnd, end - 1); // Ensure that currentEnd doesn't exceed the overall end
    			    tasks.add(new ImageBlurTask(currentStart, currentEnd));
    			    currentStart = currentEnd;
    			}
                
    			invokeAll(tasks);
    			return;
        }
        
        protected synchronized void computeDirectly() {
        	int endLine = height == end ? end - 1 : end; 
        	int index, pixel;
        	for (int i = start; i < endLine; i++) {
                for (int j = 1; j < width - 1; j++) {
                	float rt = 0, gt = 0, bt = 0;
					for (int k = i - 1; k <= i + 1; k++) {
						index = k * width + j - 1;
						pixel = src[index];
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
					index = i * width + j;
					int dpixel = (0xff000000) | (((int) rt / 9) << 16) | (((int) gt / 9) << 8) | (((int) bt / 9));
					dst[index] = dpixel;
                }
            }
	        	
        }
    }

    public void applyParallel(int numThreads) {
    	this.numThreads = numThreads;
        ForkJoinPool forkJoinPool = new ForkJoinPool(numThreads);
        for (int steps = 0; steps < NRSTEPS; steps++) {
	        forkJoinPool.invoke(new ImageBlurTask(1, height));
        	// swap references
	        int[] help; help = src; src = dst; dst = help;
        } 
    }
}
