import java.util.*;

public class LLAPSearch extends GenericSearch {

	static State rootState;
	static boolean foundIt = false;
	static String solution = "NOSOLUTION";
	static int nodesExpanded = 0;
	static Stack<String> steps = new Stack<String>();

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
		//reset();
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
		nodesExpanded++;
		if (cur.state.blocked)
			nodesExpanded--;
		if (blockedwall(cur) || cur.depth == limit)
			return;
		if (goalTest(cur)) {
			foundIt = true;
			solution = printChain(cur);
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
		} else if (list instanceof PriorityQueue) {
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
					cur.moneySpent + cur.priceFood + cur.priceMaterials + cur.priceEnergy);
			return new Node(state, parent, Action.RequestFood, parent.depth + 1, true, false, false, cur.delayReqFood,
					0, 0, parent.compareidx);
		case RequestMaterials:
			////// price resoures here...
			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
					cur.moneySpent + cur.priceFood + cur.priceMaterials + cur.priceEnergy);
			return new Node(state, parent, Action.RequestMaterials, parent.depth + 1, false, true, false, 0,
					cur.delayReqMaterials, 0, parent.compareidx);
		case RequestEnergy:
			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
					cur.moneySpent + cur.priceFood + cur.priceMaterials + cur.priceEnergy);
			return new Node(state, parent, Action.RequestEnergy, parent.depth + 1, false, false, true, 0, 0,
					cur.delayReqEnergy, parent.compareidx);
		case WAIT:
			state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
					cur.moneySpent + cur.priceFood + cur.priceMaterials + cur.priceEnergy);
			if (!parent.pending) {
				return new Node(state, parent, Action.WAIT, parent.depth + 1, parent.compareidx);
			} else {
				return updatePending(parent, state, Action.WAIT);
			}
		case BUILD1:
			state = new State(cur.prosperity + cur.prosperityBUILD1, cur.food - cur.foodUseBUILD1,
					cur.materials - cur.materialsUseBUILD1, cur.energy - cur.energyUseBUILD1,
					cur.moneySpent + cur.priceBUILD1);
			if (blockedwallState(state))
				state.blocked = true;
			if (!parent.pending) {
				return new Node(state, parent, Action.BUILD1, parent.depth + 1, parent.compareidx);
			} else {
				return updatePending(parent, state, Action.BUILD1);
			}

		case BUILD2:
			state = new State(cur.prosperity + cur.prosperityBUILD2, cur.food - cur.foodUseBUILD2,
					cur.materials - cur.materialsUseBUILD2, cur.energy - cur.energyUseBUILD2,
					cur.moneySpent + cur.priceBUILD2);
			if (blockedwallState(state))
				state.blocked = true;
			if (!parent.pending) {
				return new Node(state, parent, Action.BUILD2, parent.depth + 1, parent.compareidx);
			} else {
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

		Node res = new Node(newState, cur, a, cur.depth + 1, cur.pendingFood, cur.pendingMaterials, cur.pendingEnergy,
				cur.timeToFood, cur.timeToMaterials, cur.timeToEnergy, cur.compareidx);

		if (cur.timeToFood > 0) {
			res.timeToFood = cur.timeToFood - 1;
			if (res.timeToFood == 0) {
				res.state.food += cur.state.amountReqFood;
				res.state.food = Math.min(res.state.food, 50);
				res.pendingFood = false;
			}
		}
		if (cur.timeToMaterials > 0) {
			res.timeToMaterials = cur.timeToMaterials - 1;
			if (res.timeToMaterials == 0) {
				res.state.materials += cur.state.amountReqMaterials;
				res.state.materials = Math.min(res.state.materials, 50);
				res.pendingMaterials = false;
			}
		}
		if (cur.timeToEnergy > 0) {
			res.timeToEnergy = cur.timeToEnergy - 1;
			if (res.timeToEnergy == 0) {
				res.state.energy += cur.state.amountReqEnergy;
				res.state.energy = Math.min(res.state.energy, 50);
				res.pendingEnergy = false;
			}
		}
		res.pending = res.pendingFood || res.pendingMaterials || res.pendingEnergy;

		return res;
	}

	static void reset() {
		rootState=null;
		foundIt = false;
		solution = "NOSOLUTION";
		nodesExpanded = 0;
		steps = new Stack<String>();
	}

	public static void main(String args[]) {
		LLAPSearch l1 = new LLAPSearch();
		String init = "30;" +
                "30,25,19;" +
                "90,120,150;" +
                "9,2;13,1;11,1;" +
                "3195,11,12,10,34;" +
                "691,7,8,6,15;";

		// System.out.println(l1.solve(init, "DF", false));
		String sln = l1.solve(init, "ID", true);
		System.out.println(sln);
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
