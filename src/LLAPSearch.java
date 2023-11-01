import java.util.*;

public class LLAPSearch extends GenericSearch {

	static State rootState;
	static int priceFood, priceMaterials, priceEnergy;
	static int amountReqFood, delayReqFood;
	static int amountReqMaterials, delayReqMaterials;
	static int amountReqEnergy, delayReqEnergy;
	static int priceBUILD1, foodUseBUILD1, materialsUseBUILD1, energyUseBUILD1, prosperityBUILD1;
	static int priceBUILD2, foodUseBUILD2, materialsUseBUILD2, energyUseBUILD2, prosperityBUILD2;
	static boolean foundIt = false;
	static String solution = "NOSOLUTION";
	static int nodesExpanded = 0;

	public LLAPSearch() {

	}

	public static String solve(String initalState, String strategy, boolean visualize) {
		// boolean condition;
		tokenizeInitState(initalState);
		Node rootNode = new Node(rootState, null, null, 0,0);
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
		case "ID":
			list = new Stack<Node>();
			((Stack<Node>) list).push(rootNode);
			break;
		case "UC":
			list = new PriorityQueue<Node>();
			((PriorityQueue<Node>) list).add(rootNode);
			break;
		case "GR1":
			list = new PriorityQueue<Node>();
			rootNode.compareidx=1;
			((PriorityQueue<Node>) list).add(rootNode);
			break;
		case "GR2":
			list = new PriorityQueue<Node>();
			rootNode.compareidx=2;
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
		
		if(strategy.equals("DF"))
			solveDF(list);
		else if(strategy.equals("BF"))
			solveBF(list);
		else if(strategy.equals("ID"))
			solveID(list);
		else if(strategy.equals("UC")||strategy.equals("GR1")||strategy.equals("GR2")) 
			solveUC(list);
//		else if(strategy.equals("GR1")) 
//			solveUC(list);
//		else if(strategy.equals("GR2")) 
//			solveUC(list);
		String res=solution.equals("NOSOLUTION")?"NOSOLUTION":solution + ";" + nodesExpanded;
		reset();
		return res;
	}
	
	static void solveDF(Object list) {
		while (!((Stack<Node>) list).isEmpty() && !foundIt) {
			expand(((Stack<Node>) list).pop(), list,Integer.MAX_VALUE);
		}
	}
	
	static void solveBF(Object list) {
		while (!((LinkedList<Node>) list).isEmpty() && !foundIt) {
			expand(((LinkedList<Node>) list).remove(), list,Integer.MAX_VALUE);
		}
	}
	
	static void solveID(Object list) {
		for (int i = 0;!foundIt; i++) {
			if(((Stack<Node>) list).isEmpty())
				((Stack<Node>) list).push(new Node(rootState, null, null, 0,0));
			while (!((Stack<Node>) list).isEmpty() && !foundIt) {
				expand(((Stack<Node>) list).pop(), list,i);
			}
			
		}
	}
	
	static void solveUC(Object list) {
		while (!((PriorityQueue<Node>) list).isEmpty() && !foundIt) {
			expand(((PriorityQueue<Node>) list).remove(), list,Integer.MAX_VALUE);
		}
	}
	
//	
//	void solveGR1(Object list) {
//		while (!((PriorityQueue<Node>) list).isEmpty() && !foundIt) {
//			expand(((PriorityQueue<Node>) list).remove(), list,Integer.MAX_VALUE);
//		}
//	}

	static void expand(Node cur, Object list,int limit) {
		nodesExpanded++;
		 //System.out.println(cur.state.moneySpent+"      "+cur.depth);
		if(cur.state.blocked)
			nodesExpanded--;
		if (blockedwall(cur)||cur.depth==limit)
			return;
		if (goalTest(cur)) {
			foundIt = true;
			solution = printChain(cur);
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

	static Node generate(Node parent, Action operator) {
		State cur = parent.state;
		State state;
		switch (operator) {
		case RequestFood:

			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
					cur.moneySpent + priceFood + priceMaterials + priceEnergy);
			return new Node(state, parent, Action.RequestFood, parent.depth + 1, true, false, false, delayReqFood,
					0, 0,parent.compareidx);
		case RequestMaterials:
			////// price resoures here...
			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
					cur.moneySpent + priceFood + priceMaterials + priceEnergy);
			return new Node(state, parent, Action.RequestMaterials, parent.depth + 1, false, true, false, 0,
					delayReqMaterials, 0,parent.compareidx);
		case RequestEnergy:
			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
					cur.moneySpent + priceFood + priceMaterials + priceEnergy);
			return new Node(state, parent, Action.RequestEnergy, parent.depth + 1, false, false, true, 0, 0,
					delayReqEnergy,parent.compareidx);
		case WAIT:
			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
					cur.moneySpent + priceFood + priceMaterials + priceEnergy);
			if (!parent.pending) {
				return new Node(state, parent, Action.WAIT, parent.depth + 1,parent.compareidx);
			} else {
				return updatePending(parent, state, Action.WAIT);
			}
		case BUILD1:
			state = new State(cur.prosperity + prosperityBUILD1, cur.food - foodUseBUILD1,
					cur.materials - materialsUseBUILD1, cur.energy - energyUseBUILD1,
					cur.moneySpent + priceBUILD1);
			if(blockedwallState(state))
				state.blocked=true;
			if (!parent.pending) {
				return new Node(state, parent, Action.BUILD1, parent.depth + 1,parent.compareidx);
			} else {
				return updatePending(parent, state, Action.BUILD1);
			}

