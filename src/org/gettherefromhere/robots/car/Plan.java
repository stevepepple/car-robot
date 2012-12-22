package org.gettherefromhere.robots.car;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import org.apache.commons.lang3.ArrayUtils;

public class Plan {
	
	public int cost;
	public Point start;
	public Point goal;
	
	public Grid grid; 
	public float[][] heuristic;
		
	/* Just initialize the set of paths */
	public int[][] paths;
	public ArrayList<Point> finalPath;
	public ArrayList<Point2D.Float> smoothPath;
	
    public Plan(Grid grid, Point start, Point goal) {
        this.grid = grid;
        this.start = start;  
        this.goal = goal;

        this.createHeuristic();
        this.aStar();
        /* dataWeight, smoothWeight, tolerance */
        this.smoothPath(.1F, .01F, 0.03F);
	}
        
    private void createHeuristic() {
 	   this.paths = new int[grid.points.length][grid.points.length];
	   this.heuristic = new float[paths.length][paths.length];
	   
	   /* Generate Heuristic */
	   for (int x = 0; x < heuristic.length; x++) {
			for (int y = 0; y < heuristic.length; y++) {
				//int value = (grid.length -1 + grid.length -1) - x - y;
				//heuristic[x][y] = value * 1.5F;
                this.paths[x][y] = 0;
                this.heuristic[x][y] = (float) Math.sqrt(Math.pow((float)x - (float) this.goal.x,2) + Math.pow((float)y - (float) this.goal.y,2));
			}
	   }
	   //System.out.println(Arrays.deepToString(heuristic));
	   
   }
   
	/* Update paths with the cost to goal */
	public void aStar() {
		
		class aPoint {
			public float h;
			public Integer g;
			public Float cost;
			public Point point; 

			public aPoint(Float cost, Integer g, float h2, Point p) {
		        this.cost = cost;
		        this.g = g;
		        this.h = h2;
		        this.point = p;
		    }
		}
		
		class sortPoints implements Comparator<aPoint> {
			public int compare(aPoint s, aPoint t) {
				return s.cost.compareTo(t.cost);
		    }
		}
			
		boolean goalReached = false;
		int count = 0;
		float h = this.heuristic[0][0];
		int g = 0;
		float cost = h + g;
		
		/* Total Cost (goal value + heuristic), x, y */
        ArrayList<aPoint> open = new ArrayList<aPoint>();
		open.add(new aPoint(cost, g, h, new Point(0, 0)));
		
		boolean[][] closed = new boolean[grid.points.length][grid.points[0].length];
		int[][] action = new int[grid.points.length][grid.points[0].length];
		
		closed[0][0] = true;
		
		/* Deltas */
		int[] opsX = {0, +1, 0, -1};
		int[] opsY = ArrayUtils.clone(opsX);
		ArrayUtils.reverse(opsY);

        int x2, y2, g2;
        float h2, cost2;

		while(goalReached == false && open.size() > 0) {

			Collections.sort(open, new sortPoints());			
						
			/* Best X and Y */
			Point best = new Point();
			g = open.get(0).g; 
			best.setLocation(open.get(0).point.x, open.get(0).point.y);	
						
			/* Pop */
			open.remove(0);
						
			if (open.size() == 0) System.out.print("Out of options! \n");
							
			for (int j = 0; j < opsX.length; j++) {
				
				x2 = opsX[j] + best.x;
				y2 = opsY[j] + best.y;
				
				if (best.x == goal.x && best.y == goal.y) {
					goalReached = true;
					System.out.print("Goal reached! \n");
					paths[x2][y2] = g;
					break;
				}
				
				if (x2 >= 0 && x2 < grid.points[0].length && y2 >= 0 && y2 < grid.points[0].length) {
					if (closed[x2][y2] != true && this.paths[x2][y2] == 0) {
						if (grid.blocks[x2][y2] == false && paths[x2][y2] == 0) {
							
							g2 = g + 1;
							h2 = this.heuristic[x2][y2];
							cost2 = g2 + h2;

							open.add(new aPoint(cost2, g2, h2, new Point(x2, y2)));
							
							paths[x2][y2] = g2;

                            closed[x2][y2] = true;
                            action[x2][y2] = j;

						}
					}
				} 
			}
			count +=1;
		}
		
		if (goalReached == true) {
			/* Extract Single Path */
			int x = goal.x;
			int y = goal.y;

			finalPath = new ArrayList<Point>();
			finalPath.add(new Point(x, y));
			
	        while (x != this.start.x || y != this.start.y) {
				x2 = x - opsX[action[x][y]];
				y2 = y - opsY[action[x][y]];
				
				x = x2;
				y = y2;
				finalPath.add(new Point(x, y));
	        }
	        Collections.reverse(finalPath);
		}
	}

