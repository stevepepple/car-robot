package org.gettherefromhere.robots.car;

import java.util.*;
import java.math.*;

public class Kalman {
	
	public static double mean = 0;
	public static double sigma = 10000.0;

	public static double maxGaus(double mu, double sigma2, int x) {
		double result = 0.0;
						
		System.out.print(1 / Math.sqrt(2.0 * Math.PI * sigma2) + "\n");
		System.out.print(Math.exp( -0.5 * Math.pow((x - mu), 2) / sigma2) + "\n");
		
		result = (1 / Math.sqrt(2.0 * Math.PI * sigma2)) * Math.exp( -0.5 * Math.pow((x - mu), 2) / sigma2 );
		
		return result;
	}

	// Move and update the new variance
	public static void update(double mean1, double var1, double mean2, double var2) {
		double newMean, newVar;

		newMean = (1 / (var1 + var2)) * (var2 * mean1 + var1 * mean2);
	    newVar = 1 / (1 / var1 + 1 / var2);

	    mean = newMean;
	    sigma = newVar;

	    System.out.print(mean + ", " + sigma + "\n");
	    /* TODO: Return Array */
	}
	
	public static void predict(double mean1, double var1, double mean2, double var2) {
		double newMean, newVar;
		
		newMean = mean1 + mean2;
		newVar = var1 + var2;

	    mean = newMean;
	    sigma = newVar;
	    
	    System.out.print(mean + ", " + sigma + "\n");
		
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		double[] measurements = {
			new Double(5.0),
			new Double(6.0),
			new Double(7.0),
			new Double(9.0),
			new Double(10.0)
		};
		
		double[] motion = {
			new Double(1.0),
			new Double(1.0),
			new Double(2.0),
			new Double(1.0),
			new Double(1.0)
		};
		
		double measurementSig = 4.0;
		double motionSig = 2.0;	
		
		//System.out.print(maxGaus(10.0, 4.0, 8));
		//predict(10, 4, 12, 4);
		
		update(1, 1, 1, 1);

		/*
		for(int k = 0; k < measurements.length; k++) {
			
			update(mean, sigma, measurements[k], measurementSig);
			
			predict(mean, sigma, motion[k], motionSig);

		} 
		*/

		System.out.print("Final result: " + mean + ", " + sigma + "\n");

	}

}
