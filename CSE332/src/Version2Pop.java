import java.util.concurrent.*;

public class Version2Pop extends RecursiveAction {
	// Sequential cutoff value
	public static final int SEQUENTIAL_CUTOFF = 2;
	// Array of census data.
	public CensusData pop;
	// Variables that stores the desired population and total population
	public int population;
	public int totalpop;
	// Variables that stores the user inputs.
	public int xleft;
	public int ybottom;
	public int xright;
	public int yup;
	// Size of each block
	public float xblock;
	public float yblock;
	// Calculated x_min and y_min
	public float xmin;
	public float ymin;
	// User inputs
	public int totalx;
	public int totaly;
	// low and high
	public int low;
	public int high;
	
	public Version2Pop(CensusData pop, int xleft, int ybottom, int xright, int yup, 
					   float xblock, float yblock, float xmin, float ymin, int totalx, int totaly,
					   int low, int high) {
		this.pop = pop;
		this.population = 0;
		this.totalpop = 0;
		this.xleft = xleft;
		this.ybottom = ybottom;
		this.xright = xright;
		this.yup = yup;
		this.xblock = xblock;
		this.yblock = yblock;
		this.xmin = xmin;
		this.ymin = ymin;
		this.totalx = totalx;
		this.totaly = totaly;
		this.low = low;
		this.high = high;
	}
	
	public void compute() {
		if (high - low < SEQUENTIAL_CUTOFF) { // Smaller than sequential cutoff, add
			for (int i = low; i < high; i++) {
				totalpop = pop.data[i].population;
				
				int x = (int) (1 + (pop.data[i].longitude - xmin) / xblock);
				int y = (int) (1 + (pop.data[i].latitude - ymin) / yblock);
				
				if (x >= xleft && x <= xright && y >= ybottom && y <= yup
						|| xright == totalx && x == totalx + 1
						|| yup == totaly && y == totaly + 1) {
					population += pop.data[i].population;
				}
			}
		} else { // Not smaller, fork more.
			Version2Pop left = new Version2Pop(pop, xleft, ybottom, xright, yup, xblock, yblock, xmin, ymin,
											   totalx, totaly, low, (low + high) / 2);
			Version2Pop right = new Version2Pop(pop, xleft, ybottom, xright, yup, xblock, yblock, xmin, ymin,
					   totalx, totaly, (low + high) / 2, high);
			
			left.fork();
			right.compute();
			left.join();
			
			this.population = left.population + right.population;
			this.totalpop = left.totalpop + right.totalpop;
		}
	}
}
