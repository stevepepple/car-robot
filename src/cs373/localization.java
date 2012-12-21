package cs373;
import java.util.*;

public class localization {
	
	List<Double> probs = new ArrayList<Double>();
	private int steps = 5;
	public int numMoves = 4;
	private List<Boolean> world = new ArrayList<Boolean>();
	public double position = 1.0;
	public double belief = 0.0;
	
	final double pHit = 0.6;
	final double pMiss = 0.2;
	
	private void createWorld(){
		/* Create a Even Probability Distribution */
		for (int i = 0; i < steps; i++){
			double dist = position / steps;
			probs.add(dist);	    	
		}
		
		/* Create a Random World */		
	    for(int i = 0; i < probs.size(); i++){
	        Random rand = new Random();
	        world.add(rand.nextBoolean());
	    }
	}
	
	public int mod(int a, int b) {
		int result = a % b; 
		if (result < 0) {
			result += b;
		}
		return result; 
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
		
		/* Reset stored best position */
		belief = 0.0;
		this.position = 1.0;
		
		/* Normalize the probabilities */
		for (int i = 0; i < probs.size(); i++) {
			double prob = probs.get(i) / sum;
			probs.set(i, prob);
			
		    if (prob > this.belief) {
		    	belief = probs.get(i);
		    	this.position = i;
		    }
			
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
			/* Account for the probability of under or overshooting */
			double sum = 0.0;
			
			/* Exact motion. 
			int prev = mod((i - U), probs.size()); */
			int prev = 0;
			if (i > 0) {
				prev = i - U;
			} else {
				prev = i;
			}
			sum = probs.get(prev);

			/*
			sum = pExact * probs.get(mod((i - U), probs.size()));
			sum = sum + pOver * probs.get(mod((i - U - 1),probs.size()));
			sum = sum + pUnder * probs.get(mod((i - U + 1), probs.size()));
			*/
			
			newProb.add(sum);
		}
		
		probs = newProb;
		return probs;
	}
 
	public localization() {
		createWorld();
		
		for (int i = 0; i <= numMoves; i++) {
			sense(world.get(i));
			move(1);
			sense(world.get(i));
		}
		//System.out.print(world + "\n");
		//System.out.print(world.get(2));
//		move(1);
//		sense(world.get());
//		move(1);
//		sense(world.get(3));
	}
	
	public static void main(String[] args){
		new localization();
	}
	
}

