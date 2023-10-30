
public class Node {
	State state;
	Node parent;
	Action operator;
	int depth;
	int pathCost;
	
	public Node(State s,Node p, Action a,int d) {
		state=s;
		parent=p;
		operator=a;
		depth=d;
		//pathCost=cost;
	}
}
