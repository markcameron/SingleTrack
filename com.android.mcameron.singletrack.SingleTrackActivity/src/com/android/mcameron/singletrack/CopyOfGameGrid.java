package com.android.mcameron.singletrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Mark Cameron <mark.cameron.dev@gmail.com>
 *
 */
public class CopyOfGameGrid {
	ArrayList <float[]>drawnLines = new ArrayList<float[]>();
	ArrayList <float[]>fixedLines = new ArrayList<float[]>();
	ArrayList <float[]>guideLines = new ArrayList<float[]>();
	ArrayList <float[]>drawnAndFixedLines = new ArrayList<float[]>();
	
    private float[] gridPts;
    private float[] startPoint = new float[2];
    private float[] endPoint = new float[2];
    private float[] closestPoints = new float[4];

	private int gridSize;
	private int gridSizeX;
	private int gridSizeY;
	private int gridShape;
    private int gridSpacing;
    private int gridPadding;
    
    private static final int GRID_SHAPE_TRIANGLE  = 1;
    private static final int GRID_SHAPE_SQUARE    = 2;
    private static final int GRID_SHAPE_RECTANGLE = 3;
    
    private boolean isFadingLine;
    
	/**
     * Initialize the GameGrid, setting size, shape, view padding and scaling.
     * 
     * @param gridSize
     * @param gridSpacing
     */
    CopyOfGameGrid(int gridShape, int displayWidth, int displayHeight, float[] levelConfig) {
    	setGridSizeX((int)levelConfig[0]);
    	setGridSizeY((int)levelConfig[1]);
    	setGridPadding(displayWidth, displayHeight);
    	setGridSize(gridSizeX, gridSizeY);
    	setGridShape(gridShape);
    	setGridSpacing(displayWidth, displayHeight);
    	setFadingLine(false);
    	createGrid();
    	setupLevel(levelConfig);
    }
    
	public float[] getStartPoint() {
		return startPoint;
	}

	private void setStartPoint(float[] startPoint) {
		this.startPoint[0] = startPoint[0] * gridSpacing + gridPadding;
		this.startPoint[1] = startPoint[1] * gridSpacing + gridPadding;
	}

	public float[] getEndPoint() {
		return endPoint;
	}

	private void setEndPoint(float[] endPoint) {
		this.endPoint[0] = endPoint[0] * gridSpacing + gridPadding;
		this.endPoint[1] = endPoint[1] * gridSpacing + gridPadding;
	}
	
	public ArrayList<float[]> getFixedLines() {
		return fixedLines;
	}
	
	/**
	 * Convert the guideLines ArrayList to a simple array, readable by drawLine
	 * 
	 * @return array of points
	 */
	public float[] getGuideLinesArray() {
		int i = 0;
		float[] guideLinesArray = new float[guideLines.size()*4];
		for (Iterator<float[]> iterator = guideLines.iterator(); iterator.hasNext();) {
			float[] line = iterator.next();
			guideLinesArray[i++] = line[0];
			guideLinesArray[i++] = line[1];
			guideLinesArray[i++] = line[2];
			guideLinesArray[i++] = line[3];
		}
		return guideLinesArray;
	}
	
	/**
	 * Convert the fixedLines ArrayList to a simple array, readable by drawLine
	 * 
	 * @return array of points
	 */
	public float[] getFixedLinesArray() {
		int i = 0;
		float[] fixedLinesArray = new float[fixedLines.size()*4];
		for (Iterator<float[]> iterator = fixedLines.iterator(); iterator.hasNext();) {
			float[] line = iterator.next();
			fixedLinesArray[i++] = line[0];
			fixedLinesArray[i++] = line[1];
			fixedLinesArray[i++] = line[2];
			fixedLinesArray[i++] = line[3];
		}
		
		return fixedLinesArray;
	}

	public int getGridPadding() {
		return gridPadding;
	}

	public void setGridPadding(int width, int height) {
		int gridPadding = (int)(width*0.2)/2;
		this.gridPadding = gridPadding;
	}

