package backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/*
 * This class holds a 2D array of <air> objects of size (x,y) as given in the constructor. 
 * it defaults to all 0's unless changed manually.
 */
public class windmap {
	private final double globalDeltaHeat = 2;
	
	private final int mySizeX;
	private final int mySizeY;
	private air[][] myMap;
	
	/*
	 * creates a new windmap with size (x) by (y) dimensions. 
	 * if either is negative it makes them positive.
	 */
	public windmap (int theX, int theY) {
		if (theX < 2 || theY < 2)
			throw new IllegalArgumentException("size must be at least 2 in all dimensions");
		myMap = new air[Math.abs(theX)][Math.abs(theY)];
		
		mySizeX = Math.abs(theX);
		mySizeY = Math.abs(theY);
		
		// we make sure all the <air> objects are created (defaults to (0,0) for now).
		for (int i = 0; i < ((int) mySizeX-1); i++) {
			for (int j = 0; j < ((int) mySizeY-1); j++) {
				myMap[i][j] = new air(0,0,0);
			}
		}
	}
	
	public double getTotalEnergy() {
		double total = 0;
        for (int i = 0; i < mySizeX-1; i++) {
        	for (int j = 0; j < mySizeY-1; j++) {
        		total += myMap[i][j].getTemp();
        	}
        }
        return total;
	}
	
	/*
	 * This method allows the user to set the temperature of points in this map. 
	 * does nothing if the given point doesn't exist.
	 */
	public void setTemp(int x, int y, double vH) {
		if (checkLoc(x, y)) {
			myMap[x][y].setTemp(vH);
			return;
		} else {
			return;
		}
	}
	
	public void createMapFile(final String filePath, String fileName) {
	    try (PrintWriter writer = new PrintWriter(new File(fileName))) {

	        StringBuilder sb = new StringBuilder();
	        
	        for (int i = 0; i < mySizeX-1; i++) {
	        	for (int j = 0; j < mySizeY-1; j++) {
	        		sb.append(myMap[i][j].getTemp());
	        		sb.append(",");
	        	}
	        	sb.append("\n");
	        }
	        
	        sb.append(".csv");

	        writer.write(sb.toString());

	        System.out.println("finished file-creation.");

	      } catch (FileNotFoundException e) {
	        System.out.println(e.getMessage());
	      }
        	
		return;
	}
	
	/*
	 * this method allows the user to set points, does nothing if the location given is not in the board.
	 */
	public void setWind(int x, int y, double vX, double vY) {
		if (checkLoc(x, y)) {
			myMap[x][y].getWind().setLocation(vX, vY);
			return;
		} else {
			return;
		}
	
	}
	
	/*
	 * This method allows the user to run one tick of simulation by propagating changes.
	 * it creates a new copy of the 2D air-array and sets each "tile/block" to be the old value there.
	 * it then changes each such copy by looking at the neighbors (if they exist) and adding/subtracting the difference.
	 * 
	 * keep in mind that the X and Y dimensions are tracted as seperate vectors for each point.
	 */
	public void propagateOneTick() {
		final air[][] myNewMap = new air[mySizeX][mySizeY];
		//new copy of the wind-map array.
		double before_energy = this.getTotalEnergy();
		//outer loop (order does not matter, we could switch i and j here and it would not change anything).
		for (int i = 0; i < mySizeX-1; i++) {
			
			//inner loop. note that this method checks the "y" values first so this corresponds to columns in the printout.
			for (int j = 0; j < mySizeY-1; j++) {
				//create a new air object and set it to the old number and modify based on the "calcChange" method for the X and
				// Y dimensions seperately.
				final air theAir = new air(myMap[i][j].getWind().getX() + calcChangeX(i,j)
					, myMap[i][j].getWind().getY() + calcChangeY(i,j), myMap[i][j].getTemp() + calcChangeTemp(i,j));
				
				//save to the new map.
				myNewMap[i][j] = theAir;
			}
		}
		
		System.out.println("total energy change after tick: " + (this.getTotalEnergy() - before_energy));
		
		//now that the new map is done, change (this) object's 2D air array pointer to point to it.
		myMap = myNewMap;
	}
	
	/*
	 * This method calculates the change in temp in a tile based on its neighbors.
	 */
	private double calcChangeTemp(int i, int j) {
		double avgT = 0;
		int count = 0;	
		//note that we want to skip the neighbors that don't exist in the case that the tile 
		// in question is on the edge.
		
		//also note that we weight the neighbors differently based on the local wind vector.
		
		double myTemp = myMap[i][j].getTemp();
		double windX = myMap[i][j].getWind().getX();
		double windY = myMap[i][j].getWind().getY();
		//left
		if (i > 0) {
			double windM = 1;
//			if (windX < 0)
//				windM = windX;
//			else if (windX > 0) 
//				windM = 1/windX;
			avgT += windM*(myMap[i-1][j].getTemp() - myTemp)/globalDeltaHeat;
			count += 1;
		}
			
		//right
		if (i < mySizeX-2) {
			double windM = 1;
//			if (windX > 0)
//				windM = windX;
//			else if (windX < 0) 
//				windM = 1/windX;
			avgT += windM*(myMap[i+1][j].getTemp() - myTemp)/globalDeltaHeat;
			count += 1;
		}
		
		//up
		if (j > 0) {
			double windM = 1;
//			if (windY < 0)
//				windM = windY;
//			else if (windY > 0) 
//				windM = 1/windY;
			avgT += windM*(myMap[i][j-1].getTemp() - myTemp)/globalDeltaHeat;
			count += 1;
		}
			
		//down
		if (j < mySizeY-2) {
			avgT += (myMap[i][j+1].getTemp() - myTemp)/globalDeltaHeat;
			count += 1;
		}

		return avgT/count;
	}

