import java.util.*;

public class LLAPSearch extends GenericSearch {

	static State rootState;
	static boolean foundIt = false;
	static String solution = "NOSOLUTION";
	static int nodesExpanded = 0;
	static Stack<String> steps = new Stack<String>();
	static HashSet<State> expanded=new HashSet<State>();
	public LLAPSearch() {

	}

	public static String solve(String initalState, String strategy, boolean visualize) {
		// boolean condition;
		reset();
		rootState = new State(initalState);
		Node rootNode = new Node(rootState, null, null, 0, 0);
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
			rootNode.compareidx = 1;
			((PriorityQueue<Node>) list).add(rootNode);
			break;
		case "GR2":
			list = new PriorityQueue<Node>();
			rootNode.compareidx = 2;
			((PriorityQueue<Node>) list).add(rootNode);
			break;
		case "AS1":
			list = new PriorityQueue<Node>();
			rootNode.compareidx = 3;
			((PriorityQueue<Node>) list).add(rootNode);
			break;
		case "AS2":
			list = new PriorityQueue<Node>();
			rootNode.compareidx = 4;
			((PriorityQueue<Node>) list).add(rootNode);
			break;

		default:
			list = new PriorityQueue<Node>();

		}

		if (strategy.equals("DF"))
			solveDF(list);
		else if (strategy.equals("BF"))
			solveBF(list);
		else if (strategy.equals("ID"))
			solveID(list);
		else if (strategy.equals("UC") || strategy.equals("GR1") || strategy.equals("GR2") || strategy.equals("AS1")
				|| strategy.equals("AS2"))
			solveUC(list);

