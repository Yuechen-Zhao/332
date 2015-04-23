import java.util.concurrent.*;

public class Version4AddGrid extends RecursiveTask<int[][]> {
	public static final int SEQUENTIAL_CUTOFF = 2;
	// Initial two grids to merge, and the result is stored in grid1
	public int[][] grid1;
	public int[][] grid2;
	// four bounds
	public int xlow;
	public int xhigh;
	public int ylow;
	public int yhigh;
	
	public Version4AddGrid(int[][] grid1, int[][] grid2, int xlow, int xhigh, int ylow, int yhigh) {
		this.grid1 = grid1;
		this.grid2 = grid2;
		this.xlow = xlow;
		this.xhigh = xhigh;
		this.ylow = ylow;
		this.yhigh = yhigh;
	}
	
	public int[][] compute() {
		if (xhigh - xlow < SEQUENTIAL_CUTOFF) { // Smaller than sequential cutoff, add elements together
												// and stores in grid1.
			for (int column = xlow; column < xhigh; column++) {
				for (int i = ylow; i < yhigh; i++) {
					grid1[column][i] += grid2[column][i];
				}
			}
			
// NOTE: THESE COMMENT-OUTS ALSO WORK, BUT ARE EXTREMELY SLOW! THEY DIVIDE EACH BLOCK INTO 4 SUB-BLOCKS
//		 INSTEAD OF 2 (LEFT AND RIGHT). REMEMBER TO DELETE THEM BEFORE TURNING IN!!!!!!!!!!!
//		} else if (yhigh - ylow == 1) {
//			for (int j = xlow; j < xhigh; j++) {
//				grid1[j][ylow] += grid2[j][ylow];
//			}
			
		} else { // Not smaller, fork more into two sub-blocks (left and right).
			
//			Version4AddGrid leftbottom = new Version4AddGrid(grid1, grid2, xlow, (xlow + xhigh) / 2, 
//					ylow, (ylow + yhigh) / 2);
//			Version4AddGrid leftup = new Version4AddGrid(grid1, grid2, xlow, (xlow + xhigh) / 2, 
//					(ylow + yhigh) / 2, yhigh);
//			Version4AddGrid rightbottom = new Version4AddGrid(grid1, grid2, (xlow + xhigh) / 2, xhigh,
//					ylow, (ylow + yhigh) / 2);
//			Version4AddGrid rightup = new Version4AddGrid(grid1, grid2, (xlow + xhigh) / 2, xhigh,
//					(ylow + yhigh) / 2, yhigh);
//			
//			leftbottom.fork();
//			leftup.fork();
//			rightbottom.compute();
//			rightup.compute();
//			leftbottom.join();
//			leftup.join();
			
			Version4AddGrid left = new Version4AddGrid(grid1, grid2, xlow, (xlow + xhigh) / 2, 
					ylow, yhigh);
			Version4AddGrid right = new Version4AddGrid(grid1, grid2, (xlow + xhigh) / 2, xhigh,
					ylow, yhigh);
			
			left.fork();
			right.compute();
			left.join();
		}
		
		return grid1;
	}
}
