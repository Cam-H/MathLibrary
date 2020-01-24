package collision;

import java.util.ArrayList;
import java.util.List;

import functions.Trigonometry;
import shapes.Polygon;
import vectors.Point;
import vectors.Vector;

public class Collision {
		
	/**
	 * 
	 * @return Vector v - 
	 * if v = 0, there is either zero or one collision
	 * if v != 0, there are two collision points, with v being their overlap
	 */
	public static Vector twoCircle(double r1, double cx1, double cy1, double r2, double cx2, double cy2) {
		
		if(cx1 == cx2 && cy1 == cy2) {
			return new Vector(new Point(r1 + r2, 0));
		}
		
		double xDist = cx1 - cx2;
		double yDist = cy1 - cy2;
		
		double crossSection = Math.pow(r1 + r2, 2);
		
		double overlap = crossSection - (Math.pow(xDist, 2) + Math.pow(yDist, 2));

		if(overlap <= 0) {
			return new Vector();
		}
		
		double theta = Trigonometry.get360Angle((float)xDist, (float)yDist);
		
		overlap = (float)Math.sqrt(overlap) / 2;	

		return new Vector(new Point((float)(overlap * Math.cos(theta)), (float)(overlap * Math.sin(theta))));
		
	}
	
	public static boolean vectorIntersectsCircle(Vector vector, double radius, double cx, float cy) {
		
		List<Double> roots = vectorCircleIntersection(vector, radius, cx, cy);

		if(roots.size() == 0) {
			return false;
		}
		
		for(int i = 0; i < roots.size(); i++) {
			if(roots.get(i) > 0 && roots.get(i) < 1) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * 
	 * 
	 * @param vector
	 * @param radius
	 * @param cx
	 * @param cy
	 * @return t 
	 */
	public static List<Double> vectorCircleIntersection(Vector vector, double radius, double cx, float cy){
		
		double A = Math.pow((vector.x1() - vector.x0()), 2) + Math.pow((vector.y1() - vector.y0()), 2);
		double B = 2 * (vector.x1() - vector.x0()) * (vector.x0() - cx) + 2 * (vector.y1() - vector.y0()) * (vector.y0() - cy);
		double C = Math.pow((vector.x0() - cx), 2) + Math.pow(vector.y0() - cy, 2) - Math.pow(radius, 2);
		
		return getRoots(A, B, C);
	}
	
	public static List<Double> getRoots(double A, double B, double C){
		List<Double> roots = new ArrayList<Double>();
		
		double discriminant = getDiscriminant(A, B, C);
		
		if(discriminant < 0) {//No roots
			return roots;
		}
		
		double sqRoot = Math.sqrt(discriminant);
		
		if(sqRoot != 0) {
			roots.add((-B + sqRoot) / (2 * A));
			roots.add((-B - sqRoot) / (2 * A));
		}else {
			roots.add(-B / (2 * A));
		}
		
		return roots;
	}
	
	public static double getDiscriminant(double A, double B, double C) {
		return Math.pow(B, 2) - 4 * A * C;
	}
	

	public static boolean vectorIntersectsBody(Vector v, Polygon b) {
		return vectorBodyIntersect(v, b) != null;
	}
	
	public static Vector nearestVector;
	
	/**
	 * 
	 * @return Point p - The nearest point of intersection
	 */
	public static Point vectorBodyIntersect(Vector v, Polygon b) {
		
		List<Point> points = b.points();
		
		Vector v2 = new Vector(points.get(0), points.get(points.size() - 1));
		
		Point nearest = v.intersectionWith(v2);
		
		if(nearest != null) {
			v = new Vector(v.tail, nearest);
		}
		
		for(int i = 0; i < points.size() - 1; i++) {
			v2 = new Vector(points.get(i), points.get(i + 1));
			
			Point temp = v.intersectionWith(v2);
			
			if(temp != null) {
				
				if(nearest == null) {
					nearest = temp;
					
					v = new Vector(v.tail, nearest);

					nearestVector = v2;
					
				}else if(v.magSquared() > new Vector(v.tail, temp).magSquared()) {
					nearest = temp;
					
					v = new Vector(v.tail, nearest);

					nearestVector = v2;
				}
				
			}
		}

		return nearest;
	}

}
