package code;

import java.util.*;

public abstract class GenericSearch {

    static State initialState;

    static int nodesExpanded = 0;

    static HashSet<State> expanded = new HashSet<>();

    static String solution = "NOSOLUTION";

    static Stack<String> steps = new Stack<>();

    abstract boolean goalTest(Node curNode);

    abstract Node operator(Node curNode, Action action);

    abstract void search(Node initial);

    abstract void queuing(Node cur, Collection list);

    String getSolution(Node cur) {
        StringBuilder res = new StringBuilder(";" + cur.state.moneySpent);
        while (cur.parent != null) {
            steps.push(cur.state.toString());
            res.insert(0, cur.operator + ",");
            cur = cur.parent;
        }

        steps.push(initialState.toString());
        return res.toString();
    }

    void expand(Node cur, Collection list) {
        nodesExpanded++;
        if (goalTest(cur)) {
            solution = getSolution(cur);
            return;
        }

        queuing(cur, list);
    }
}
