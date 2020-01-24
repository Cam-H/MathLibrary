package functions;

import vectors.Vector;

public class Trigonometry {

	private static final double computationAccuracy = 0.01d;
	
	/**
	 * 
	 * @param dx
	 * @param dy
	 * @return theta - The angle in radians (range: 0 - 6.24)
	 */
	public static double get360Angle(double dx, double dy) {
		return Math.atan(dy / dx) + ((dx < 0) ? Math.PI : 0);
	}
	
	public static double get360Angle(Vector v) {
		return Math.atan(v.dy() / v.dx()) + ((v.dx() < 0) ? Math.PI : 0);
	}

	public static double getAngle(double dx, double dy) {
		return Math.atan(dy / dx);
	}
	
	public static double getAngle(Vector a, Vector b) {
		return getAngle(a, b, a.magnitude(), b.magnitude());
	}
	
	public static double getAngle(Vector a, Vector b, double aMagnitude, double bMagnitude) {
		double dotProduct = a.dotProduct(b);
		
		return Math.acos(dotProduct / (aMagnitude * bMagnitude));
	}
	
	public static boolean compareAngles(double a, double b) {
		return Math.abs(Math.tan(a) - Math.tan(b)) < computationAccuracy;
	}
	
	public static double getInteriorAngle(Vector a, Vector b) {
		double angle = getAngle(a, b);
		
		return (angle <= Math.PI) ? angle : Math.PI * 2 - angle;
	}
	
}
