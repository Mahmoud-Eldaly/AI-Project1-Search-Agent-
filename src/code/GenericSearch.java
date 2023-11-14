package code;

import java.util.*;

public abstract class GenericSearch {

    static State initialState;

    static int nodesExpanded = 0;

    static HashSet<State> expanded = new HashSet<State>();

    static String solution = "NOSOLUTION";

    static Stack<String> steps = new Stack<String>();

    abstract boolean goalTest(Node curNode);

    abstract Node operator(Node curNode, Action action);

    abstract void search(Node initial);

    abstract void queuing(Node cur, Collection list);

    String getSolution(Node cur) {
        String res = ";" + cur.state.moneySpent;
        while (cur.parent != null) {
            steps.push(cur.state.toString());
            res = cur.operator + "," + res;
            cur = cur.parent;
        }
        // res=res+";"+
        steps.push(initialState.toString());
        return res;
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
