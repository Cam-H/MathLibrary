package shapes;

public class Rect {
	
	private double x;
	private double y;
	
	private double width;
	private double height;
	
	public Rect() {
		this(0, 0, 100, 100);
	}
	
	public Rect(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		
		this.width = width;
		this.height = height;
	}

	public double x() {
		return x;
	}

	public double y() {
		return y;
	}

	public double width() {
		return width;
	}
	
	public double height() {
		return height;
	}
	
	public static boolean isColliding(Rect r, double x, double y) {
		return isColliding(r.x, r.y, r.width, r.height, x, y);
	}
	
	public static boolean isColliding(double rx, double ry, double width, double height, double x, double y) {
		
		if(Math.abs(x - rx) < width / 2) {
			if(Math.abs(y - ry) < height / 2) {
				return true;
			}
		}
		
		return false;
		
	}

}
