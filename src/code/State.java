package code;

import java.util.Objects;

public class State {
    int prosperity;
    int food, materials, energy;
    int moneySpent;

    int deliverIdx = -1;
    int deliverTime = Integer.MAX_VALUE;

    /////////////////////////
    static int priceBUILD1, foodUseBUILD1, materialsUseBUILD1, energyUseBUILD1, prosperityBUILD1;
    static int priceBUILD2, foodUseBUILD2, materialsUseBUILD2, energyUseBUILD2, prosperityBUILD2;
    static int priceFood, priceMaterials, priceEnergy;
    static int amountReqFood, delayReqFood;
    static int amountReqMaterials, delayReqMaterials;
    static int amountReqEnergy, delayReqEnergy;
    /////
    boolean blocked = false;

    public State(String initState) {
        // can we call instance . static variable???

        initState = initState.replace(';', ',');
        String[] Strvalues = initState.split(",");
        int[] values = new int[Strvalues.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = Integer.parseInt(Strvalues[i]);
        }
        prosperity = values[0];
        food = values[1];
        materials = values[2];
        energy = values[3];
        moneySpent = 0;
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
        priceBUILD1 = values[13] + (foodUseBUILD1 * priceFood) + (materialsUseBUILD1 * priceMaterials)
                + (energyUseBUILD1 * priceEnergy);

        foodUseBUILD2 = values[19];
        materialsUseBUILD2 = values[20];
        energyUseBUILD2 = values[21];
        prosperityBUILD2 = values[22];
        priceBUILD2 = values[18] + (foodUseBUILD2 * priceFood) + (materialsUseBUILD2 * priceMaterials)
                + (energyUseBUILD2 * priceEnergy);

    }

    public State(int p, int f, int m, int e, int mSpent, int dIdx, int time) {
        prosperity = p;
        food = f;
        materials = m;
        energy = e;
        moneySpent = mSpent;
        deliverIdx = dIdx;
        deliverTime = time;
    }

    public String toString() {
        String resource = "No resource";
        switch(deliverIdx) {
            case 0:
                resource = "Food";
                break;
            case 1:
                resource = "Materials";
                break;
            case 2:
                resource = "Energy";
                break;
            default:
                resource = "No resource";
        }

        return "code.State={Prosperity:" + prosperity + ", Food:" + food + ", Materials:" + materials + ", Energy:" + energy
                + ", Spent money:" + moneySpent + ",   Waiting resource:" + resource + ",   Time to arrive:" + ((deliverTime == Integer.MAX_VALUE) ? "Infinity" : deliverTime) + "}";
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        State state = (State) o;
        return (this.prosperity == state.prosperity &&
                this.food == state.food &&
                this.materials == state.materials &&
                this.energy == state.energy &&
                this.moneySpent == state.moneySpent &&
                this.deliverIdx == state.deliverIdx &&
                this.deliverTime == state.deliverTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prosperity, food, materials, energy, moneySpent, deliverIdx, deliverTime);
    }

}

/*
 * initialP rosperity; initialF ood, initialM aterials, initialEnergy; unitP
 * riceF ood, unitP riceM aterials, unitP riceEnergy; amountRequestF ood,
 * delayRequestF ood; amountRequestM aterials, delayRequestM aterials;
 * amountRequestEnergy, delayRequestEnergy; priceBUILD1, foodUseBUILD1,
 * materialsUseBUILD1, energyUseBUILD1, prosperityBUILD1; priceBUILD2,
 * foodUseBUILD2, materialsUseBUILD2, energyUseBUILD2, prosperityBUILD1
 *
 */
