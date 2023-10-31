import java.util.*;

public class LLAPSearch extends GenericSearch {

	State rootState;
	int priceFood, priceMaterials, priceEnergy;
	int amountReqFood, delayReqFood;
	int amountReqMaterials, delayReqMaterials;
	int amountReqEnergy, delayReqEnergy;
	int priceBUILD1, foodUseBUILD1, materialsUseBUILD1, energyUseBUILD1, prosperityBUILD1;
	int priceBUILD2, foodUseBUILD2, materialsUseBUILD2, energyUseBUILD2, prosperityBUILD2;
	boolean foundIt = false;
	String solution = "NOSOLUTION";
	int nodesExpanded = 0;

	public LLAPSearch() {

	}

	@Override
	public String solve(String initalState, String strategy, boolean visualize) {
		// boolean condition;
		tokenizeInitState(initalState);
		Node rootNode = new Node(rootState, null, null, 0);
		Object list;
		switch (strategy) {
		case "DF":
			list = new Stack<Node>();
			((Stack<Node>) list).push(rootNode);
			break;
		case "BF":
			list = new LinkedList<Node>();
			((LinkedList<Node>) list).add(rootNode);
			break;
		case "UC":
			list = new PriorityQueue<Node>();
			((PriorityQueue<Node>) list).add(rootNode);
			break;
		// rest of cases here...

		default:
			list = new PriorityQueue<Node>();
			// add root node here..
		}

		// handle the varieties of list here, i will just work with stack for now
		//boolean termination=false;
		//if(list instanceof Stack<Node>)
			
//		while (!((Stack<Node>) list).isEmpty() && !foundIt) {
//			expand(((Stack<Node>) list).pop(), list);
//		}
		
		if(list instanceof Stack)
			solveDF(list);
		else if(list instanceof LinkedList)
			solveBF(list);
		else if(list instanceof PriorityQueue) {
			System.out.println("PQ");
			solveUC(list);
		}

		return this.solution + ";" + this.nodesExpanded;
	}
	
	void solveDF(Object list) {
		while (!((Stack<Node>) list).isEmpty() && !foundIt) {
			expand(((Stack<Node>) list).pop(), list);
		}
	}
	
	void solveBF(Object list) {
		while (!((LinkedList<Node>) list).isEmpty() && !foundIt) {
			expand(((LinkedList<Node>) list).remove(), list);
		}
	}
	
	
	//not done yet!!!
	void solveID(Object list) {
		while (!((LinkedList<Node>) list).isEmpty() && !foundIt) {
			expand(((LinkedList<Node>) list).remove(), list);
		}
	}
	
	void solveUC(Object list) {
		System.out.println(list instanceof PriorityQueue);
		while (!((PriorityQueue<Node>) list).isEmpty() && !foundIt) {
			expand(((PriorityQueue<Node>) list).remove(), list);
		}
	}

	void expand(Node cur, Object list) {
		this.nodesExpanded++;
		// System.out.println(cur.state.moneySpent);
		if (this.blockedwall(cur))
			return;
		if (this.goalTest(cur)) {
			foundIt = true;
			this.solution = printChain(cur);
			// System.out.println(cur.state.toString());
			// System.out.println("I found it!!!!!");
			return;
		}
		if (list instanceof Stack) {

			for (Action act : Action.values()) {
				if ((!cur.pending && act != Action.WAIT)
						|| ((cur.pending && act == Action.WAIT) || act == Action.BUILD1 || act == Action.BUILD2))
					((Stack<Node>) list).push(generate(cur, act));
			}
		} else if (list instanceof LinkedList) {
			for (Action act : Action.values()) {
				if ((!cur.pending && act != Action.WAIT)
						|| ((cur.pending && act == Action.WAIT) || act == Action.BUILD1 || act == Action.BUILD2))
					((LinkedList<Node>) list).add(generate(cur, act));
			}
		} else if(list instanceof PriorityQueue) {
			for (Action act : Action.values()) {
				if ((!cur.pending && act != Action.WAIT)
						|| ((cur.pending && act == Action.WAIT) || act == Action.BUILD1 || act == Action.BUILD2))
					((PriorityQueue<Node>) list).add(generate(cur, act));
				
			}
		} 

	}

