package shapes;

import vectors.Point;
import vectors.Vector;

public class Triangle {

	private Point a;
	private Point b;
	private Point c;
	
	public Triangle(double x1, double y1, double x2, double y2, double x3, double y3) {
		this(new Point(x1, y1), new Point(x2, y2), new Point(x3, y3));
	}
	
	public Triangle(Point a, Point b, Point c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public double getArea() {
		return Math.abs((a.x() * (b.y() - c.y()) + b.x() * (c.y() - a.y()) + c.x() * (a.y() - b.y())) / 2);
	}
	
	public double getAreaOfInertia() {
		
		Vector base = new Vector(a, this.b);
		Vector perpendicular = base.getPerpendicular();
		perpendicular.offset(c.x(), c.y());

		Point bhIntersection = base.intersectionWith(perpendicular);

		double b = base.magnitude();
		double b1 = new Vector(a, bhIntersection).magnitude();
		
		double h = new Vector(c, bhIntersection).magnitude();
				
		double Ix = b * Math.pow(h, 3) / 36;
		double Iy = h * b / 36 * (Math.pow(b, 2) - b1 * b + Math.pow(b1, 2));
		
		
		return Ix + Iy;
	}
	
	public Point center() {
		return new Point((a.x() + b.x() + c.x()) / 3, (a.y() + b.y() + c.y()) / 3);
	}
}
