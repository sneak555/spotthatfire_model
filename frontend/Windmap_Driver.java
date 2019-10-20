/*
 * spaceapps hackathon, "spothatfire" algorithm.
 */
package frontend;

import backend.windmap;

/*
 * This is the main driver, it runs the other classes/methods/etc to run a basic/wrong(?) fire propagation.
 */

/**
 * 
 * @author Benjamin De Jager, Steven Fischbach
 * @version 0.1.2
 * date:10/19/2019
 */
public class Windmap_Driver {
	
	/*
	 * file-seperator is the project default root-path.
	 */
	private final static String fileSeparator = System.getProperty("file.separator");
	
	/*
	 * These two are constants that are used as the size of the wind-map.
	 */
	private final static int sizeX = 20;
	private final static int sizeY = 20;
	
	private static windmap theMap;
	
	/*
	 * This is the main method to run this windmap program.
	 */
	public static void main(String[] args) {
		intro();
		// create the windmap.
		theMap = new windmap(sizeX+1, sizeY+1);

		//print out the default-map for comparison and then edit two sample points.
		theMap.setTemp(3, 4, 150);
		
		theMap.setTemp(15, 7, 110);
		
		theMap.setTemp(13, 13, 90);
		theMap.setTemp(12, 12, 90);
		theMap.setTemp(12, 13, 90);
		theMap.setTemp(13, 12, 90);
		
		/*
		 * this is a weird model as it treats "wind" more like it is temp/heat/energy spreading across a array of points. 
		 * run the main method in a java-IDE to see a map-print out that first prints out a default all-zeros map and 
		 * then modifys a example points. this model has a couple bugs that I'm still fixing in regards to the size/points.
		 * 
		 * it then prints out each following tick of the simulation that results from that change.
		 */
		
		//print out the new map.
		System.out.print("\nstart simulation: tick# = 0");
		theMap.printTempMap();
		System.out.println();
		//run 20 ticks of propagation/simulation.
		doXTicks(60);

	}
	
	/*
	 * basic introduction. says little of value.
	 */
	private static void intro() {
		System.out.println("windmapV0.1");
		return;
	}
	
	/*
	 * This method runs X ticks of simulation as given as a int.
	 */
	private static void doXTicks(int X) {
		for (int i = 1;i < X+1;i++) {
			System.out.print("start tick #" + i);
			doOneTick();
			System.out.println();
			
			String temp = fileSeparator+"mapFile_tick-"+i+"txt";
			
			theMap.createMapFile(temp, "tick"+i+".csv");
//			} catch (IOException e) {
//				System.out.println("could not create the map file");
//				e.printStackTrace();
//			}
		}
	}
	
	/*
	 * This method runs exactly one tick of simulation and prints out the map each time.
	 */
	private static void doOneTick() {
		theMap.propagateOneTick();
		theMap.printTempMap();
	}
}