	public float[] getmPts() {
		return gridPts;
	}
	
	public ArrayList<float[]> getDrawnLines() {
		return drawnLines;
	}
	
	public int getGridSize() {
		return gridSize;
	}

	public void setGridSize(int gridSizeX, int gridSizeY) {
		this.gridSize = gridSizeX * gridSizeY;
	}

	public int getGridSizeX() {
		return gridSizeX;
	}

	public void setGridSizeX(int gridSizeX) {
		this.gridSizeX = gridSizeX;
	}

	public int getGridSizeY() {
		return gridSizeY;
	}

	public void setGridSizeY(int gridSizeY) {
		this.gridSizeY = gridSizeY;
	}
	
	public int getGridShape() {
		return gridShape;
	}

	public void setGridShape(int gridShape) {
		this.gridShape = gridShape;
	}

	public int getGridSpacing() {
		return gridSpacing;
	}

	public void setGridSpacing(int width, int height) {
		if (gridSizeX >= gridSizeY) {
			this.gridSpacing = (width-gridPadding*2)/(gridSizeX-1);
		}
		else {
			this.gridSpacing = (height-gridPadding*2)/(gridSizeY-1);
		}
	}
	
    public float[] getClosestPoints() {
		return closestPoints;
	}
    
    public boolean isFadingLine() {
		return isFadingLine;
	}

	public void setFadingLine(boolean isFadingLine) {
		this.isFadingLine = isFadingLine;
	}

    /**
     * Helper function to initialize the array with all the points for the grid.
     */
	private void createGrid() {
		switch (gridShape) {
			case GRID_SHAPE_SQUARE:
			case GRID_SHAPE_RECTANGLE:
				createGridSquare();
				setGuideLines();
				break;
		}
	}
	
	/**
	 * Function that will setup the points for a square grid.
	 */
	private void createGridSquare() {
		gridPts = new float[gridSize*2];
		for (int i = 0; i < gridSizeY; i++) {
        	for (int j = 0; j < gridSizeX; j++) {
        		gridPts[(2*((gridSizeX*i)+j))] = gridPadding+((gridSpacing*j));
        		gridPts[(2*((gridSizeX*i)+j))+1] = gridPadding+((gridSpacing*i));
        	}
		}
	}
	
	/**
	 * Does all the setup for the level.
	 *  - Scales the level points to the grid size
	 *  - Populates the ArrayLists with the fixed lines.
	 * @param levelConfig
	 */
	private void setupLevel(float[] levelConfig) {
		this.setStartPoint(new float[]{levelConfig[2], levelConfig[3]});
		this.setEndPoint(new float[]{levelConfig[4], levelConfig[5]});
		for (int i = 6; i < levelConfig.length; i++) {
			if (i % 4 == 2) {
				float[] tempArray = new float[4];
				tempArray[0] = ((levelConfig[i] * gridSpacing)) + gridPadding;
				tempArray[1] = ((levelConfig[i+1] * gridSpacing)) + gridPadding;
				tempArray[2] = ((levelConfig[i+2] * gridSpacing)) + gridPadding;
				tempArray[3] = ((levelConfig[i+3] * gridSpacing)) + gridPadding;
				fixedLines.add(tempArray);
				drawnAndFixedLines.add(tempArray.clone());
			}
		}
	}
	
	/**
	 * Generates the points for the horizontal and vertical lines that
	 * make up the dashed background to tell the user where they can touch
	 */
	private void setGuideLines() {
		for (int i = 0; i < gridSizeX; i++) {
			float[] tempArray = new float[4];
			tempArray[0] = 0 + gridPadding;
			tempArray[1] = (i * gridSpacing) + gridPadding;
			tempArray[2] = ((gridSizeX-1) * gridSpacing) + gridPadding;
			tempArray[3] = tempArray[1];
			guideLines.add(tempArray);
		}
		
		for (int i = 0; i < gridSizeY; i++) {
			float[] tempArray = new float[4];
			tempArray[0] = (i * gridSpacing) + gridPadding;
			tempArray[1] = 0 + gridPadding;
			tempArray[2] = tempArray[0];
			tempArray[3] = ((gridSizeY-1)   * gridSpacing) + gridPadding;
			guideLines.add(tempArray);
		}
	}
	
