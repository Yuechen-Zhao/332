/**
 * 
 * This is the TreeNode class that keeps track of a single node in a splay tree.
 * It has three public fields, key, left and right, which allow splay tree class
 * to refer to easily.
 *
 */

public class TreeNode {
	
	int key; // the 'info' field
	TreeNode left; // the left child of this node
	TreeNode right; // the right child of this node
	
	/**
	 * This constructor constructs a tree with given key value, left and right
	 * children.
	 * 
	 * @param key The key value.
	 * @param left The left child.
	 * @param right The right child.
	 */
	public TreeNode(int key, TreeNode left, TreeNode right) {
		this.key = key;
		this.left = left;
		this.right = right;
	}
	
	/**
	 * This constructor constructs a tree with given key value, and no children.
	 * @param key The key value.
	 */
	public TreeNode(int key) {
		this(key, null, null);
	}
}
