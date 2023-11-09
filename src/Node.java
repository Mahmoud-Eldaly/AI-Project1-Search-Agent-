
public class Node implements Comparable {
	State state;
	Node parent;
	Action operator;
	int depth;
	int compareidx = 0;
	// int pathCost;

	/////////////////////////////TODO
///to be moved...
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
		return Math.max(0,(100 - this.state.prosperity));
	}
	
	public double MoneyPerPros() {
		double r1=(1.0*this.state.priceBUILD1)/this.state.prosperityBUILD1;
		double r2=(1.0*this.state.priceBUILD2)/this.state.prosperityBUILD2;
		return Math.min(r1, r2);
	}
	
	public int admH1() {
		return (int)(this.h1()*this.MoneyPerPros());
	}
	
	public int f1() {
		return this.state.moneySpent+admH1();
	}

	public int compare1(Object o) {
		return (this.h1() - ((Node) o).h1());
	}

	public int h2() {
		// not centered?!--> goal.h2()!=0???
		//need to be done...
		return (this.h1() + this.state.moneySpent);
	}

	public int compare2(Object o) {
		return this.h2() - ((Node) o).h2();
	}
	
	public int admH2() {
		//need admissible hueristic 2;
		return 0;
	}
	
	public int f2() {
		return this.state.moneySpent+admH2();
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		if (compareidx == 1)
			return compare1(o);
		else if (compareidx == 2)
			return compare2(o);
		else if(compareidx==3)
			return this.f1();
		else if(compareidx==4)
			return this.f2();///////////////////////Just place holder waiting for another adm hueristic
		return (this.state.moneySpent - ((Node) o).state.moneySpent);
	}
}
