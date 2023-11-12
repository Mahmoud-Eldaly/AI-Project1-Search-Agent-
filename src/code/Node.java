package code;

public class Node implements Comparable {
    State state;
    Node parent;
    Action operator;
    int depth;
    int compareidx = 0;

    public Node(State s, Node p, Action a, int d, int cidx) {
        state = s;
        parent = p;
        operator = a;
        depth = d;
        compareidx = cidx;

    }

    public int h1() {
        return Math.max(0, (100 - this.state.prosperity));
    }

    public int h2() {
        // not centered?!--> goal.h2()!=0???
        // need to be done...
        return (this.h1() + this.state.moneySpent);
    }

    public int admH1() {
        // Money for build
        double neededBuildMoneyPerPros = Math.min(this.state.priceBUILD1 * 1.0 / this.state.prosperityBUILD1,
                this.state.priceBUILD2 * 1.0 / this.state.prosperityBUILD2);
        int neededBuildMoney = (int) (Math.max(0, (100 - this.state.prosperity)) * neededBuildMoneyPerPros);

        return neededBuildMoney;
    }

    public int admH2() {
        // Money for resources
        double neededFoodPerPros = Math.min(this.state.foodUseBUILD1 * 1.0 / this.state.prosperityBUILD1,
                this.state.foodUseBUILD2 * 1.0 / this.state.prosperityBUILD2);

        double neededMaterialsPerPros = Math.min(this.state.materialsUseBUILD1 * 1.0 / this.state.prosperityBUILD1,
                this.state.materialsUseBUILD2 * 1.0 / this.state.prosperityBUILD2);

        double neededEnergyPerPros = Math.min(this.state.energyUseBUILD1 * 1.0 / this.state.prosperityBUILD1,
                this.state.energyUseBUILD2 * 1.0 / this.state.prosperityBUILD2);

        int minRequestFood = (int) Math.ceil(Math.max(0.0,
                (100.0 - this.state.prosperity - this.state.food / neededFoodPerPros)) / this.state.amountReqFood);
        int minRequestMaterials = (int) Math.ceil(Math.max(0.0,
                (100.0 - this.state.prosperity - this.state.materials / neededMaterialsPerPros)) / this.state.amountReqMaterials);
        int minRequestEnergy = (int) Math.ceil(Math.max(0.0,
                (100.0 - this.state.prosperity - this.state.energy / neededEnergyPerPros)) / this.state.amountReqEnergy);

        int neededResourcesMoney = (minRequestFood + minRequestMaterials + minRequestEnergy) *
                (this.state.priceFood + this.state.priceMaterials + this.state.priceEnergy);

        return neededResourcesMoney;
    }

    public int f1() {
        return this.state.moneySpent + admH1();
    }

    public int f2() {
        return this.state.moneySpent + admH1() + admH2();
    }

    @Override
    public int compareTo(Object o) {
        if (compareidx == 1)
            return this.h1() - ((Node) o).h1();
        else if (compareidx == 2)
            return this.h2() - ((Node) o).h2();
        else if (compareidx == 3)
            return this.f1() - ((Node) o).f1();
        else if (compareidx == 4)
            return this.f2() - ((Node) o).f2();

        return this.state.moneySpent - ((Node) o).state.moneySpent;
    }
}
