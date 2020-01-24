package shapes;

public class Circle {
	
	private double cx;
	private double cy;
	
	private double r;
	
	public Circle() {
		this(0, 0, 100);
	}
	
	public Circle(double cx, double cy, double r) {
		this.cx = cx;
		this.cy = cy;
		
		this.r = r;
	}

	public double cx() {
		return cx;
	}

	public double cy() {
		return cy;
	}

	public double r() {
		return r;
	}
	
	public static boolean isColliding(Circle c, double x, double y) {
		return isColliding(c.cx, c.cy, c.r, x, y);
	}
	
	public static boolean isColliding(double cx, double cy, double r, double x, double y) {
		return Math.pow(cx - x, 2) + Math.pow(cy - y, 2) < Math.pow(r, 2);
	}
	
	public boolean isColliding(Circle collider) {
		return Math.pow(cx - collider.cx, 2) + Math.pow(cy - collider.cy, 2) < Math.pow(r + collider.r, 2);
	}

}
