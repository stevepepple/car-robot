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
			/* Account for the probability of under or overshooting */
			double sum = 0.0;
			
			/* Exact motion.
			int prev = mod((i - U), probs.size());
			sum = probs.get(prev);
			*/

			sum = pExact * probs.get(mod((i - U), probs.size()));
			sum = sum + pOver * probs.get(mod((i - U - 1),probs.size()));
			sum = sum + pUnder * probs.get(mod((i - U + 1), probs.size()));
			
			newProb.add(sum);
		}
		
		probs = newProb;
		return probs;
	}
 
	public localization() {
		createWorld();
		System.out.print(world + "\n");
		
		sense(world.get(0));
		move(1);
		sense(world.get(1));
		move(1);
		sense(world.get(2));
		move(1);
		sense(world.get(3));
	}
	
	public static void main(String[] args){
		new localization();
	}
	
}

