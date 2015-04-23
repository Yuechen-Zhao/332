import java.util.concurrent.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class PopulationQuery {
	// next four constants are relevant to parsing
	public static final int TOKENS_PER_LINE  = 7;
	public static final int POPULATION_INDEX = 4; // zero-based indices
	public static final int LATITUDE_INDEX   = 5;
	public static final int LONGITUDE_INDEX  = 6;
	
	// parse the input file into a large array held in a CensusData object
	public static CensusData parse(String filename) {
		CensusData result = new CensusData();
		
        try {
            BufferedReader fileIn = new BufferedReader(new FileReader(filename));
            
            // Skip the first line of the file
            // After that each line has 7 comma-separated numbers (see constants above)
            // We want to skip the first 4, the 5th is the population (an int)
            // and the 6th and 7th are latitude and longitude (floats)
            // If the population is 0, then the line has latitude and longitude of +.,-.
            // which cannot be parsed as floats, so that's a special case
            //   (we could fix this, but noisy data is a fact of life, more fun
            //    to process the real data as provided by the government)
            
            String oneLine = fileIn.readLine(); // skip the first line

            // read each subsequent line and add relevant data to a big array
            while ((oneLine = fileIn.readLine()) != null) {
                String[] tokens = oneLine.split(",");
                if(tokens.length != TOKENS_PER_LINE)
                	throw new NumberFormatException();
                int population = Integer.parseInt(tokens[POPULATION_INDEX]);
                if(population != 0)
                	result.add(population,
                			   Float.parseFloat(tokens[LATITUDE_INDEX]),
                		       Float.parseFloat(tokens[LONGITUDE_INDEX]));
            }

            fileIn.close();
        } catch(IOException ioe) {
            System.err.println("Error opening/reading/writing input or output file.");
            System.exit(1);
        } catch(NumberFormatException nfe) {
            System.err.println(nfe.toString());
            System.err.println("Error in file format");
            System.exit(1);
        }
        return result;
	}

	// argument 1: file name for input data: pass this to parse
	// argument 2: number of x-dimension buckets
	// argument 3: number of y-dimension buckets
	// argument 4: -v1, -v2, -v3, -v4, or -v5
	public static void main(String[] args) {
		try {
			if (args.length != 4) {
				throw new NumberFormatException();
			}
			
			CensusData pop = parse(args[0]);
			
			int totalx = Integer.parseInt(args[1]);
			int totaly = Integer.parseInt(args[2]);
			
			if (args[3].equals("-v1")) {
				version1(pop, totalx, totaly);
			} else if (args[3].equals("-v2")) {
				version2(pop, totalx, totaly);
			} else if (args[3].equals("-v3")) {
				version3(pop, totalx, totaly);
			} else if (args[3].equals("-v4")) {
				version4(pop, totalx, totaly);
			} else {
				throw new IllegalArgumentException();
			}
		} catch(Exception e) {
			System.out.println("Error in arguments.");
		}
	}

	private static void version1(CensusData pop, int totalx, int totaly) {
		
		// Find the minimum and maximum x and y
		float xmin = Float.POSITIVE_INFINITY;
		float xmax = Float.NEGATIVE_INFINITY;
		float ymin = Float.POSITIVE_INFINITY;
		float ymax = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < pop.data_size; i++) {
			xmin = Math.min(xmin, pop.data[i].longitude);
			xmax = Math.max(xmax, pop.data[i].longitude);
			ymin = Math.min(ymin, pop.data[i].latitude);
			ymax = Math.max(ymax, pop.data[i].latitude);
		}
		
		// Calculate how large is one block of x and y
		float xblock = (xmax - xmin) / totalx;
		float yblock = (ymax - ymin) / totaly;
		
		// Prompt for xleft, ybottom, xright, yup, for the first time.
		System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
		Scanner console = new Scanner(System.in);
		Scanner lineScan = new Scanner(console.nextLine());
		
		try {
			int xleft = lineScan.nextInt();
			int ybottom = lineScan.nextInt();
			int xright = lineScan.nextInt();
			int yup = lineScan.nextInt();
			
			if (lineScan.hasNext() || xleft < 1 || xleft > totalx 
								   || xright < 1 || xright > totalx
								   || ybottom < 1 || ybottom > totaly
								   || yup < 1 || yup > totaly
								   || xleft > xright
								   || ybottom > yup) { // If all arguments valid, enter while loop.
				throw new IllegalArgumentException(); // Otherwise, exit.
			}
			
			while (true) {
				
				// Calculate desired population and total population.
				int population = 0;
				int totalpop = 0;
				for (int i = 0; i < pop.data_size; i++) {
					int x = (int) (1 + (pop.data[i].longitude - xmin) / xblock);
					int y = (int) (1 + (pop.data[i].latitude - ymin) / yblock);
					
					totalpop += pop.data[i].population;
					
					if (x >= xleft && x <= xright && y >= ybottom && y <= yup
						|| xright == totalx && x == totalx + 1
						|| yup == totaly && y == totaly + 1) { // If in the desired range, or on the top/right edge
						population += pop.data[i].population;
					}
				}
				
				// Output the result
				System.out.println("population of rectangle: " + population);
				System.out.println("percent of total population: " + 
									String.format("%.2f", 100.0 * population / totalpop));
				
				// Prompt again.
				System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
				lineScan = new Scanner(console.nextLine());
				xleft = lineScan.nextInt();
				ybottom = lineScan.nextInt();
				xright = lineScan.nextInt();
				yup = lineScan.nextInt();
				
				if (lineScan.hasNext() || xleft < 1 || xleft > totalx 
									   || xright < 1 || xright > totalx
									   || ybottom < 1 || ybottom > totaly
									   || yup < 1 || yup > totaly
									   || xleft > xright
									   || ybottom > yup) { // If all arguments valid, enter while loop again.
					throw new IllegalArgumentException(); // Otherwise, exit.
				}
			}
			
		} catch (Exception e) {
			// Exit the while loop.
		}
	}

	private static void version2(CensusData pop, int totalx, int totaly) {
		
		// Use Fork Join to calculate x_max, x_min, y_max, y_min
		ForkJoinPool fjPool = new ForkJoinPool();
		Version2MinMax thread = new Version2MinMax(pop, 0, pop.data_size);
		fjPool.invoke(thread);
		
		// Calculate how large is one block of x and y
		float xblock = (thread.xmax - thread.xmin) / totalx;
		float yblock = (thread.ymax - thread.ymin) / totaly;
		
		// Prompt for input
		System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
		Scanner console = new Scanner(System.in);
		Scanner lineScan = new Scanner(console.nextLine());
		
		try {
			int xleft = lineScan.nextInt();
			int ybottom = lineScan.nextInt();
			int xright = lineScan.nextInt();
			int yup = lineScan.nextInt();
			
			if (lineScan.hasNext() || xleft < 1 || xleft > totalx 
								   || xright < 1 || xright > totalx
								   || ybottom < 1 || ybottom > totaly
								   || yup < 1 || yup > totaly
								   || xleft > xright
								   || ybottom > yup) { // If all arguments valid, enter while loop.
				throw new IllegalArgumentException(); // Otherwise, exit.
			}
			
			while (true) {
				// Use Fork Join to calculate desired population and total population, stored in "population"
				// and "totalpop" respectively.
				Version2Pop popthread = new Version2Pop(pop, xleft, ybottom, xright, yup, xblock, yblock,
										thread.xmin, thread.ymin, totalx, totaly, 0, pop.data_size);
				fjPool.invoke(popthread);
				
				// Output the result
				System.out.println("population of rectangle: " + popthread.population);
				System.out.println("percent of total population: " + 
									String.format("%.2f", 100.0 * popthread.population / popthread.totalpop));
				
				// Prompt again.
				System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
				lineScan = new Scanner(console.nextLine());
				xleft = lineScan.nextInt();
				ybottom = lineScan.nextInt();
				xright = lineScan.nextInt();
				yup = lineScan.nextInt();
				
				if (lineScan.hasNext() || xleft < 1 || xleft > totalx 
									   || xright < 1 || xright > totalx
									   || ybottom < 1 || ybottom > totaly
									   || yup < 1 || yup > totaly
									   || xleft > xright
									   || ybottom > yup) { // If all arguments valid, enter while loop again.
					throw new IllegalArgumentException(); // Otherwise, exit.
				}
			}
			
		} catch (Exception e) {
			// Exit the while loop.
		}
			
			
	}

	private static void version3(CensusData pop, int totalx, int totaly) {
		
		// Find the minimum and maximum x and y
		float xmin = Float.POSITIVE_INFINITY;
		float xmax = Float.NEGATIVE_INFINITY;
		float ymin = Float.POSITIVE_INFINITY;
		float ymax = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < pop.data_size; i++) {
			xmin = Math.min(xmin, pop.data[i].longitude);
			xmax = Math.max(xmax, pop.data[i].longitude);
			ymin = Math.min(ymin, pop.data[i].latitude);
			ymax = Math.max(ymax, pop.data[i].latitude);
		}
				
		// Calculate how large is one block of x and y
		float xblock = (xmax - xmin) / totalx;
		float yblock = (ymax - ymin) / totaly;
				
		// Prompt for xleft, ybottom, xright, yup, for the first time.
		System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
		Scanner console = new Scanner(System.in);
		Scanner lineScan = new Scanner(console.nextLine());
		
		try {
			int xleft = lineScan.nextInt();
			int ybottom = lineScan.nextInt();
			int xright = lineScan.nextInt();
			int yup = lineScan.nextInt();
					
			if (lineScan.hasNext() || xleft < 1 || xleft > totalx 
					|| xright < 1 || xright > totalx
					|| ybottom < 1 || ybottom > totaly
					|| yup < 1 || yup > totaly
					|| xleft > xright
					|| ybottom > yup) { // If all arguments valid, enter while loop.
						throw new IllegalArgumentException(); // Otherwise, exit.
			}
					
			while (true) {
				
				int[][] grid = new int[totalx][totaly];
				
				for (int i = 0; i < pop.data_size; i++) {
					int x = (int) (1 + (pop.data[i].longitude - xmin) / xblock);
					int y = (int) (1 + (pop.data[i].latitude - ymin) / yblock);
					
					// put everything in the grid
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
				
				// calculate the cumulative grid
				for (int i = 0; i < totalx; i++) {
					for (int j = totaly - 1; j >= 0; j--) {
						int left = 0;
						int leftup = 0;
						int up = 0;
						
						if (i > 0) {
							left = grid[i-1][j];
						}
						if (j < totaly - 1) {
							up = grid[i][j+1];
						}
						if (i > 0 && j < totaly - 1) {
							leftup = grid[i-1][j+1];
						}
						
						grid[i][j] = grid[i][j] + left + up - leftup;
					}
				}
				
				// calculate the desired population and the total population in O(1)
				int left = 0;
				int top = 0;
				int lefttop = 0;
				
				if (xleft - 1 > 0) {
					left = grid[xleft-2][ybottom-1];
				}
				if (yup - 1 < totaly - 1) {
					top = grid[xright-1][yup];
				}
				if (xleft - 1 > 0 && yup - 1 < totaly - 1) {
					lefttop = grid[xleft-2][yup];
				}
				
				int population = grid[xright-1][ybottom-1] - top - left + lefttop;
				int totalpop = grid [totalx-1][0];
				
				
				
				// Output the result
				System.out.println("population of rectangle: " + population);
				System.out.println("percent of total population: " + 
									String.format("%.2f", 100.0 * population / totalpop));
							
				// Prompt again.
				System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
				lineScan = new Scanner(console.nextLine());
				xleft = lineScan.nextInt();
				ybottom = lineScan.nextInt();
				xright = lineScan.nextInt();
				yup = lineScan.nextInt();
				
				if (lineScan.hasNext() || xleft < 1 || xleft > totalx 
									   || xright < 1 || xright > totalx
									   || ybottom < 1 || ybottom > totaly
									   || yup < 1 || yup > totaly
									   || xleft > xright
									   || ybottom > yup) { // If all arguments valid, enter while loop again.
					throw new IllegalArgumentException(); // Otherwise, exit.
				}
			}
		} catch (Exception e) {
			// Exit the while loop.
		}
	}

	private static void version4(CensusData pop, int totalx, int totaly) {
		
		// Use Fork Join to calculate x_max x_min y_max y_min
		ForkJoinPool fjPool = new ForkJoinPool();
		Version2MinMax thread = new Version2MinMax(pop, 0, pop.data_size);
		fjPool.invoke(thread);
		
		// Calculate how large is one block of x and y
		float xblock = (thread.xmax - thread.xmin) / totalx;
		float yblock = (thread.ymax - thread.ymin) / totaly;
		
		// Prompt for user inputs
		System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
		Scanner console = new Scanner(System.in);
		Scanner lineScan = new Scanner(console.nextLine());
		
		try {
			int xleft = lineScan.nextInt();
			int ybottom = lineScan.nextInt();
			int xright = lineScan.nextInt();
			int yup = lineScan.nextInt();
			
			if (lineScan.hasNext() || xleft < 1 || xleft > totalx 
								   || xright < 1 || xright > totalx
								   || ybottom < 1 || ybottom > totaly
								   || yup < 1 || yup > totaly
								   || xleft > xright
								   || ybottom > yup) { // If all arguments valid, enter while loop.
				throw new IllegalArgumentException(); // Otherwise, exit.
			}
			
			while (true) {
				
				// Use Fork Join to calculate the initial grid
				Version4Grid gridthread = new Version4Grid(pop, 0, pop.data_size, thread.xmin, thread.ymin, 
						xblock, yblock, totalx, totaly);
				int[][] grid = fjPool.invoke(gridthread);
				
				
				// calculate the cumulative grid sequentially
				for (int i = 0; i < totalx; i++) {
					for (int j = totaly - 1; j >= 0; j--) {
						int left = 0;
						int leftup = 0;
						int up = 0;
						
						if (i > 0) {
							left = grid[i-1][j];
						}
						if (j < totaly - 1) {
							up = grid[i][j+1];
						}
						if (i > 0 && j < totaly - 1) {
							leftup = grid[i-1][j+1];
						}
						
						grid[i][j] = grid[i][j] + left + up - leftup;
					}
				}
				
				// calculate the desired population and the total population in O(1)
				int left = 0;
				int top = 0;
				int lefttop = 0;
				
				if (xleft - 1 > 0) {
					left = grid[xleft-2][ybottom-1];
				}
				if (yup - 1 < totaly - 1) {
					top = grid[xright-1][yup];
				}
				if (xleft - 1 > 0 && yup - 1 < totaly - 1) {
					lefttop = grid[xleft-2][yup];
				}
				
				int population = grid[xright-1][ybottom-1] - top - left + lefttop;
				int totalpop = grid [totalx-1][0];
				
				
				// Output the result
				System.out.println("population of rectangle: " + population);
				System.out.println("percent of total population: " + 
									String.format("%.2f", 100.0 * population / totalpop));
				
				// Prompt again.
				System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
				lineScan = new Scanner(console.nextLine());
				xleft = lineScan.nextInt();
				ybottom = lineScan.nextInt();
				xright = lineScan.nextInt();
				yup = lineScan.nextInt();
				
				if (lineScan.hasNext() || xleft < 1 || xleft > totalx 
									   || xright < 1 || xright > totalx
									   || ybottom < 1 || ybottom > totaly
									   || yup < 1 || yup > totaly
									   || xleft > xright
									   || ybottom > yup) { // If all arguments valid, enter while loop again.
					throw new IllegalArgumentException(); // Otherwise, exit.
				}
			}
			
		} catch (Exception e) {
			// Exit the while loop.
		}
			
	}
}
