import java.util.concurrent.*;

public class Version4Grid extends RecursiveTask<int[][]> {
	// Sequential cutoff value
	public static final int SEQUENTIAL_CUTOFF = 2;
	
	public int[][] grid;
	public CensusData pop;
	public int low;
	public int high;
	// x_min and y_min
	public float xmin;
	public float ymin;
	// how large is one block
	public float xblock;
	public float yblock;
	// user input totalx totaly
	public int totalx;
	public int totaly;
	
	public Version4Grid(CensusData pop, int low, int high, float xmin, float ymin,
						float xblock, float yblock, int totalx, int totaly) {
		this.grid = new int[totalx][totaly];
		this.pop = pop;
		this.low = low;
		this.high = high;
		this.xmin = xmin;
		this.ymin = ymin;
		this.xblock = xblock;
		this.yblock = yblock;
		this.totalx = totalx;
		this.totaly = totaly;
	}
	
	public int[][] compute() {
		if (high - low < SEQUENTIAL_CUTOFF) { // Smaller than sequential cutoff, add to new grid
			for (int i = low; i < high; i++) {
				int x = (int) (1 + (pop.data[i].longitude - xmin) / xblock);
				int y = (int) (1 + (pop.data[i].latitude - ymin) / yblock);
				
				// add the value in the grid
				if (x-1 != totalx && y-1 != totaly) {
					grid[x-1][y-1] += pop.data[i].population;
				} else if (x-1 != totalx) {
					grid[x-1][totaly-1] += pop.data[i].population;
				} else if (y-1 != totaly) {
					grid[totalx-1][y-1] += pop.data[i].population;
				} else {
					grid[totalx-1][totalx-1] += pop.data[i].population;
				}
			}
		} else { // Not smaller, fork more
			Version4Grid left = new Version4Grid(pop, low, (low+high) / 2,
					xmin, ymin, xblock, yblock, totalx, totaly);
			Version4Grid right = new Version4Grid(pop, (low+high) / 2, high,
					xmin, ymin, xblock, yblock, totalx, totaly);
			left.fork();
			int[][] rightsubgrid = right.compute();
			int[][] leftsubgrid = left.join();
			
			// Create another Fork Join to sum up (merge) the two subgrids
			Version4AddGrid addgridthread = new Version4AddGrid(leftsubgrid, rightsubgrid, 0, totalx, 0, totaly);
			grid = addgridthread.compute();
			
		}
		
		return grid;
	}
	
}
