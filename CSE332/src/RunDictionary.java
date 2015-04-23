import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * 
 * This is the main class that takes the names of input and output files
 * and takes the commands from input file, sort the data using splay tree,
 * and output the result to the output file.
 *
 */

public class RunDictionary {
	
	/**
	  * This is the main method. It read commands from a input file and
	  * writes results to an output file, using a splay tree.
	  * 
	  * @param It takes two parameters as input and output file names.
	  * @throws FileNotFoundException if the input file does not exist.
	  * @throws IllegalArgumentException if not exactly two parameters are
	  *         passed.
	  * @throws IllegalArgumentException if the command given in the input
	  *         file is not legal.
	  */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws FileNotFoundException {
		if (args.length != 2) {
			throw new IllegalArgumentException();
		}
		
		String in = args[0];
		String out = args[1];
		
		Scanner input = new Scanner(new File(in));
		PrintStream output = new PrintStream(new File(out));
		
		SplayTree tree = new SplayTree();
		
		while (input.hasNextLine()) {
			String line = input.nextLine();
			Scanner lineScan = new Scanner(line);
			String command = lineScan.next();
			int target = lineScan.nextInt();
			
			if (command.equals("insert")) {
				tree.insert(target);
			} else if (command.equals("delete")) {
				tree.delete(target);
			} else if (command.equals("lookup")) {
				tree.lookup(target);
			} else {
				throw new IllegalArgumentException();
			}
			
			output.println(line);
			tree.display(output);
		}
		input.close();
		output.close();
	}
}
