import java.util.*;

public class LLAPSearch extends GenericSearch {

	State rootState;
	int priceFood, priceMaterials, priceEnergy;
	int amountReqFood, delayReqFood;
	int amountReqMaterials, delayReqMaterials;
	int amountReqEnergy, delayReqEnergy;
	int priceBUILD1, foodUseBUILD1, materialsUseBUILD1, energyUseBUILD1, prosperityBUILD1;
	int priceBUILD2, foodUseBUILD2, materialsUseBUILD2, energyUseBUILD2, prosperityBUILD2;
	boolean foundIt=false;

	public LLAPSearch() {

	}

	@Override
	public String solve(String initalState, String strategy, boolean visualize) {
		//boolean condition;
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
			((Queue<Node>) list).add(rootNode);
			break;
		// rest of cases here...

		default:
			list = new PriorityQueue<Node>();
			// add root node here..
		}

		// handle the varieties of list here, i will just work with stack for now
		while (!((Stack<Node>) list).isEmpty()&&!foundIt) {
			// can i order mresource at the same moment of finishing pending or i have to
			// wait for next cycle?
			// if (pendingFood && timeToFood == 0) {
			// pendingFood = false;

			// }
			Node curNode = ((Stack<Node>) list).pop();
			// for simplicity, print inly the path cost for now
			// if (goalTest(curNode))
			// return "" + curNode.pathCost;

			expand(curNode, list);

		}

		return "NOSOLUTION";
	}

	void expand(Node cur, Object list) {
		if (this.blockedwall(cur))
			return;
		if(this.goalTest(cur)) {
			foundIt=true;
			//System.out.println(cur.state.toString());
			//System.out.println("I found it!!!!!");
			return;
		}
		if (list instanceof Stack) {

			for (Action act : Action.values()) {
				if (!cur.pending || (act == Action.WAIT || act == Action.BUILD1 || act == Action.BUILD2))
					((Stack<Node>) list).push(generate(cur, act));
			}
		} else if (list instanceof Queue) {
			for (Action act : Action.values()) {
				if (!cur.pending || (act == Action.WAIT || act == Action.BUILD1 || act == Action.BUILD2))
					((Queue<Node>) list).add(generate(cur, act));
			}
		}

	}

	Node generate(Node parent, Action operator) {
		State cur = parent.state;
		State state;
		switch (operator) {
		case RequestFood:

			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1, cur.moneySpent);
			return new Node(state, parent, Action.RequestFood, parent.depth + 1, true, false, false, this.delayReqFood,
					0, 0);
		case RequestMaterials:

			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1, cur.moneySpent);
			return new Node(state, parent, Action.RequestMaterials, parent.depth + 1, false, true, false, 0,
					this.delayReqMaterials, 0);
		case RequestEnergy:
			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1, cur.moneySpent);
			return new Node(state, parent, Action.RequestEnergy, parent.depth + 1, false, false, true, 0, 0,
					this.delayReqEnergy);
		case WAIT:
			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1, cur.moneySpent);
			if (!parent.pending) {
				return new Node(state, parent, Action.WAIT, parent.depth + 1);
			} else {
				return updatePending(parent, state);
			}
		case BUILD1:
			state = new State(cur.prosperity + this.prosperityBUILD1, cur.food - this.foodUseBUILD1,
					cur.materials - this.materialsUseBUILD1, cur.energy - this.energyUseBUILD1,
					cur.moneySpent + this.priceBUILD1);
			if (!parent.pending) {
				return new Node(state, parent, Action.BUILD1, parent.depth + 1);
			} else {
				return updatePending(parent, state);
			}
			
		case BUILD2:
			state = new State(cur.prosperity + this.prosperityBUILD2, cur.food - this.foodUseBUILD2,
					cur.materials - this.materialsUseBUILD2, cur.energy - this.energyUseBUILD2,
					cur.moneySpent + this.priceBUILD2);
			if (!parent.pending) {
				return new Node(state, parent, Action.BUILD2, parent.depth + 1);
			} else {
				return updatePending(parent, state);
			}
			
		default:
			return null;
		}

	}

	void tokenizeInitState(String initState) {
		initState=initState.replace(';', ',');
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
		this.priceBUILD1 = values[13];
		this.foodUseBUILD1 = values[14];
		this.materialsUseBUILD1 = values[15];
		this.energyUseBUILD1 = values[16];
		this.prosperityBUILD1 = values[17];
		this.priceBUILD2 = values[18];
		this.foodUseBUILD2 = values[19];
		this.materialsUseBUILD2 = values[20];
		this.energyUseBUILD2 = values[21];
		this.prosperityBUILD2 = values[22];
	}

	boolean goalTest(Node cur) {
		return cur.state.prosperity >= 100;
	}

	boolean blockedwall(Node cur) {
		State state = cur.state;
		if (state.moneySpent >= 100000 || state.food == 0 || state.materials == 0 || state.energy == 0)
			return true;
		return false;
	}

	Node updatePending(Node cur, State newState) {

		Node res = new Node(newState, cur, Action.WAIT, cur.depth + 1, cur.pendingFood, cur.pendingMaterials,
				cur.pendingEnergy, cur.timeToFood, cur.timeToMaterials, cur.timeToEnergy);

		if (cur.timeToFood > 0) {
			res.timeToFood = cur.timeToFood - 1;
			if (res.timeToFood == 0) {
				res.pendingFood = false;
			}
		}
		if (cur.timeToMaterials > 0) {
			res.timeToMaterials = cur.timeToMaterials - 1;
			if (res.timeToMaterials == 0) {
				res.pendingMaterials = false;
			}
		}
		if (cur.timeToEnergy > 0) {
			res.timeToEnergy = cur.timeToEnergy - 1;
			if (res.timeToEnergy == 0) {
				res.pendingEnergy = false;
			}
		}
		res.pending=res.pendingFood||res.pendingMaterials||res.pendingEnergy;
		
		
		return res;
	}

	public static void main(String args[]) {
		LLAPSearch l1=new LLAPSearch();
		String init="50;"+
				"22,22,22;" +
				"50,60,70;" +
				"30,2;19,1;15,1;" +
				"300,5,7,3,20;" +
				"500,8,6,3,40;";
		l1.solve(init, "DF", false);
	}
	
}
