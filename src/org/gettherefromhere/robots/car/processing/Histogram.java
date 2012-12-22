package org.gettherefromhere.robots.car;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

@SuppressWarnings("serial")
public class Histogram extends PApplet {

	int height = 200;
	int width = 300;
	int length = 5;
	int columnWidth = width / length;
	
	private List<Double> probs = new ArrayList<Double>();

	boolean isEven(int x) {
	  return (x % 2) == 0;
	}

	public void setup() {
	  
	  size(width + 50, height + 100);
	  background(255);
	  
	  /* Set up a background grid */
	  pushMatrix();
	  translate(25, 75);
	  
	  for (int i = 0; i < length; i++) {
	    noStroke();
	    if (isEven(i)) {
	    	fill(248);
	    	rect(i * columnWidth, 0, columnWidth, height);    
	    }
	  }
	  noLoop();  // Run once and stop
	}

	public void draw() {
		textFont(createFont("Helvetica", 14));

		localization bot = new localization();
		List<Double> probs = bot.sense(true);
		
		pushMatrix();
		translate(25, 80);

		for (int i = 0; i < probs.size(); i++) {
		    stroke(120, 120, 120);
		    /* Draw in the inverse direction and fractions 1 probability to percentages */
		    if (i == bot.position) {
			    fill(123, 230, 115);
		    } else {
			    fill(153, 204, 255);
		    }
		    rect(i * columnWidth, 150, columnWidth, (float) (- probs.get(i) * 100));
	    	fill(0);
		    text(i + 1, (i * columnWidth) + (columnWidth / 2), 200);
		    
		}
		popMatrix();
				
		String message = "After " + bot.numMoves + " moves the robot is at location " + Math.round(bot.position + 1) + " with a probability of " + Math.round(bot.belief * 100) + " percent.";
    	fill(0);
		text(message, 25, 25, width - 20, 40);		
	}
	
	public static void main(String _args[]) {
		PApplet.main(new String[] { Histogram.class.getName() });
	}
}
