import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

public class SortInts {
	public static void main(String[] args) throws FileNotFoundException {
		String in = args[0];
		String out = args[1];
		
		Scanner input = new Scanner(new File(in));
		@SuppressWarnings("resource")
		PrintStream output = new PrintStream(new File(out));
		
		BinaryTree bst = new BinaryTree();
		while (input.hasNextLine()) {
			bst.insert(input.nextInt());
		}
		
		TreeNode node = bst.root();
		Stack s = new Stack();
		s.push(null);
		while (!s.isEmpty()) {
			while (node != null) {
				s.push(node);
				node = node.left;
			}
			node = s.pop();
			if (node != null) {
				output.println(node.key);
				node = node.right;
			}
		}
	}
}
