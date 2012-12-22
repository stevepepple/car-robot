package org.gettherefromhere.robots.car;

import java.awt.Point;
import java.awt.geom.*;
import java.awt.geom.Point2D.Float;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.*;

public class Robot {
	
	/* Scale: 1 grid unit = 10 meters */
	public float x;
	public float y; 
	public float orient; // in radians 
	private float length; 
	public Random random = new Random();
	
	public float steeringNoise;
	public float distanceNoise; 
	public float measureNoise;
	private float maxSteerAngle; 

	public int collisions; 
	public int steps; 
	private boolean backup = false;
	private int numBackup = 0;

	public float pGain = 2.0F;
	public float dGain = 6.0F;
	
	public Particle particle;
	
	public ArrayList<Robot> eachMove;
	public int numSteps = 0;
	public int numCollisions = 0;
	
	public Robot() {
		this.setLength(25F); 
		this.x = 10; 
		this.y = 10;
		this.orient = 0;
		
		this.steeringNoise = 0.1F;
		this.distanceNoise = 0.03F; 
		this.measureNoise = 0.3F;

		this.setMaxSteerAngle((float) Math.PI / 4.0F); 
	}

	public float getLength() {
		return length;
	}

	private void setLength(float length) {
		this.length = length;
	}

	public float getMaxSteerAngle() {
		return maxSteerAngle;
	}

	public void setMaxSteerAngle(float maxSteerAngle) {
		this.maxSteerAngle = maxSteerAngle;
	}
	
    public void setLocation(float x, float y, float or) {
        this.x = x;
        this.y = y;
        this.orient = (float) (or % (20. * Math.PI));
    }
    
    public void setGoal(float x, float y) {
    	
    }

    public void setNoise(float dist, float steer, float meas) {
        this.distanceNoise = dist;
        this.steeringNoise = steer;
        this.measureNoise = meas;
    }
    
    /* Has the robot reached the goal */
    public boolean isGoal(Point goal) {
    	float threshold = 1.0F;
    	
    	float xDist = (float) Math.pow(goal.getX() - this.x, 2);
    	float yDist = (float) Math.pow(goal.getY() - this.y, 2);
    	
		float dist = (float) Math.sqrt(xDist + yDist);
		        
        if (dist < threshold) {
        	return true;
        } else {
        	return false;	
        }
    }
    
    public boolean isCollision(Grid grid) {
		float distance = 0.0F;
		boolean collision = false;
		
		for (int x = 0; x < grid.length; x ++) {
			for (int y = 0; y < grid.length; y++) {
				
				if (grid.blocks[x][y] == true) {
					float xDist =  (float) Math.pow(x - this.x, 2);
					float yDist =  (float) Math.pow(y - this.y, 2);
					distance = (float) Math.sqrt(xDist + yDist);
					
					if (distance < 0.5) {
						grid.collisions[x][y] = true;
						collision = true;
						this.numCollisions++;
					} else if (distance < 0.8) {
						this.backup = true;
					}
				}
			}
		}
		
		if (collision) {
			return true;
		} else {
			return false;
		}
    }
    
    /* Returns a random float between - 1 and 1 */
    public float randomNoiseRange() {
    	float randSigned = -1 * random.nextInt(2); 
    	float random = this.random.nextFloat();    	

    	if (randSigned != 0) {
    		random = random * randSigned;
    	}
		return random;
    }
    
