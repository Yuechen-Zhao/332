import java.util.concurrent.*;

public class Version2MinMax extends RecursiveAction {
	
	// Stores the sequential cutoff value.
	public static final int SEQUENTIAL_CUTOFF = 2;
	// Array of census data
	public CensusData pop;
	// x_min, x_max, y_min, y_max
	public float xmin;
	public float xmax;
	public float ymin;
	public float ymax;
	// low and high
	public int low;
	public int high;
	
	// Constructor
	public Version2MinMax(CensusData pop, int low, int high) {
		this.pop = pop;
		this.xmin = Float.POSITIVE_INFINITY;
		this.xmax = Float.NEGATIVE_INFINITY;
		this.ymin = Float.POSITIVE_INFINITY;
		this.ymax = Float.NEGATIVE_INFINITY;
		this.low = low;
		this.high = high;
	}
	
	// Override
	public void compute() {
		if (high - low < SEQUENTIAL_CUTOFF) { // Smaller than sequential cutoff, iterate through
			for (int i = low; i < high; i++) {
				this.xmin = Math.min(xmin, pop.data[i].longitude);
				this.xmax = Math.max(xmax, pop.data[i].longitude);
				this.ymin = Math.min(ymin, pop.data[i].latitude);
				this.ymax = Math.max(ymax, pop.data[i].latitude);
			}
		} else { // Larger than sequential cutoff, fork more.
			Version2MinMax left = new Version2MinMax(pop, low, (low + high) / 2);
			Version2MinMax right = new Version2MinMax(pop, (low + high) / 2, high);
			left.fork();
			right.compute();
			left.join();
			
			xmin = Math.min(left.xmin, right.xmin);
			xmax = Math.max(left.xmax, right.xmax);
			ymin = Math.min(left.ymin, right.ymin);
			ymax = Math.max(left.ymax, right.ymax);
		}
	}
}