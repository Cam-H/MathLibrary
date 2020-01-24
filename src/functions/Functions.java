package functions;

import vectors.Point;

public class Functions {

	public static double getLogistic(double maximum, double growthRate, double x) {
		return maximum / (1 + Math.pow(Math.E, -growthRate * (x)));
	}
	
	public static Point getRoots(double A, double B, double C) {
		double discriminant = Math.pow(B, 2) - 4 * A * C;
		
		if(discriminant < 0) {
			System.err.println("Negative discriminant invalid! " + discriminant);
			return null;
		}
		
		discriminant = Math.sqrt(discriminant);

		double divisor = (A != 0 ? 2 * A : 1);

		return new Point((-B + discriminant) / (divisor), (-B - discriminant) / (divisor));
	}
	
	public static boolean bothOdd(int a, int b) {
		return a % 2 != 0 && b % 2 != 0;
	}
	
	public static boolean oddXOR(int a, int b) {
		return a % 2 != b % 2;
	}
	
}