	Node generate(Node parent, Action operator) {
		State cur = parent.state;
		State state;
		switch (operator) {
		case RequestFood:

			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
					cur.moneySpent + priceFood + priceMaterials + priceEnergy);
			return new Node(state, parent, Action.RequestFood, parent.depth + 1, true, false, false, this.delayReqFood,
					0, 0);
		case RequestMaterials:
			////// price resoures here...
			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
					cur.moneySpent + priceFood + priceMaterials + priceEnergy);
			return new Node(state, parent, Action.RequestMaterials, parent.depth + 1, false, true, false, 0,
					this.delayReqMaterials, 0);
		case RequestEnergy:
			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
					cur.moneySpent + priceFood + priceMaterials + priceEnergy);
			return new Node(state, parent, Action.RequestEnergy, parent.depth + 1, false, false, true, 0, 0,
					this.delayReqEnergy);
		case WAIT:
			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
					cur.moneySpent + priceFood + priceMaterials + priceEnergy);
			if (!parent.pending) {
				return new Node(state, parent, Action.WAIT, parent.depth + 1);
			} else {
				return updatePending(parent, state, Action.WAIT);
			}
		case BUILD1:
			state = new State(cur.prosperity + this.prosperityBUILD1, cur.food - this.foodUseBUILD1,
					cur.materials - this.materialsUseBUILD1, cur.energy - this.energyUseBUILD1,
					cur.moneySpent + this.priceBUILD1);
			if(this.blockedwallState(state))
				state.blocked=true;
			if (!parent.pending) {
				return new Node(state, parent, Action.BUILD1, parent.depth + 1);
			} else {
				return updatePending(parent, state, Action.BUILD1);
			}

		case BUILD2:
			state = new State(cur.prosperity + this.prosperityBUILD2, cur.food - this.foodUseBUILD2,
					cur.materials - this.materialsUseBUILD2, cur.energy - this.energyUseBUILD2,
					cur.moneySpent + this.priceBUILD2);
			if(this.blockedwallState(state))
				state.blocked=true;
			if (!parent.pending) {
				return new Node(state, parent, Action.BUILD2, parent.depth + 1);
			} else {
				return updatePending(parent, state, Action.BUILD2);
			}

		default:
			return null;
		}

	}

	void tokenizeInitState(String initState) {
		initState = initState.replace(';', ',');
		String[] Strvalues = initState.split(",");
		int[] values = new int[Strvalues.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = Integer.parseInt(Strvalues[i]);
		}

		this.rootState = new State(values[0], values[1], values[2], values[3], 0);
		this.priceFood = values[4];
		this.priceMaterials = values[5];
		this.priceEnergy = values[6];
		this.amountReqFood = values[7];
		this.delayReqFood = values[8];
		this.amountReqMaterials = values[9];
		this.delayReqMaterials = values[10];
		this.amountReqEnergy = values[11];
		this.delayReqEnergy = values[12];
		this.priceBUILD1 = values[13] + (this.foodUseBUILD1 * this.priceFood)
				+ (this.materialsUseBUILD1 * this.priceMaterials) + (this.energyUseBUILD1 * this.priceEnergy);
		this.foodUseBUILD1 = values[14];
		this.materialsUseBUILD1 = values[15];
		this.energyUseBUILD1 = values[16];
		this.prosperityBUILD1 = values[17];
		this.priceBUILD2 = values[18] + (this.foodUseBUILD2 * this.priceFood)
				+ (this.materialsUseBUILD2 * this.priceMaterials) + (this.energyUseBUILD2 * this.priceEnergy);
		;
		this.foodUseBUILD2 = values[19];
		this.materialsUseBUILD2 = values[20];
		this.energyUseBUILD2 = values[21];
		this.prosperityBUILD2 = values[22];

		
	}

	String printChain(Node cur) {
		String res = ";" + cur.state.moneySpent;
		while (cur.parent != null) {
			System.out.println(cur.state);
			res = cur.operator + "," + res;
			cur = cur.parent;
		}
		// res=res+";"+
		System.out.println(rootState.toString());
		return res;
	}

	boolean goalTest(Node cur) {
		return cur.state.prosperity >= 100;
	}

	boolean blockedwall(Node cur) {
		State state = cur.state;
	  return blockedwallState(state);
	}
	
	boolean blockedwallState(State state) {
		
		if (state.moneySpent >= 100000 || state.food <= 0 || state.materials <= 0 || state.energy <= 0||state.blocked)
			return true;
		return false;
	}

	Node updatePending(Node cur, State newState, Action a) {

		Node res = new Node(newState, cur, a, cur.depth + 1, cur.pendingFood, cur.pendingMaterials, cur.pendingEnergy,
				cur.timeToFood, cur.timeToMaterials, cur.timeToEnergy);

		if (cur.timeToFood > 0) {
			res.timeToFood = cur.timeToFood - 1;
			if (res.timeToFood == 0) {
				res.state.food += amountReqFood;
				res.state.food = Math.min(res.state.food, 50);
				res.pendingFood = false;
			}
		}
		if (cur.timeToMaterials > 0) {
			res.timeToMaterials = cur.timeToMaterials - 1;
			if (res.timeToMaterials == 0) {
				res.state.materials += amountReqMaterials;
				res.state.materials = Math.min(res.state.materials, 50);
				res.pendingMaterials = false;
			}
		}
		if (cur.timeToEnergy > 0) {
			res.timeToEnergy = cur.timeToEnergy - 1;
			if (res.timeToEnergy == 0) {
				res.state.energy += amountReqEnergy;
				res.state.energy = Math.min(res.state.energy, 50);
				res.pendingEnergy = false;
			}
		}
		res.pending = res.pendingFood || res.pendingMaterials || res.pendingEnergy;

		return res;
	}

	public static void main(String args[]) {
		LLAPSearch l1 = new LLAPSearch();
		String init =  "30;" +
                "30,25,19;" +
                "90,120,150;" +
                "9,2;13,1;11,1;" +
                "3195,11,12,10,34;" +
                "691,7,8,6,15;";
		
		//System.out.println(l1.solve(init, "DF", false));
		//System.out.println(l1.solve(init, "BF", false));
		System.out.println(l1.solve(init, "UC", false));
	}

}
