public class Stack {
	private TreeNode[] list;
	private int current;
	
	public Stack() {
		list = new TreeNode[1000];
		current = -1;
	}
	
	public TreeNode pop() {
		if (current < 0) {
			throw new IndexOutOfBoundsException();
		}
		current--;
		return list[current + 1];
	}
	
	public void push(TreeNode a) {
		current++;
		list[current] = a;
	}
	
	public boolean isEmpty() {
		return current == -1;
	}
}