		String res = solution.equals("NOSOLUTION") ? "NOSOLUTION" : solution + ";" + nodesExpanded;
		if (visualize)
			while (!steps.isEmpty())
				System.out.println(steps.pop());
		// reset();
		return res;
	}

	static void solveDF(Object list) {
		while (!((Stack<Node>) list).isEmpty() && !foundIt) {
			expand(((Stack<Node>) list).pop(), list, Integer.MAX_VALUE);
		}
	}

	static void solveBF(Object list) {
		while (!((LinkedList<Node>) list).isEmpty() && !foundIt) {
			
			expand(((LinkedList<Node>) list).remove(), list, Integer.MAX_VALUE);
		}
	}

	static void solveID(Object list) {
		for (int i = 0; !foundIt; i++) {
			if (((Stack<Node>) list).isEmpty())
				((Stack<Node>) list).push(new Node(rootState, null, null, 0, 0));
			expanded=new HashSet<State>();
			while (!((Stack<Node>) list).isEmpty() && !foundIt) {
				expand(((Stack<Node>) list).pop(), list, i);
			}

		}
	}

	static void solveUC(Object list) {
		while (!((PriorityQueue<Node>) list).isEmpty() && !foundIt) {
			expand(((PriorityQueue<Node>) list).remove(), list, Integer.MAX_VALUE);
		}
	}

	static void expand(Node cur, Object list, int limit) {
		System.out.println(cur.depth+"    "+nodesExpanded);
		nodesExpanded++;
		if (cur.state.blocked)
			nodesExpanded--;
		if (blockedwall(cur) || cur.depth == limit)
			return;
		if (goalTest(cur)) {
//			System.out.println();
//			System.out.println("found it !!!!!!!!!!!!!!!!!!!!!");
			System.out.println(cur.depth+"///////////////////////////////////////////////////////////////////");
			foundIt = true;
			solution = printChain(cur);
			return;
		}
		expanded.add(cur.state);

		if (list instanceof Stack) {
			if(cur.depth<2) {
			System.out.println(cur.state);
			System.out.println(cur.state.deliverIdx+"      "+cur.operator+"        "+cur.state.deliverTime+"      depth="+cur.depth);
			}
			for (Action act : Action.values()) {
				if ((cur.state.deliverIdx == -1 && act != Action.WAIT)
						|| ((cur.state.deliverIdx != -1 && act == Action.WAIT)
								|| act == Action.BUILD1
								|| act == Action.BUILD2)) {
					Node next = generate(cur, act);
					
					if (next != null&&!expanded.contains(next.state))
						((Stack<Node>) list).push(next);
				}
			}
		} else if (list instanceof LinkedList) {
			//System.out.println(cur.state);
			//System.out.println(cur.state.deliverIdx+"      "+cur.operator+"        "+cur.state.deliverTime+"      depth="+cur.depth);
			for (Action act : Action.values()) {
				if ((cur.state.deliverIdx == -1 && act != Action.WAIT)
						|| ((cur.state.deliverIdx != -1 && act == Action.WAIT)
								|| act == Action.BUILD1
								|| act == Action.BUILD2)) {
//					if(cur.depth<5)
//						System.out.println(cur.state.deliverIdx+"      "+act+"        "+cur.state.deliverTime+"      depth="+cur.depth);
					Node next = generate(cur, act);
					if (next != null&&!expanded.contains(next.state))
						((LinkedList<Node>) list).add(next);
				}
			}
		} else if (list instanceof PriorityQueue) {
			for (Action act : Action.values()) {
				if((cur.state.deliverIdx == -1 && act != Action.WAIT)
						|| ((cur.state.deliverIdx != -1 && act == Action.WAIT)
								|| act == Action.BUILD1
								|| act == Action.BUILD2)) {
					Node next = generate(cur, act);
					if (next != null&&!expanded.contains(next.state))
						((PriorityQueue<Node>) list).add(next);
				}

			}
		}

	}

	static Node generate(Node parent, Action operator) {
		//System.out.println(11);
		State cur = parent.state;
		State state;
		
		switch (operator) {
		case RequestFood:

			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
					cur.moneySpent + cur.priceFood + cur.priceMaterials + cur.priceEnergy,0,cur.delayReqFood);
			return new Node(state, parent, Action.RequestFood, parent.depth + 1, parent.compareidx);
		case RequestMaterials:
			////// price resoures here...
			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
					cur.moneySpent + cur.priceFood + cur.priceMaterials + cur.priceEnergy,1,cur.delayReqMaterials);
			return new Node(state, parent, Action.RequestMaterials, parent.depth + 1, parent.compareidx);
		case RequestEnergy:
			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
					cur.moneySpent + cur.priceFood + cur.priceMaterials + cur.priceEnergy,2,cur.delayReqEnergy);
			return new Node(state, parent, Action.RequestEnergy, parent.depth + 1, parent.compareidx);
		case WAIT:
			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
					cur.moneySpent + cur.priceFood + cur.priceMaterials + cur.priceEnergy,cur.deliverIdx,cur.deliverTime);
			if (parent.state.deliverIdx == -1) {
				return new Node(state, parent, Action.WAIT, parent.depth + 1, parent.compareidx);
			} else {
				return updatePending(parent, state, Action.WAIT);
			}
		case BUILD1:
			//System.out.println("b11          pros:"+cur.prosperity+"        prosb1:"+cur.prosperityBUILD1);
			state = new State(cur.prosperity + cur.prosperityBUILD1, cur.food - cur.foodUseBUILD1,
					cur.materials - cur.materialsUseBUILD1, cur.energy - cur.energyUseBUILD1,
					cur.moneySpent + cur.priceBUILD1,cur.deliverIdx,cur.deliverTime);
			//if(parent.depth<5)
			//	System.out.println(state+",,,,,,,,,,,,,"+cur);
			if (blockedwallState(state))
				return null;
			//System.out.println("cont...");
			if (parent.state.deliverIdx == -1) {
				return new Node(state, parent, Action.BUILD1, parent.depth + 1, parent.compareidx);
			} else {
				return updatePending(parent, state, Action.BUILD1);
			}

		case BUILD2:
			//System.out.println("b22");
			state = new State(cur.prosperity + cur.prosperityBUILD2, cur.food - cur.foodUseBUILD2,
					cur.materials - cur.materialsUseBUILD2, cur.energy - cur.energyUseBUILD2,
					cur.moneySpent + cur.priceBUILD2,cur.deliverIdx,cur.deliverTime);
			if (blockedwallState(state))
				return null;
			if (parent.state.deliverIdx == -1) {
				return new Node(state, parent, Action.BUILD2, parent.depth + 1, parent.compareidx);
			} else {
				//when pending
				return updatePending(parent, state, Action.BUILD2);
			}

		default:
			return null;
		}

	}

	static String printChain(Node cur) {
		String res = ";" + cur.state.moneySpent;
		while (cur.parent != null) {
			steps.push(cur.state.toString());
			// System.out.println(cur.state);
			res = cur.operator + "," + res;
			cur = cur.parent;
		}
		// res=res+";"+
		steps.push(rootState.toString());
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

		if (state.moneySpent >= 100000 || state.food <= 0 || state.materials <= 0 || state.energy <= 0 || state.blocked)
			return true;
		return false;
	}

	static Node updatePending(Node cur, State newState, Action a) {

		Node res = new Node(newState, cur, a, cur.depth + 1, cur.compareidx);

		if (newState.deliverTime > 0) {
			newState.deliverTime--;
//			System.out.println("after update, time="+newState.deliverTime);
		}
		if (newState.deliverTime == 0 && newState.deliverIdx != -1) {

			if (newState.deliverIdx == 0) {
				newState.food += newState.amountReqFood;
				newState.food = Math.min(newState.food, 50);
			}
			if (newState.deliverIdx == 1) {
				newState.materials += newState.amountReqMaterials;
				newState.materials = Math.min(newState.materials, 50);

			}
			if (newState.deliverIdx == 2) {
				newState.energy += newState.amountReqEnergy;
				newState.energy = Math.min(newState.energy, 50);

			}
			newState.deliverIdx = -1;
			newState.deliverTime = Integer.MAX_VALUE;
		}

		return res;
	}

	static void reset() {
		rootState = null;
		foundIt = false;
		solution = "NOSOLUTION";
		nodesExpanded = 0;
		steps = new Stack<String>();
		expanded=new HashSet<State>();
	}

	public static void main(String args[]) {
		LLAPSearch l1 = new LLAPSearch();
		String init =   "32;" +
				"20,16,11;" +
				"76,14,14;" +
				"9,1;9,2;9,1;" +
				"358,14,25,23,39;" +
				"5024,20,17,17,38;";
//		HashSet< State>hSet=new HashSet<State>();
//		hSet.add(new State(20, 30, 40, 80, 155692, 40, 40));
//		hSet.add(new State(20, 30, 40, 80, 155692, 41, 40));
//		hSet.add(new State(20, 30, 40, 80, 155692, 41, 40));
//		hSet.add(new State(20, 3, 40, 80, 155692, 40, 40));
//
//	
//		System.out.println(hSet.size());
		// System.out.println(l1.solve(init, "DF", false));
		//String sln = l1.solve(init, "BF", false);
		//System.out.println(sln);
//		System.out.println(l1.solve(init, "UC", false));
//		System.out.println(l1.solve(init, "ID", false));
//		System.out.println(l1.solve(init, "GR1", false));
//		System.out.println(l1.solve(init, "GR2", false));
//		String[] actions = (sln.split(";"))[0].split(",");
//		System.out.println(Arrays.toString(actions));
		// LLAPPlanChecker checker=new LLAPPlanChecker(init);
		// System.out.println(checker.tryPlan(actions,checker ));

	}

}
