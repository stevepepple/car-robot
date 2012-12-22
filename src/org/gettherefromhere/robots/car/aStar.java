package org.gettherefromhere.robots.car;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.*;;

public class aStar {
	
	public boolean[][] blocks;
	public static int[][] heuristic;
	
	public int[][] paths; 
			
	public int length; 
	
	public int[] start = {0, 0};
	public int[] goal = {0, 0};
	
	
	/* Will the block make the grid un-navigable */
	private boolean clearPath(int x, int y) {
		
		/* Up, Right, Down, Left */
		boolean adj = true;
		boolean[] recent = {true, true, true, true};

		/* Check Up */
		if ((y - 1) < 0) {
			recent[0] = false;
		} else if (blocks[x][y - 1] == true) {
			recent[0] = false;
		}
		
		
		/* Check Right */
		if (x + 1 == length) {
			recent[1] = false;
		} else if (blocks[x + 1][y] == true) {
			recent[1] = false;
		}
		
		
		/* Check Down */
		if (y + 1 == length) {
			recent[2] = false;
		} else if (blocks[x][y + 1] == true) {
			recent[2] = false;
		}
		
		/* Check Left */
		if (x - 1 < 0) {
			recent[3] = false;
		} else if (blocks[x - 1][y] == true) {
			recent[2] = false;
		}
		
		
		try {
			if (x - 1 < 0 && y - 1 < 0) {
				adj = false;
			} else if (blocks[x - 1][y - 1] == true){
				adj = false;
			} else if (blocks[x][y - 1] == true) {
				adj = false;
			} else if (blocks[x - 1][y + 1] == true && blocks[x + 1][y - 1] == true) {
				adj = false;
			}
		} catch(ArrayIndexOutOfBoundsException err) {
			/* Do Nothing */
		}
				
		/* Up and Right are blocked; Or Up and Left; Or Left and Down */
		if (recent[0] == false && recent[1] == false) {
			return true;
		} else if (recent[0] == false && recent[3] == false) {
			return true;
		} else if (adj == false) { 
			return true;			
		} else {
			return false;	
		}
	}
	
	/* Height, Width, Length */
	private void createGrid(int length) {
		this.length = length;
		
		/* Initialize Grid Size */
		blocks = new boolean[length][length];
		/* Just initialize the set of paths */
		paths = new int[length][length];
		heuristic = new int[length][length];
		
		/* Set the goal */
		goal[0] = length - 1;
		goal[1] = length - 1;
		
		/* Generate optional paths */
		for (int x = 0; x < length; x++) {
			for (int y = 0; y < length; y++) {
				Random random = new Random();
				boolean blocker = random.nextBoolean();
				
				if (x <= 1 && y <= 1) {
					
				} else if (blocker == false || clearPath(x, y) == true) {
			    	/* Do nothing */
			    } else {
					blocks[x][y] = blocker;
			    }		    	

			}
		}

		/* Generate Heuristic */
		for (int x = 0; x < heuristic.length; x++) {
			for (int y = 0; y < heuristic.length; y++) {
				int value = (length -1 + length -1) - x - y ;
				heuristic[x][y] = value;
			}
		}
	}
	
	public class Point {
		public Integer cost;
		public Integer gVal;
		public int[] path;
		
	    public Point(Integer gVal, Integer cost, int[] path) {
	        this.cost=cost;
	        this.gVal=gVal;
	        this.path=path;
	    }
	}
	
	public static class SortPoints implements Comparator<Point> {
		@Override
	    public int compare(Point s, Point t) {
			return s.cost.compareTo(t.cost);
	    }
	}
	
	/* Update paths with the cost to goal */
	public void shortestPath() {
		
		/* Total Cost (goal value + heuristic), x, y */
		ArrayList<Point> open = new ArrayList<Point>();				
		open.add(new Point(0, 0, new int[]{0,0}));		

		int count = 0;
		int gVal = 0;
		boolean goalReached = false;
		boolean[][] closed = new boolean[length][length];
		
		int[] opsX = {0, +1, 0, -1};
		int[] opsY = ArrayUtils.clone(opsX);
		ArrayUtils.reverse(opsY);	

		while(goalReached == false && open.size() > 0) {
			
			int[] best = {0,0};
			
			Collections.sort(open, new SortPoints());	
			
			for (int i = 0; i < open.size(); i++) {
				System.out.print("option " + i + ": ");
				System.out.print(open.get(i).cost + " : ");
				System.out.print(Arrays.toString(open.get(i).path));
				System.out.print("\n");
			}
			System.out.print("----\n");

						
			best = open.get(0).path;
			gVal = open.get(0).gVal;
			gVal++;

			open.remove(0);
			
			if (open.size() == 0) {
				System.out.print("Out of options! \n");
			}
						
			for (int j = 0; j < opsX.length; j++) {
				int x2 = opsX[j] + best[0];
				int y2 = opsY[j] + best[1];
							
				if (goal[0] == x2 && goal[1] == y2) {
					goalReached = true;
					System.out.print("Goal reached! \n");
					paths[x2][y2] = gVal;
					break;
				}
				
				if (x2 >= 0 && x2 < paths.length && y2 >= 0 && y2 < paths.length) {
					if (closed[x2][y2] != true) {						
						if (blocks[x2][y2] == false && paths[x2][y2] <= paths[best[0]][best[1]]) {
							int cost = gVal + heuristic[x2][y2];
							
							open.add(new Point(gVal, cost, new int[]{x2,y2}));
							paths[x2][y2] = gVal;

						}
					}
				}
				closed[best[0]][best[1]] = true;
			}
		}
	}
	
	public aStar(int length) {
		createGrid(length);
		shortestPath();
	}
	
	public static void main(String[] args){
		new aStar(20);
	}
	
}