		case BUILD2:
			state = new State(cur.prosperity + prosperityBUILD2, cur.food - foodUseBUILD2,
					cur.materials - materialsUseBUILD2, cur.energy - energyUseBUILD2,
					cur.moneySpent + priceBUILD2);
			if(blockedwallState(state))
				state.blocked=true;
			if (!parent.pending) {
				return new Node(state, parent, Action.BUILD2, parent.depth + 1,parent.compareidx);
			} else {
				return updatePending(parent, state, Action.BUILD2);
			}

		default:
			return null;
		}

	}

	static void tokenizeInitState(String initState) {
		initState = initState.replace(';', ',');
		String[] Strvalues = initState.split(",");
		int[] values = new int[Strvalues.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = Integer.parseInt(Strvalues[i]);
		}

		rootState = new State(values[0], values[1], values[2], values[3], 0);
		priceFood = values[4];
		priceMaterials = values[5];
		priceEnergy = values[6];
		amountReqFood = values[7];
		delayReqFood = values[8];
		amountReqMaterials = values[9];
		delayReqMaterials = values[10];
		amountReqEnergy = values[11];
		delayReqEnergy = values[12];
		foodUseBUILD1 = values[14];
		materialsUseBUILD1 = values[15];
		energyUseBUILD1 = values[16];
		prosperityBUILD1 = values[17];
		priceBUILD1 = values[13] + (foodUseBUILD1 * priceFood)
				+ (materialsUseBUILD1 * priceMaterials) + (energyUseBUILD1 * priceEnergy);
		
		foodUseBUILD2 = values[19];
		materialsUseBUILD2 = values[20];
		energyUseBUILD2 = values[21];
		prosperityBUILD2 = values[22];
		priceBUILD2 = values[18] + (foodUseBUILD2 * priceFood)
				+ (materialsUseBUILD2 * priceMaterials) + (energyUseBUILD2 * priceEnergy);

		
	}

	static String printChain(Node cur) {
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

	static boolean goalTest(Node cur) {
		return cur.state.prosperity >= 100;
	}

	static boolean blockedwall(Node cur) {
		State state = cur.state;
	  return blockedwallState(state);
	}
	
	static boolean blockedwallState(State state) {
		
		if (state.moneySpent >= 100000 || state.food <= 0 || state.materials <= 0 || state.energy <= 0||state.blocked)
			return true;
		return false;
	}

	static Node updatePending(Node cur, State newState, Action a) {

		Node res = new Node(newState, cur, a, cur.depth + 1, cur.pendingFood, cur.pendingMaterials, cur.pendingEnergy,
				cur.timeToFood, cur.timeToMaterials, cur.timeToEnergy,cur.compareidx);

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
	
	static void reset() {
		foundIt = false;
		solution = "NOSOLUTION";
		nodesExpanded = 0;
	}

	public static void main(String args[]) {
		LLAPSearch l1 = new LLAPSearch();
		String init = "32;" +
				"20,16,11;" +
				"76,14,14;" +
				"9,1;9,2;9,1;" +
				"358,14,25,23,39;" +
				"5024,20,17,17,38;";
		
		//System.out.println(l1.solve(init, "DF", false));
		String sln=l1.solve(init, "DF", false);
		System.out.println(sln);
//		System.out.println(l1.solve(init, "UC", false));
//		System.out.println(l1.solve(init, "ID", false));
//		System.out.println(l1.solve(init, "GR1", false));
//		System.out.println(l1.solve(init, "GR2", false));
		String[] actions=(sln.split(";"))[0].split(",");
		System.out.println(Arrays.toString(actions));
		//LLAPPlanChecker checker=new LLAPPlanChecker(init);
		//System.out.println(checker.tryPlan(actions,checker ));
		
	}

}