    /**
     * Finds the closest point to where the user touched
     * 
     * @param x
     * @param y
     */
	private void findClosestPoint(float x, float y) {
		float distX = 10000;
    	float distY = 10000;
    	
    	// Find and set the closest point on the grid to where user touched
    	for (int i = 0; i <= gridPts.length-1; i++) {
    		if (i % 2 == 0) {
	    		if (distX > Math.abs(x - gridPts[i])) {
	    			closestPoints[0] = gridPts[i];
	    			distX = Math.abs(x - gridPts[i]);
				}
			}
    		else {
	    		if (distY > Math.abs(y - gridPts[i])) {
					closestPoints[1] = gridPts[i];
					distY = Math.abs(y - gridPts[i]);
				}
    		}
    	}
	}
	
	/**
	 * Finds the point to draw the line to.
	 * 
	 * @param x
	 * @param y
	 */
	private void findDirectionPoint(float x, float y) {
		// Calculate the angle in radians between x axis, and the line draw from the touch point to closest grid point
    	float delta_x = x - closestPoints[0];
    	float delta_y = y - closestPoints[1];
    	float theta_radians = (float) Math.atan2(delta_y, delta_x);
    	
    	// Draw line according to calculated angle
    	// < - Draw to the right
    	if ((theta_radians > -Math.PI/4) && (theta_radians < Math.PI/4)) {
			closestPoints[2] = closestPoints[0] + gridSpacing;
			closestPoints[3] = closestPoints[1];
		}
    	// \/ - Draw up 
    	else if ((theta_radians > Math.PI/4) && (theta_radians < (3*Math.PI)/4)) {
			closestPoints[2] = closestPoints[0];
			closestPoints[3] = closestPoints[1] + gridSpacing;
		}
    	// > - Draw left
    	else if ((theta_radians > (3*Math.PI)/4) && (theta_radians < (5*Math.PI)/4)) {
			closestPoints[2] = closestPoints[0] - gridSpacing;
			closestPoints[3] = closestPoints[1];
		}
    	// /\ - Draw down
    	else {
    		closestPoints[2] = closestPoints[0];
			closestPoints[3] = closestPoints[1] - gridSpacing;
    	}
	}
    
	/**
	 * Finds the 2 points on either side of where the user touched
	 * 
	 * @param x
	 * @param y
	 */
    public void findClosestPoints(float x, float y) {
    	findClosestPoint(x, y);
    	findDirectionPoint(x, y);    	
    }
    
    /**
     * Returns how many lines leave/enter the point
     * 
     * @param x
     * @param y
     * 
     * @return The number of lines entering/leaving the point
     */
    private int countLinesAtPoint(float x, float y) {
    	int count = 0;
  	  	for (Iterator<float[]> iterator = drawnAndFixedLines.iterator(); iterator.hasNext();) {
			float[] points = iterator.next();
			if ((points[0] == x && points[1] == y) || (points[2] == x && points[3] == y)) {
				count++;
			}
		}
  	  	return count;
    }
    
    /**
     * Checks if the line we're trying to draw is between two valid points on the grid.
     * Not shooting off from the grid to a point outside it.
     * 
     * @param line
     * 
     * @return TRUE if point is inside the grid, FALSE otherwise
     */
    private boolean isOutsideGrid(float[] line) {
    	for (int i = 0; i <= gridPts.length-1; i++) {
    		// Only check the point we're going to, departure point must exist.
    		if (i % 2 == 0) {
    			if ((line[2] == gridPts[i]) && (line[3] == gridPts[i+1])) {
    				return false;
    			}
    		}
    	}
    	
    	return true;
    }
    
