package org.gettherefromhere.robots.car;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Random;

public class Grid {
	
	public int length; 
	public Point2D[][] points; 
	public boolean[][] blocks;
	public boolean[][] collisions;
	
	/* Grab from Robot? */
	public Point start = new Point();
    public Point goal = new Point();

	public Grid(int length) {
		this.length = length;
		this.blocks = new boolean[length][length];
		this.collisions = new boolean[length][length];

		points = new Point2D[length][length];

		/* Set the start and goal at simple defaults*/
		start.setLocation(0, 0);
		goal.setLocation(length -1, length -1);

		create();
	}

	private void create() {
		
		for (int x = 0; x < length; x++) {
			for (int y = 0; y < length; y++) {
				/* TODO: Move to scale constant */
				points[x][y] = new Point(x, y);
			}
		}	

		for (int x = 0; x < length; x++) {
			for (int y = 0; y < length; y++) {
				Random random = new Random();
				boolean blocker = random.nextBoolean();
				
				if ((x <= 1 && y <= 1) || (x == length - 1 && y == length - 1)) {
			    	/* Start or End - Do nothing */
				} else if (blocker == false || clearPoint(x, y) == true) {
			    	/* Do nothing */
			    } else {
					blocks[x][y] = blocker;
			    }		    	

			}
		}	
	}
	
	/* Will the block make the grid un-navigable */
	private boolean clearPoint(int x, int y) {
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
}
