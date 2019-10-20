package backend;

import java.awt.geom.Point2D;

/*
 * this class holds all the point-wise information we want to store, for now it is only wind but temp/humidity/density/etc
 * can be added later as seperate values. "temp" is shown in comment code as a basic idea of how.
 */
class air {
	private Point2D.Double myWind;
	private double myTemp;
		
	air(double x, double y, double h) {
		myWind = new Point2D.Double(x,y);
		myTemp = h;
	}
	
	air(Point2D.Double thePoint, double h) {
		myWind = thePoint;
		myTemp = h;
	}
		
	Point2D getWind() {
		return myWind;
	}
	
	double getTemp() {
		return myTemp;
	}
		
	void setWind(double x, double y) {
		myWind = new Point2D.Double(x,y);
	}
	
	void setTemp(double newTemp) {
		myTemp = newTemp;
	}
	
}