    /* Main routine. Have the robot run the grid */
    public void run(Plan plan, float speed, int timeout, Point goal) {

        int number = 0;
    	
        float CTE = 0.0F;
        float error = 0.0F;
    	
        this.setLocation(0.0F, 0.0F, (float)Math.PI * 2.0F);
		eachMove = new ArrayList<Robot>();
        
        /* Create particle filter for this robot */
        this.particle = new Particle(500);
        this.particle.set(this);

        int index = 0;
        
        while(this.isGoal(goal) == false && number < timeout) {
        	
        	float diffCTE = - CTE;
        	Robot estimate = particle.getPosition();
        	
        	/* Proportional Controller */
            float dx = (float) (plan.smoothPath.get(index + 1).getX() - plan.smoothPath.get(index).getX());
            float dy = (float) (plan.smoothPath.get(index + 1).getY() - plan.smoothPath.get(index).getY());
            
            float rx = (float) (estimate.x - plan.smoothPath.get(index).getX());
            float ry = (float) (estimate.y - plan.smoothPath.get(index).getY());
            
            /* Projected estimate of robot location for the segment */
            float projection = (rx * dx + ry * dy) / (dx * dx + dy * dy);
            /* Crosstrack error is the estimate projected onto the normal of the path segment */
            CTE = (ry * dx - rx * dy) / (dx * dx + dy * dy);
            
            /* Jump to the next part of the path */
            if (projection > 1) index++;

        	diffCTE += CTE;

            float steer = - pGain * CTE - dGain * diffCTE;
            this.move(steer, speed, .001F);
            particle.move(steer, speed, .001F);
            
            particle.sense(this.sense());
            Robot newPos = particle.getPosition();
            
            this.setLocation(newPos.x, newPos.y, newPos.orient);
            
            this.eachMove.add( new Robot() );
            this.eachMove.get(number).setLocation(newPos.x, newPos.y, newPos.orient);

            number++;

            /* Keep track of error */
            error += Math.pow(CTE, 2);
        }
    }
    
    /* Move the robot 
     * 
     */ 
    public void move(float steering, float distance, float tolerance) {
    	float turn;
    	    	
    	if (steering > this.maxSteerAngle) 
    		steering = this.maxSteerAngle;
        if (steering < -this.maxSteerAngle) 
        	steering = -this.maxSteerAngle;
        if (distance < 0F) 
        	distance = 0F;
        
        float steerand = randomNoiseRange() * this.steeringNoise;
        float distrand = randomNoiseRange() * this.distanceNoise; 

        float steering2 = steering + steerand;
        float distance2 = distance + distrand;
        
        turn = (float) Math.tan(steering2) * distance2;
        
        float xNew = this.x;
        float yNew = this.y;
        float oNew = this.orient;
                        
    	/* Approximate a straight line 
         * If not straight, use the bicycle motion model */
        if (Math.abs(turn) < tolerance) { 
        	xNew += distance2 * (float) Math.cos(this.orient);
            yNew += distance2 * (float) Math.sin(this.orient);
            oNew = (this.orient + turn) % (2.0F * (float)Math.PI);
        } else if(backup == false) {
            float radius = distance2 / turn;
            float cx = this.x - ((float)Math.sin(this.orient) * radius);
            float cy = this.y + ((float)Math.cos(this.orient) * radius);            
            oNew = (this.orient + turn) % (2.0F * (float) Math.PI);
            
            xNew = cx + (float) Math.sin(oNew) * radius;
            yNew = cy - (float) Math.cos(oNew) * radius;
            
        } else {
        	System.out.println("Backing up...");
            xNew -= distance2 * (float) Math.cos(this.orient);
            yNew -= distance2 * (float) Math.sin(this.orient);
            numBackup++;
            if (numBackup < 7) {
                backup = false;
                numBackup = 0;
            }
        }

        this.setLocation(xNew, yNew, oNew);
    }

	public Point2D.Float sense() {
		
		float xGauss = this.measureNoise * (float) random.nextGaussian();
        float yGauss = this.measureNoise * (float) random.nextGaussian();
        
        Point2D.Float point = new Point2D.Float();        
        point.setLocation(this.x + xGauss, this.y + yGauss);
        
        return point;
	}
	
	/* Computes the probability of a measurement */ 
    public float measureProb(Point2D.Float measurement) {
        
    	float xError = (float) (measurement.getX() - this.x);
        float yError = (float) (measurement.getY() - this.y);
        
        /* Create Gaussian */
        float error = (float) (Math.pow(Math.E, - ((xError * xError) 
        		/ this.measureNoise * this.measureNoise) / 2.0) 
        		/ Math.sqrt(2.0 * Math.PI * this.measureNoise * this.measureNoise));
        error *= (float) (Math.pow(Math.E, -((yError * yError) 
        		/ (this.measureNoise * this.measureNoise) / 2.0)) 
        		/ Math.sqrt(2.0 * Math.PI * this.measureNoise * this.measureNoise));
        return error;
    }
	
}

