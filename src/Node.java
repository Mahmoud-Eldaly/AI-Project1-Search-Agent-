
public class Node implements Comparable {
	State state;
	Node parent;
	Action operator;
	int depth;
	int compareidx = 0;
	// int pathCost;

	/////////////////////////////

	boolean pendingFood, pendingMaterials, pendingEnergy;
	boolean pending = false;
	int timeToFood = Integer.MAX_VALUE, timeToMaterials = Integer.MAX_VALUE, timeToEnergy = Integer.MAX_VALUE;

	public Node(State s, Node p, Action a, int d, int cidx) {
		state = s;
		parent = p;
		operator = a;
		depth = d;
		compareidx = cidx;

		// pathCost=cost;
	}

	public Node(State s, Node p, Action a, int d, boolean pfood, boolean pmaterials, boolean penergy, int cidx) {
		state = s;
		parent = p;
		operator = a;
		depth = d;
		pendingFood = pfood;
		pendingMaterials = pmaterials;
		pendingEnergy = penergy;
		pending = pfood || pmaterials || penergy;
		compareidx = cidx;

		// pathCost=cost;
	}

	public Node(State s, Node p, Action a, int d, boolean pfood, boolean pmaterials, boolean penergy, int tfood,
			int tmaterials, int tenergy, int cidx) {
		state = s;
		parent = p;
		operator = a;
		depth = d;
		pendingFood = pfood;
		pendingMaterials = pmaterials;
		pendingEnergy = penergy;
		pending = pfood || pmaterials || penergy;
		timeToFood = pfood ? tfood : Integer.MAX_VALUE;
		timeToMaterials = pmaterials ? tmaterials : Integer.MAX_VALUE;
		timeToEnergy = penergy ? tenergy : Integer.MAX_VALUE;
		compareidx = cidx;

		// pathCost=cost;
	}

	public int h1() {
		return (100 - this.state.prosperity);
	}

	public int compare1(Object o) {
		return (this.h1() - ((Node) o).h1());
	}

	public int h2() {
		// not centered?!--> goal.h2()!=0???
		return (this.h1() + this.state.moneySpent);
	}

	public int compare2(Object o) {
		return this.h2() - ((Node) o).h2();
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		if (compareidx == 1)
			return compare1(o);
		else if (compareidx == 2)
			return compare2(o);
		return (this.state.moneySpent - ((Node) o).state.moneySpent);
	}
}
