package cs373;
import java.util.*;

public class localization {
	
	List<Double> probs = new ArrayList<Double>();
	private int steps = 10;
	private List<Boolean> world = new ArrayList<Boolean>();
	
	final double pHit = 0.6;
	final double pMiss = 0.2;
	final double pos = 1.0;
	
	private void createWorld(){
		/* Create a Even Probability Distribution */
		for (int i = 0; i < steps; i++){
			double dist = pos / steps;
			probs.add(dist);	    	
		}
		
		/* Create a Random World*/		
	    for(int i = 0; i < probs.size(); i++){
	        Random rand = new Random();
	        world.add(rand.nextBoolean());
	    }
	}
	
	// Z = sensed location is a landmark
	public List<Double> sense(boolean Z) {
		double sum = 0;
		
		/* Update the probabilities based on sensed location */
		for (int i = 0; i < probs.size(); i++) {
			boolean hit = (Z == world.get(i));
			
			double p = probs.get(i);
			
			if (hit == true) {
				probs.set(i, p * pHit);
				
			} else {
				probs.set(i, p * pMiss);
			}
			sum = sum + probs.get(i);
		}
		
		
		/* Normalize the probabilities */
		for (int i = 0; i < probs.size(); i++) {
			probs.set(i, probs.get(i) / sum);
		}
		
		return probs;
	}
	
	// U = number of steps
	public List<Double> move(int U) {

		double pExact = 0.8;
	    /* Add overshoot probability */
	    double pOver = 0.1;
	    double pUnder = 0.1;
	    List<Double> newProb = new ArrayList();
	    
		for (int i = 0; i < probs.size(); i++) {
			int exact = (i - U) % probs.size();
			int over = (i - U - 1) % probs.size();
			int under = (i - U + 1) % probs.size();
			
			double sum = 0.0;
			/* Exact move */
			if (exact < 0) {
				sum += pExact * probs.get(probs.size() + exact);	
			} else {
				sum += pExact * probs.get(exact);
			}
			
			if (over < 0) {
				sum += pOver * probs.get(probs.size() + over);	
			} else {
				sum += pOver * probs.get(over);
			}

			if (under < 0) {
				sum += pUnder * probs.get(probs.size() + under);	
			} else {
				sum += pUnder * probs.get(under);			
			}

			
			probs.set(i, sum);
			return probs;
		}
		
		//sense(true);
		//sense(false);
		
		return newProb;
	}
 
	public localization() {
		createWorld();
		System.out.print(world);
		System.out.print("\n");
		System.out.print(sense(world.get(0)));
		System.out.print("\n");
		System.out.print(move(1));
		System.out.print("\n");
		System.out.print(sense(world.get(1)));
		System.out.print("\n");
	}
	
	public static void main(String[] args){
		new localization();
	}
	
}

