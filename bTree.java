package aBall;

public class bTree {

	double lastSize;
	private static final double DELTASIZE= 0.1;
	private double Xo=0;
	private double Yo;
	private boolean simRunning = false;
/**
 * Implements a B-Tree class using a NON-RECURSIVE algorithm.
 */

// Instance variables
	
	bNode root=null;
	
/**
 * addNode method - adds a new node by descending to the leaf node
 *                  using a while loop in place of recursion.  Ugly,
 *                  yet easy to understand.
 */
	
	
	public void addNode(aBall data) {
		
		bNode current; 

// Empty tree
		
		if (root == null) {
			root = makeNode(data);
		}
		
// If not empty, descend to the leaf node according to
// the input data.  
		
		else {
			current = root;
			while (true) {
				if (data.getbSize() < current.data.getbSize()) {
					
// New data < data at node, branch left		
					if (current.left == null) {				// leaf node
						current.left = makeNode(data);		// attach new node here
						break;
					}
					else {									// otherwise
						current = current.left;				// keep traversing
					}
				}
				else {
// New data >= data at node, branch right
					
					if (current.right == null) {			// leaf node	
						current.right = makeNode(data);		// attach
						break;
					}
					else {									// otherwise 
						current = current.right;			// keep traversing
					}
				}
			}
		}
		
	}
	
/**
 * makeNode
 * 
 * Creates a single instance of a bNode
 * 
 * @param	aBall data   Data from aBall class to be added
 * @return  bNode node Node created
 */
	
	bNode makeNode(aBall data) {
		bNode node = new bNode();							// create new object
		node.data = data;									// initialize data field
		node.left = null;									// set both successors
		node.right = null;									// to null
		return node;										// return handle to new object
	}
	
	
/**
 * stackBalls() method - stacks the balls in the x/y directions using the stacking algorithm in traverse_inorder
 */
	
	public void stackBalls() {	
		lastSize = 0;
		Xo = 0; 
		Yo = 0;
		traverse_inorder(root);
	}
	
/**
 * traverse_inorder() method - recursively traverses tree in order (LEFT-Root-RIGHT)
 * Upon traversal, the tree updates the position of each ball using a stacking algorithm which will be implemented in the stackBalls method above
 */
	
	private void traverse_inorder(bNode root) { // not outputting anything, printing inside of itself not printing into the code 
		if (root.left != null) traverse_inorder(root.left);
		//System.out.println("I am printing " + root.data.getbSize()); Used this print statement to make sure tree was stacked correctly 
		
		if (root.data.getbSize()-lastSize > DELTASIZE) { //new stack
			Xo += lastSize + root.data.getbSize();
			Yo = root.data.getbSize();
			lastSize=root.data.getbSize(); 
			
		} else { //add on top
			Yo += (lastSize + root.data.getbSize());
		}
		root.data.moveTo(Xo, Yo);

		//lastSize=root.data.getbSize(); //stacks a lot of balls into two skinny columns 
		
		if (root.right != null) traverse_inorder(root.right);
	}
/** traverse() method - gets each ball's state (moving or not moving) by setting movement equal to true 
 * @param bNode root Starts traversal from root 
 */
	public void traverse(bNode root) {
		if (root.left != null) traverse(root.left);
		if (root.data.getbState()) simRunning = true; 
		if (root.right != null) traverse(root.right);
		return;
		}
/** isRunning() method - determines whether the simulation is still running based on the state of each individual ball
 * 
 * @return simRunning (if true: signifies the simulation is still running; if false: signifies the simulation has stopped entirely)
 */
	public boolean isRunning() {
		simRunning = false; 
		traverse(root); 
		return simRunning;
	}
	
/**
 * preorder method - preorder traversal via call to recursive method
 * 
 */
	
	public void preorder() {
		traverse_preorder(root);
	}
	
/**
 * traverse_preorder method - recursively traverses tree in preorder (Root-LEFT-RIGHT) and prints each node.
 */

	public void traverse_preorder(bNode root) {
		System.out.println(root.data); 
		if (root.left != null) traverse_preorder(root.left);
		if (root.right != null) traverse_preorder(root.right);
	}
	
/**
 * postorder method - postorder traversal via call to recursive method
 */
	
	public void postorder() {
		traverse_postorder(root);
	}
	
/**
 * traverse_postorder method - recursively traverses tree in postorder (LEFT-RIGHT-Root) and prints each node.
 */
	
	public void traverse_postorder(bNode root) {
		if (root.left != null) traverse_postorder(root.left);
		if (root.right != null) traverse_postorder(root.right);
		System.out.println(root.data);
	}

}

/**
 * A simple bNode class for use by bTree.  The "payload" can be
 * modified accordingly to support any object type.
 *
 */

class bNode {
	aBall data; // changed this from int to aBall so that data can come from the aBall class 
	bNode left;
	bNode right;
}


