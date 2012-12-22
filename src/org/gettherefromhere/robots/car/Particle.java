package org.gettherefromhere.robots.car;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Random;

public class Particle {

	public Robot[] parts;
	public int number;
	Random random = new Random();
	
	public Particle(int number) {
		this.number = number;
		this.parts = new Robot[number];
		
	    for (int i = 0; i < this.parts.length; i++) {
	    	this.parts[i] = new Robot();
	    }
	}

	public Robot getPosition() {
		
      Robot tempBot= new Robot();
      tempBot.setLocation(0.0F, 0.0F, 0.0F);
	  
      float xNew = 0.0F;
	  float yNew = 0.0F; 
	  float orientNew = 0.0F;
      
      for (int i = 0; i < number; i++) {
    	  
    	  xNew += this.parts[i].x;
    	  yNew += this.parts[i].y;
    	  orientNew += (float) (((parts[i].orient - parts[0].orient + Math.PI) % (2.0 * Math.PI)) 
                  + parts[0].orient - Math.PI);
      }
      
      tempBot.setLocation(xNew / this.number, yNew / this.number, orientNew / this.number);
	  return tempBot;
	}
	
	public void set(Robot robot) {
	    for (int i = 0; i < this.parts.length; i++) {
	    	parts[i].setLocation(robot.x, robot.y, robot.orient);
	    } 
	}
		
	public void move(float steering, float distance, float tolerance) {
		
	    for (int i = 0; i < this.number; i++) {
	    	parts[i].move(steering, distance, tolerance);
	    }
	}
	
	/* Sense and resample */
	public void sense(Point2D.Float point) {
		
		/* Calculate important weight for each particle */
		float [] w = new float[this.number];
		
		for (int i = 0; i < this.number; i++) {
			w[i] = this.parts[i].measureProb(point);
		}

		/* Resample */
		Robot[] p3; 
        p3 = new Robot[number];
		int index = random.nextInt(number);
		float wMax = 0.0F;
		
        for (int i = 0; i < this.number; i++) {
        	if (w[i] > wMax) wMax = w[i];
        }
        
        float mw = wMax; 
        float beta = 0.0F;
        
		/* Resample particles */
        for (int i = 0; i < this.number; i++) {
			beta += random.nextInt() * 2.0F * mw;
			index = (index + 1) % this.number;
			p3[i] = new Robot();
			p3[i].setLocation(this.parts[index].x, this.parts[index].y, this.parts[index].orient);
		}
		
		for (int i = 0; i < this.number; i++) {
			this.parts[i].setLocation(p3[i].x, p3[i].y, p3[i].orient);
		}
	}
}
