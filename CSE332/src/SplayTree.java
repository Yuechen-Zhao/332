import java.io.PrintStream;
import java.util.ArrayList;


public class SplayTree {
	
	// This field is the splay tree's root.
	private TreeNode root;
	
	/**
	  * Constructor
	  */
	public SplayTree() {
		root = null;
	}
	
	public SplayTree(TreeNode node) {
		root = node;
	}
	
	/**
	  * Deletes the given target if found. Does not delete any node, but only splay
	  * the tree if target not found.
	  * 
	  * @param target The number to delete from the tree.
	  */
	public void delete(int target) {
		
		if (lookup(target)) {
			TreeNode left = root.left;
			TreeNode right = root.right;
			root = concat(left, right);
		}
	}
	
	/**
	  * Concatenates two trees by finding the smallest value in the right tree.
	  * Used by delete(int target).
	  * 
	  * @param t1 The left tree
	  * @param t2 The right tree
	  * @return The tree node of the concatenated tree.
	  */
	private TreeNode concat(TreeNode t1, TreeNode t2) {
		if (t2 != null) {
			SplayTree left = new SplayTree(t1);
			left.splay(t2.key);
			left.root.right = t2;
			return left.root;
		} else {
			return t1;
		}
	}
	
	/**
	  * Splays the tree by the given target. Returns true if root == target, and
	  * returns false otherwise.
	  * 
	  * @param target The target to look up.
	  * @return true if root == target after splay, false otherwise.
	  */
	public boolean lookup(int target) {
		
		splay(target);
		if (root == null) {
			return false;
		}
		return root.key == target;
	}
	
	/**
	  * Inserts the given value into the tree if not exist, and does not insert duplicates
	  * if value already exists.
	  * 
	  * @param target The value to be inserted.
	  */
	public void insert(int target) {
		
		splay(target);
		if (root == null) {
			root = new TreeNode(target);
		} else if (root.key > target) {
			TreeNode left = root.left;
			root.left = null;
			TreeNode right = root;
			root = new TreeNode(target, left, right);
		} else if (root.key < target) {
			TreeNode right = root.right;
			root.right = null;
			TreeNode left = root;
			root = new TreeNode(target, left, right);
		}
	}
	
	/**
	  * Splays the tree by the given value.
	  * 
	  * @param target The value used to splay the tree.
	  */
	private void splay(int target) {
		ArrayList<String> depth = new ArrayList<String>();
		root = splay(root, target, depth);
		
		// If the history list still has one element left, rotate the root one more time.
		if (!depth.isEmpty()) {
			root = rotate(root, depth.get(0));
		}
	}
	
	/**
	  * Splays the node using recursion.
	  * 
	  * @param node The node from where to splay
	  * @param target The target value to splay the tree
	  * @param depth The list that stores the splay history
	  * @return TreeNode The treenode that is already splayed.
	  */
	private TreeNode splay(TreeNode node, int target, ArrayList<String> depth) {
		// if node is null, do nothing.
		if (node == null) {
			return null;
		}
		
		// if key > or < target, splay the corresponding child. Then add to the history
		// list.
		if (node.key > target && node.left != null) {
			node.left = splay(node.left, target, depth);
			depth.add(0, "L");
		} else if (node.key < target && node.right != null) {
			node.right = splay(node.right, target, depth);
			depth.add(0, "R");
		}
		
		// If the history list's size reaches 2, do a zig-zag or zig-zig rotation.
		if (depth.size() == 2) {
			String first = depth.get(0);
			String second = depth.get(1);
			
			if (first.equals(second)) { // first == second, do a zig-zig rotation
				node = rotate(node, first);
				node = rotate(node, second);
			} else {
				if (first.equals("L")) { // otherwise first != second, zig-zag
					node.left = rotate(node.left, second);
					node = rotate(node, "L");
				} else {
					node.right = rotate(node.right, second);
					node = rotate(node, "R");
				}
			}
			depth.clear();
		}
		
		// returns the node.
		return node;
	}
	
	/**
	  * Rotates the tree once.
	  * 
	  * @param node The node to rotate
	  * @param c The direction to rotate. c is either "L" or "R".
	  * @return TreeNode The node after a single rotation.
	  */
	private TreeNode rotate(TreeNode node, String c) {
		
		if (c.equals("R")) {
			TreeNode prev = node;
			TreeNode y = node.right.left;
			node = node.right;
			node.left = prev;
			prev.right = y;
		} else {
			TreeNode prev = node;
			TreeNode y = node.left.right;
			node = node.left;
			node.right = prev;
			prev.left = y;
		}
		return node;
	}
	
	/**
	  * Displays the tree.
	  */
	public void display(PrintStream output) {
		display(root, 0, output);
	}
	
	/**
	  * Displays the tree.
	  * 
	  * @param node The node to display
	  * @param depth The depth of the given node
	  * @param output The output stream to write to
	  */
	private void display(TreeNode node, int depth, PrintStream output) {
		for (int i = 0; i < depth; i++) {
			output.print("  ");
		}
		if (node == null) {
			output.println("-");
		} else if (node.left == null && node.right == null) {
			output.println(node.key);
		} else {
			output.println(node.key);
			display(node.left, depth + 1, output);
			display(node.right, depth + 1, output);
		}
	}
}
