
public class State {
	int prosperity;
	int food,materials,energy;
	int moneySpent;
	
	public State(int p,int f, int m, int e,int mSpent) {
		prosperity=p;
		food=f;
		materials=m;
		energy=e;
		moneySpent=mSpent;
	}
	
	public String toString() {
		return "State={pros:"+prosperity+", food:"+food+", mat:"+materials+", enery:"+energy+", spennt:"+moneySpent;
	}
	

}

/*
 * initialP rosperity;
initialF ood, initialM aterials, initialEnergy;
unitP riceF ood, unitP riceM aterials, unitP riceEnergy;
amountRequestF ood, delayRequestF ood;
amountRequestM aterials, delayRequestM aterials;
amountRequestEnergy, delayRequestEnergy;
priceBUILD1, foodUseBUILD1,
materialsUseBUILD1, energyUseBUILD1, prosperityBUILD1;
priceBUILD2, foodUseBUILD2,
materialsUseBUILD2, energyUseBUILD2, prosperityBUILD1
 * 
 */