	/* Smooth the final path
	 * A stronger weight results in a smoother path */
	public void smoothPath(float dataWeight, float smoothWeight, float tolerance) {
		
		this.smoothPath = new ArrayList<Point2D.Float>();		
        Point2D aux = new Point.Float();
                
        for (int i = 0; i < this.finalPath.size(); i++) {
            this.smoothPath.add( new Point.Float() );
            this.smoothPath.get(i).setLocation(this.finalPath.get(i).getX(), this.finalPath.get(i).getY());
        }
        
        float change = tolerance;
        
        while (change >= tolerance) {
        	change = 0.0F;
            
        	/* Minimize the different between points using gradient descent */
        	for (int i = 1; i < this.smoothPath.size() - 1; i++) {
                aux.setLocation(this.smoothPath.get(i).getX(), this.smoothPath.get(i).getY());
                
                float xCur = (float) this.smoothPath.get(i).getX();
                float yCur = (float) this.smoothPath.get(i).getY();
                float xFinal = (float) this.finalPath.get(i).getX();
                float yFinal = (float) this.finalPath.get(i).getY();
                
                float xSmooth = xCur + dataWeight * (xFinal - xCur);
                float ySmooth = yCur + dataWeight * (yFinal - yCur);
                                
                this.smoothPath.get(i).setLocation(xSmooth, ySmooth);                

                /* tighten the x and y paths */
                float xPrior = (float) (xCur + smoothWeight * (this.smoothPath.get(i-1).getX() + this.smoothPath.get(i + 1).getX() - 2 * xCur));
                float yPrior = (float) (yCur + smoothWeight * (this.smoothPath.get(i-1).getY() + this.smoothPath.get(i + 1).getY() - 2 * yCur));

                this.smoothPath.get(i).setLocation((float) xPrior, (float) yPrior);

                if (i >= 2) {     
                	float downX = (float) (this.smoothPath.get(i).getX() + 0.5 * smoothWeight * (2 * this.smoothPath.get(i - 1).getX() - this.smoothPath.get(i - 2).getX() - this.smoothPath.get(i).getX())); 
                	float downY = (float) (this.smoothPath.get(i).getY() + 0.5 * smoothWeight * (2 * this.smoothPath.get(i - 1).getY() - this.smoothPath.get(i - 2).getY() - this.smoothPath.get(i).getY()));
                    this.smoothPath.get(i).setLocation(downX, downY);
                }
                
                if (i <= (this.smoothPath.size() - 3)) {
                	float upX = (float) (this.smoothPath.get(i).getX() + 0.5 * smoothWeight * (2 * this.smoothPath.get(i + 1).getX() - this.smoothPath.get(i + 2).getX() - this.smoothPath.get(i).getX()));
                	float upY = (float) (this.smoothPath.get(i).getY() + 0.5 * smoothWeight * (2 * this.smoothPath.get(i + 1).getY() - this.smoothPath.get(i + 2).getY() - this.smoothPath.get(i).getY())); 
                    this.smoothPath.get(i).setLocation(upX , upY);
                }
                change += Math.abs(aux.getX() - this.smoothPath.get(i).getX());
                change += Math.abs(aux.getY() - this.smoothPath.get(i).getY());
        	}
        }
	}

}