    /**
     * Checks if the line will intersect an existing line.
     * 
     * Used to make sure we only draw a single line, not one with branches.
     * (No T junctions)
     * 
     * @param line
     * 
     * @return TRUE if the line doesn't intersect, otherwise FALSE
     */
    private int isIntersection(float[] line) {
    	int pointOne = countLinesAtPoint(line[0], line[1]);
    	int pointTwo = countLinesAtPoint(line[2], line[3]);

    	if ((pointOne == 1) && (pointTwo == 1)) {
			return 2;	
		}
		if ((pointOne <= 1) && (pointTwo <= 1)) {
			return 1;
		}
    	
    	return 0;
    }
    
    /**
     * Checks if the current point is the Start point
     * 
     * @param x
     * @param y
     * @return
     * TRUE if it is the Start point, otherwise FALSE
     */
    private boolean isStartPoint(float x, float y) {
    	if (x == startPoint[0] && y == startPoint[1]) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * Checks if the current point is the Finish point
     * 
     * @param x
     * @param y
     * @return
     * TRUE if it is the Finish point, otherwise FALSE
     */
    private boolean isEndPoint(float x, float y) {
    	if (x == endPoint[0] && y == endPoint[1]) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * Checks if the line being drawn goes to/leaves from
     * either the start or finish points.
     * 
     * @param line
     * @return
     * TRUE if we do visit start/finish, otherwise FALSE
     */
    public boolean lineVisitsStartFinish(float[] line) {
    	if (isStartPoint(line[0], line[1]) || isStartPoint(line[2], line[3])) {
			return true;
		}
    	if (isEndPoint(line[0], line[1]) || isEndPoint(line[2], line[3])) {
			return true;
		}

    	return false;
    }
    
    /**
     * Checks if the line can be drawn.
     * 
     * @param line
     * 
     * @return TRUE if the line can be drawn, otherwise FALSE
     */
    public boolean canBeDrawn(float[] line) {
    	float[] linePts = new float[]{line[2], line[3], line[2], line[3], -1.0f};
    	
    	if (isOutsideGrid(line)) {
			return false;
		}
    	
    	// Check if the line will close a loop
    	int result = isIntersection(line);
    	if (result == 2) {
    		return lineIsNotClosed(line[0], line[1], linePts);
		}
    	else if (result == 0) {
    		return false;
    	}
    	
    	return true;
    }
    
    /**
     * Helper function to check if float[] array is contained by the ArrayList.
     * 
     * @param list
     * @param arr
     * 
     * @return TRUE if contained, otherwise FALSE
     */
    private boolean contains(ArrayList<float[]> list, float[] arr) {
    	int n = list.size();
    	for (int i = 0; i < n; i++) {
    		if (Arrays.equals(list.get(i), arr)) {
    			return true;
    		}
    	}
    	return false;
	}
    
    /**
     * Checks if the line goes through every point (level solved)
     * 
     * @return
     * TRUE if the line is complete, FALSE otherwise
     */
    private boolean lineIsComplete() {
    	for (int i = 0; i < gridPts.length-1; i++) {
			if (i % 2 == 0) {
				if (isStartPoint(gridPts[i], gridPts[i+1]) || isEndPoint(gridPts[i], gridPts[i+1])) {
					continue;
				}
				
				if (countLinesAtPoint(gridPts[i], gridPts[i+1]) < 2) {
					return false;
				}
			}
		}
    	
    	return true;
    }
    
    /**
     * Checks if the player has solved the level.
     * 
     * @return
     * TRUE if the level is solved, FALSE otherwise
     */
    public boolean levelIsSolved() {
    	if (lineIsComplete()) {
			return true;
		}
    	return false;
    }
    
    /**
     * Helper function to swap the lines direction (e.g. x1 with x2, and y1 with y2)
     * 
     * @param line
     * @return
     * The new float array of the swapped line.
     */
    private float[] swapLineDirection(float[] line) {
    	float[] rLine = new float[4];
    	rLine[0] = line[2];
    	rLine[1] = line[3];
    	rLine[2] = line[0];
    	rLine[3] = line[1];
    	return rLine;
    }

    /**
     * Helper function to remove a float[] array from the ArrayList
     * 
     * @param line
     */
    @SuppressWarnings("unchecked")
	private boolean deleteLine(float[] line) {
    	// Clone original, clear original, and copy all over back to the
    	// original, except one to delete.
    	ArrayList<float[]> cloneLines = new ArrayList<float[]>();
    	Object objA = drawnLines.clone();
    	if (objA instanceof ArrayList) {
    		cloneLines = (ArrayList<float[]>) drawnLines.clone();
    	} else {
    		return false;
    	}
    	
    	ArrayList<float[]> clonedDrawnAndFixedLines = new ArrayList<float[]>();
    	Object objB = drawnAndFixedLines.clone();
    	if (objB instanceof ArrayList) {
    		clonedDrawnAndFixedLines = (ArrayList<float[]>) drawnAndFixedLines.clone();
    	} else {
    		return false;
    	}
    	
    	float[] rLine = swapLineDirection(line);
    	
    	drawnAndFixedLines.clear();
    	for (Iterator<float[]> iterator = clonedDrawnAndFixedLines.iterator(); iterator.hasNext();) {
			float[] fLine = iterator.next();
			if (Arrays.equals(fLine, line) || Arrays.equals(fLine, rLine)) {
    			continue;
    		}
			drawnAndFixedLines.add(fLine);
		}
    	
    	drawnLines.clear();
    	boolean result = false;
    	for (Iterator<float[]> iterator = cloneLines.iterator(); iterator.hasNext();) {
			float[] fLine = iterator.next();
			if (Arrays.equals(fLine, line) || Arrays.equals(fLine, rLine)) {
				result = true;
    			continue;
    		}
			drawnLines.add(fLine);
		}
    	return result;
  	}
    
    /**
     * Checks if the line where to used touched already exists.
     * 
     * Check for line starting at A and starting at B since it's only 
     * stored in one direction in the ArrayList.
     * 
     * @param needle
     * 
     * @return TRUE if the line has already been drawn, FALSE otherwise
     */
    public boolean isAlreadyDrawn(float[] needle) {
    	float[] fNeedle = needle.clone(); // Line from A to B
    	float[] rNeedle = needle.clone(); // Line from B to A
    	rNeedle[0] = fNeedle[2];
    	rNeedle[1] = fNeedle[3];
    	rNeedle[2] = fNeedle[0];
    	rNeedle[3] = fNeedle[1];
    	if (contains(drawnLines, fNeedle) || contains(drawnLines, rNeedle)) {
    		return true;
		}

    	return false;
    }
    
    /**
     * Checks if the line is part of the fixedLine ArrayList, as opposed 
     * to the drawnLine ArrayList.
     * 
     * @param needle
     * @return
     * TRUE if it is present in the List, otherwise FALSE
     */
    public boolean isFixedLine(float[] needle) {
    	float[] fNeedle = needle.clone(); // Line from A to B
    	float[] rNeedle = needle.clone(); // Line from B to A
    	rNeedle[0] = fNeedle[2];
    	rNeedle[1] = fNeedle[3];
    	rNeedle[2] = fNeedle[0];
    	rNeedle[3] = fNeedle[1];
    	if (contains(fixedLines, fNeedle) || contains(fixedLines, rNeedle)) {
    		return true;
		}
    	
    	return false;
    }
    
    /**
     * Helper function to add a line to drawnLines
     * 
     * @param line
     * 
     * @return TRUE if line was added, FALSE otherwise
     */
    public boolean addLine(float[] line) {
    	drawnAndFixedLines.add(line.clone());
    	return drawnLines.add(line.clone());
    }
    
    /**
     * Helper function to remove a line from drawnLines
     * 
     * @param line
     * 
     * @return TRUE if line was removed, FALSE otherwise
     */
    public boolean removeLine(float[] line) {	
    	boolean result = deleteLine(line);
    	
    	return result;
    }
    
    /**
     * Helper function to compare 2 points on the grid.
     * 
     * @param xStart
     * @param yStart
     * @param xCurrent
     * @param yCurrent
     * @return
     * TRUE if the points are the same, otherwise FALSE
     */
    private boolean samePoint(float xStart, float yStart, float xCurrent, float yCurrent) {
    	if ((xStart == xCurrent) && (yStart == yCurrent)) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * This function will parse the list of points to find out if we have a line leaving 
     * from the point were at that isn't the point we were on before.
     * 
     * @param lPts
     * The list of points already drawn.
     * @return
     * TRUE if there is another line from this point.
     */
    private boolean followLine(float[] lPts) {
//    	Log.d("lineFollow", "Starting from: (" + lPts[0] + ","+ lPts[1] +") to (" + lPts[2] + ","+ lPts[3] +")");
  	  	for (Iterator<float[]> iterator = drawnAndFixedLines.iterator(); iterator.hasNext();) {
			float[] points = iterator.next();
//			Log.d("lineFollow", "Current Line in list: (" + points[0] + ","+ points[1] +") to (" + points[2] + ","+ points[3] +")");
			if ((points[0] == lPts[0] && points[1] == lPts[1]) && !samePoint(lPts[2], lPts[3], points[2], points[3])) {
//				Log.d("lineFollow", "Returned: top | Points: [newgoto]" + points[2] + ","+ points[3] +" -- ("+ lPts[0] + ","+ lPts[1] +") to ("+ lPts[2] +","+ lPts[3] +") | Depth: "+ lPts[4] +")");
				lPts[0] = points[2];
				lPts[1] = points[3];
				lPts[2] = points[0];
				lPts[3] = points[1];
				return true;
			}
			if ((points[2] == lPts[0] && points[3] == lPts[1]) && !samePoint(lPts[2], lPts[3], points[0], points[1])) {
//				Log.d("lineFollow", "Returned: bottom | Points: [newgoto]" + points[0] + ","+ points[1] +" -- ("+ lPts[0] + ","+ lPts[1] +") to ("+ lPts[2] +","+ lPts[3] +") | Depth: "+ lPts[4] +")");
				lPts[0] = points[0];
				lPts[1] = points[1];
				lPts[2] = points[2];
				lPts[3] = points[3];
				return true;
			}
		}
  	  	
  	  	return false;
    }
    
    /**
     * Recursive function that will go from the current "to" point on the line 
     * and follow it until it ends to see if we make a closed loop.
     * 
     * @param xStart
     * Starting X coordinate
     * @param yStart
     * Starting Y coordinate
     * @param lPts
     * float[] of the current point, and the point we just came from.
     * @return
     * FALSE if the line returns to the starting point. TRUE otherwise.
     */
    public boolean lineIsNotClosed(float xStart, float yStart, float[] lPts) {
    	float x = lPts[0];
    	float y = lPts[1];
    	lPts[4]++;
    	boolean ifResult = followLine(lPts);
    	if (samePoint(xStart, yStart, x, y)) {
//    		Log.d("lineFollow", "Returned 1: false | Points: (" + lPts[0] + ","+ lPts[1] +") to ("+ lPts[2] +","+ lPts[3] +") | Depth: "+ lPts[4] +")");
    		return false;			
		}    	
    	else if (ifResult) {
//    		Log.d("lineFollow", "Checking: (" + lPts[0] + ","+ lPts[1] +") Coming From: ("+ lPts[2] +","+ lPts[3] +") | Depth: "+ lPts[4] +")");
    		boolean result = lineIsNotClosed(xStart, yStart, lPts);
//    		Log.d("lineFollow", "Returned 2: "+ Boolean.toString(result) +" | Points: (" + lPts[0] + ","+ lPts[1] +") to ("+ lPts[2] +","+ lPts[3] +") | Depth: "+ lPts[4] +")");
    		return result;
    	}
    	else {
//    		Log.d("lineFollow", "Returned 3: true  | Points: (" + lPts[0] + ","+ lPts[1] +") to ("+ lPts[2] +","+ lPts[3] +") | Depth: "+ lPts[4] +")");
    		return true;
    	}
    }
}
