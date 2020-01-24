package vectors;


public class Point3D extends Point {

	private double z;
	
	public Point3D(double x, double y) {
		this(x, y, -1);
	}
	
	public Point3D(Point p2D) {
		this(p2D.x(), p2D.y(), -1);
	}
	
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void offset(double dx, double dy, double dz){
        x += dx;
        y += dy;
        z += dz;
    }

    public void moveTo(double newx, double newy, double newz){
        x = newx;
        y = newy;
        z = newz;
    }
    
    public void setZ(double newz){
        z = newz;
    }
    
    public Point get2DPosition() {
    	return new Point(x, y);
    }
    
    public double z() {
    	return z;
    }
    
    @Override
    public String toString() {
    	return "("+x+", "+y+", "+z+")";
    }
	
}