	/*
	 * This method returns the change in X-values for the a given point in the map.
	 * 
	 * It works by having each tile add/subtract by the average of differences between the tile's value and each neighbor's value.
	 */
	private double calcChangeX(int i, int j) {
		double avgX = 0;
		int count = 0;
	
		//note that we want to skip the neighbors that don't exist in the case that the tile 
		// in question is on the edge.
		if (i > 0)
			avgX += (myMap[i-1][j].getWind().getX() - myMap[i][j].getWind().getX())/2;
			count++;
		if (i < mySizeX-2)
			avgX += (myMap[i+1][j].getWind().getX() - myMap[i][j].getWind().getX())/2;
			count++;
		
		if (j > 0)
			avgX += (myMap[i][j-1].getWind().getX() - myMap[i][j].getWind().getX())/2;
			count++;
		if (j < mySizeY-2)
			avgX += (myMap[i][j+1].getWind().getX() - myMap[i][j].getWind().getX())/2;
			count++;

		return avgX/count;
	}
	
	/*
	 * This method returns the change in Y-values for the a given point in the map.
	 * 
	 * It works by having each tile add/subtract by the average of differences between the tile's value and each neighbor's value.
	 */
	private double calcChangeY(int i, int j) {
		double avgY = 0;
		int count = 0;
		
		//note that we want to skip the neighbors that don't exist in the case that the tile 
		// in question is on the edge.
		if (j > 0)
			avgY += (myMap[i][j-1].getWind().getY() - myMap[i][j].getWind().getY())/2;
			count++;
		if (j < mySizeY-2)
			avgY += (myMap[i][j+1].getWind().getY() - myMap[i][j].getWind().getY())/2;
			count++;
		
		if (i > 0)
			avgY += (myMap[i-1][j].getWind().getY() - myMap[i][j].getWind().getY())/2;
			count++;
		if (i < mySizeX-2)
			avgY += (myMap[i+1][j].getWind().getY() - myMap[i][j].getWind().getY())/2;
			count++;
			
		return avgY/count;
	}

	/*
	 * This method returns the wind at a given point.
	 */
	protected air getWind(int x, int y) {
		if (checkLoc(x, y))
			return myMap[(int) x][(int) y];
		System.out.println("that is not a valid point for a map of size: ("+mySizeX+","+mySizeY+")");
		return null;
	}
	
	
	/*
	 * this method allows the user to print out this objects current wind-map array. for now it 
	 * prints out column and row numbers (only some formatting unfortunately) and displays each points
	 * wind values as decimal values in the form (X|Y) and separates each point with " [] ".
	 */
	public void printWindMap() {
		//print out the column numbers.
		System.out.print("\nnew map: \ncolumns:     ");
		for (int i = 0; i < mySizeX-1; i++) {
			System.out.printf("%-13d",i); // the "13" here is how many spaces to put between each number.
		}
		
		System.out.println();
		for (int i = 0; i < mySizeY-1; i++) {
			//before each row is printed, print the row number.
			System.out.printf("row (%d): ", i);
			for (int j = 0; j < mySizeX-1; j++) {
				//as stated in the description, print each point's value as (X|Y) with 1 decimal of precision.
				System.out.printf("(%3.1f|%3.1f) [] ", myMap[j][i].getWind().getX(), myMap[j][i].getWind().getY());
			}
			//move to next line.
			System.out.println();
		}
	}
	
	/*
	 * This method prints ONLY the temp-map.
	 */
	public void printTempMap() {
		//print out the column numbers.
		System.out.print("\ncolumns:    ");
		for (int i = 0; i < mySizeX-1; i++) {
			System.out.printf("%-8d",i+1); // the "8" here is how many spaces to put between each number.
		}
		
		System.out.println();
		for (int i = 0; i < mySizeY-1; i++) {
			//before each row is printed, print the row number.
			System.out.printf("row (%02d): ", i+1);
			for (int j = 0; j < mySizeX-1; j++) {
				//as stated in the description, print each point's value as (X|Y) with 1 decimal of precision.
				System.out.printf("(%3.0f) | ", myMap[j][i].getTemp());
			}
			//move to next line.
			System.out.println();
		}
	}
	
	/*
	 * this is just a helper method to check if a given point is inside of this objects 2D array.
	 * so it must be greater then 0 and less then the max length in both dimensions.
	 */
	private boolean checkLoc(int x, int y) {
		if (x < 0 || y < 0 || x > mySizeX || y > mySizeY) 
			return false;
		return true;
	}
	
	
}
