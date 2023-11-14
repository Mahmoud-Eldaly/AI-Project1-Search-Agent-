package code;

public class Node implements Comparable {
    State state;
    Node parent;
    Action operator;
    int depth;
    int compareIdx;

    public Node(State s, Node p, Action a, int d, int cIdx) {
        state = s;
        parent = p;
        operator = a;
        depth = d;
        compareIdx = cIdx;
    }

    public int admH1() {
        // Money for build
        double neededBuildMoneyPerPros = Math.min(State.priceBUILD1 * 1.0 / State.prosperityBUILD1,
                State.priceBUILD2 * 1.0 / State.prosperityBUILD2);
        // Needed build money
        return (int) (Math.max(0, (100 - this.state.prosperity)) * neededBuildMoneyPerPros);
    }

    public int admH2() {
        // Money for resources request
        double neededFoodPerPros = Math.min(State.foodUseBUILD1 * 1.0 / State.prosperityBUILD1,
                State.foodUseBUILD2 * 1.0 / State.prosperityBUILD2);

        double neededMaterialsPerPros = Math.min(State.materialsUseBUILD1 * 1.0 / State.prosperityBUILD1,
                State.materialsUseBUILD2 * 1.0 / State.prosperityBUILD2);

        double neededEnergyPerPros = Math.min(State.energyUseBUILD1 * 1.0 / State.prosperityBUILD1,
                State.energyUseBUILD2 * 1.0 / State.prosperityBUILD2);

        int minRequestFood = (int) Math.ceil(Math.max(0.0,
                (100.0 - this.state.prosperity - this.state.food / neededFoodPerPros)) / State.amountReqFood);
        int minRequestMaterials = (int) Math.ceil(Math.max(0.0,
                (100.0 - this.state.prosperity - this.state.materials / neededMaterialsPerPros)) / State.amountReqMaterials);
        int minRequestEnergy = (int) Math.ceil(Math.max(0.0,
                (100.0 - this.state.prosperity - this.state.energy / neededEnergyPerPros)) / State.amountReqEnergy);

        // Needed resources request money
        return (minRequestFood + minRequestMaterials + minRequestEnergy) *
                (State.priceFood + State.priceMaterials + State.priceEnergy);
    }

    public int f1() {
        return this.state.moneySpent + admH1();
    }

    public int f2() {
        return this.state.moneySpent + admH1() + admH2();
    }

    @Override
    public int compareTo(Object o) {
        if (compareIdx == 1)
            return this.admH1() - ((Node) o).admH1();
        else if (compareIdx == 2)
            return this.admH2() - ((Node) o).admH2();
        else if (compareIdx == 3)
            return this.f1() - ((Node) o).f1();
        else if (compareIdx == 4)
            return this.f2() - ((Node) o).f2();

        return this.state.moneySpent - ((Node) o).state.moneySpent;
    }
}
