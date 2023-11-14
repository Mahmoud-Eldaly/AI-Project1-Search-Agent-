package code;

import java.util.*;

public class LLAPSearch extends GenericSearch {

    public String strategy;

    public static String solve(String initialState, String strategy, boolean visualize) {
        LLAPSearch problem = new LLAPSearch();
        problem.strategy = strategy;
        reset();
        GenericSearch.initialState = new State(initialState);
        Node rootNode = new Node(GenericSearch.initialState, null, null, 0, 0);
        switch (strategy) {
            case "GR1" -> rootNode.compareIdx = 1;
            case "GR2" -> rootNode.compareIdx = 2;
            case "AS1" -> rootNode.compareIdx = 3;
            case "AS2" -> rootNode.compareIdx = 4;
        }

        problem.search(rootNode);


        String res = solution.equals("NOSOLUTION") ? "NOSOLUTION" : solution + ";" + nodesExpanded;
        if (visualize)
            while (!steps.isEmpty())
                System.out.println(steps.pop());
        return res;
    }

    Node operator(Node parent, Action operator) {
        State cur = parent.state;
        State state;
        Node next;


        switch (operator) {
            case RequestFood:
                state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
                        cur.moneySpent + State.priceFood + State.priceMaterials + State.priceEnergy, 0, State.delayReqFood);
                next = new Node(state, parent, Action.RequestFood, parent.depth + 1, parent.compareIdx);
                break;

            case RequestMaterials:
                ////// price resources here...
                state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
                        cur.moneySpent + State.priceFood + State.priceMaterials + State.priceEnergy, 1, State.delayReqMaterials);
                next =  new Node(state, parent, Action.RequestMaterials, parent.depth + 1, parent.compareIdx);
                break;

            case RequestEnergy:
                state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
                        cur.moneySpent + State.priceFood + State.priceMaterials + State.priceEnergy, 2, State.delayReqEnergy);
                next =  new Node(state, parent, Action.RequestEnergy, parent.depth + 1, parent.compareIdx);
                break;

            case WAIT:
                state = new State(cur.prosperity, cur.food - 1, cur.materials - 1, cur.energy - 1,
                        cur.moneySpent + State.priceFood + State.priceMaterials + State.priceEnergy, cur.deliverIdx, cur.deliverTime);
                if (parent.state.deliverIdx == -1) {
                    next =  new Node(state, parent, Action.WAIT, parent.depth + 1, parent.compareIdx);
                } else {
                    next =  updatePending(parent, state, Action.WAIT);
                }
                break;

            case BUILD1:
                state = new State(cur.prosperity + State.prosperityBUILD1, cur.food - State.foodUseBUILD1,
                        cur.materials - State.materialsUseBUILD1, cur.energy - State.energyUseBUILD1,
                        cur.moneySpent + State.priceBUILD1, cur.deliverIdx, cur.deliverTime);
                if (parent.state.deliverIdx == -1) {
                    next =  new Node(state, parent, Action.BUILD1, parent.depth + 1, parent.compareIdx);
                } else {
                    next =  updatePending(parent, state, Action.BUILD1);
                }
                break;

            case BUILD2:
                state = new State(cur.prosperity + State.prosperityBUILD2, cur.food - State.foodUseBUILD2,
                        cur.materials - State.materialsUseBUILD2, cur.energy - State.energyUseBUILD2,
                        cur.moneySpent + State.priceBUILD2, cur.deliverIdx, cur.deliverTime);
                if (parent.state.deliverIdx == -1) {
                    next =  new Node(state, parent, Action.BUILD2, parent.depth + 1, parent.compareIdx);
                } else {
                    //when pending
                    next =  updatePending(parent, state, Action.BUILD2);
                }
                break;
            default:
                return null;
        }

        if(blockedWall(next)){
            return null;
        }
        return next;
    }

    void queuing(Node cur, Collection list){
        for (Action act : Action.values()) {
            if ((cur.state.deliverIdx == -1 && act != Action.WAIT)
                    || ((cur.state.deliverIdx != -1 && act == Action.WAIT)
                    || act == Action.BUILD1
                    || act == Action.BUILD2)) {
                Node next = operator(cur, act);
                if (next != null && !expanded.contains(next.state)) {
                    list.add(next);
                    expanded.add(next.state);
                }
            }
        }
    }

    boolean goalTest(Node cur) {
        return cur.state.prosperity >= 100;
    }

    void search(Node initial){
        switch (strategy) {
            case "DF" -> solveDF(initial);
            case "BF" -> solveBF(initial);
            case "ID" -> solveID(initial);
            case "UC", "GR1", "GR2", "AS1", "AS2" -> solveUC(initial);
        }
    }

    void solveDF(Node initial) {
        Stack<Node> list = new Stack<>();
        list.add(initial);

        while (!list.isEmpty() && solution.equals("NOSOLUTION")) {
            expand(list.pop(), list);
        }
    }

    void solveBF(Node initial) {
        Queue<Node> list = new LinkedList<>();
        list.add(initial);

        while (!list.isEmpty() && solution.equals("NOSOLUTION")) {
            expand(list.remove(), list);
        }
    }

    void solveID(Node initial) {
        Stack<Node> list = new Stack<>();
        list.add(initial);

        for (int i = 0, maxDepth = 0; solution.equals("NOSOLUTION") && i <= maxDepth + 1; i++) {
            if (list.isEmpty())
                list.add(new Node(initialState, null, null, 0, 0));
            expanded = new HashSet<>();
            while (!list.isEmpty() && solution.equals("NOSOLUTION")) {
                Node top = list.pop();
                maxDepth = Math.max(maxDepth, top.depth);
                if (top.depth < i)
                    expand(top, list);
            }
        }
    }

    void solveUC(Node initial) {
        PriorityQueue<Node> list = new PriorityQueue<>();
        list.add(initial);

        while (!list.isEmpty() && solution.equals("NOSOLUTION")) {
            expand(list.remove(), list);
        }
    }

    boolean blockedWall(Node cur) {
        State state = cur.state;
        return blockedWallState(state);
    }

    boolean blockedWallState(State state) {
        return state.moneySpent >= 100000 || state.food <= 0 || state.materials <= 0 || state.energy <= 0;
    }

    static Node updatePending(Node cur, State newState, Action a) {

        Node res = new Node(newState, cur, a, cur.depth + 1, cur.compareIdx);

        if (newState.deliverTime > 0) {
            newState.deliverTime--;
        }
        if (newState.deliverTime == 0 && newState.deliverIdx != -1) {

            if (newState.deliverIdx == 0) {
                newState.food += State.amountReqFood;
                newState.food = Math.min(newState.food, 50);
            }
            if (newState.deliverIdx == 1) {
                newState.materials += State.amountReqMaterials;
                newState.materials = Math.min(newState.materials, 50);

            }
            if (newState.deliverIdx == 2) {
                newState.energy += State.amountReqEnergy;
                newState.energy = Math.min(newState.energy, 50);

            }
            newState.deliverIdx = -1;
            newState.deliverTime = Integer.MAX_VALUE;
        }

        return res;
    }

    static void reset() {
        initialState = null;
        solution = "NOSOLUTION";
        nodesExpanded = 0;
        steps = new Stack<>();
        expanded = new HashSet<>();
    }

    public static void main(String[] args) {
        String init = "50;" +
                "20,16,11;" +
                "76,14,14;" +
                "7,1;7,1;7,1;" +
                "359,14,25,23,39;" +
                "524,18,17,17,38;";

        System.out.println(solve(init, "AS1", true));
    }
}
