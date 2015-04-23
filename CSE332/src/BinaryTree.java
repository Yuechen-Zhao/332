public class BinaryTree {
	private TreeNode root;
	
	public BinaryTree() {
		root = null;
	}
	
	public int rootKey() {
		return root.key;
	}
	
	public TreeNode root() {
		return root;
	}
	
	public TreeNode left() {
		return root.left;
	}
	
	public TreeNode right() {
		return root.right;
	}
	
	public boolean isEmpty() {
		return root == null;
	}
	
	public void insert(int key) {
		root = insert(key, root);
	}
	
	private TreeNode insert(int key, TreeNode node) {
		if (node == null) {
			return new TreeNode(key);
		}
		
		if (key <= node.key) {
			node.left = insert(key, node.left);
		} else {
			node.right = insert(key, node.right);
		}
		
		return node;
	}
}
