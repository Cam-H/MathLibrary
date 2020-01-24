package vectors;

public class Point {

	protected double x;
    protected double y;

    public Point(){
        this(0, 0);
    }

    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double x(){
        return x;
    }

    public double y(){
        return y;
    }

    public void offset(double dx, double dy){
        x += dx;
        y += dy;
    }

    public void moveTo(double newx, double newy){
        x = newx;
        y = newy;
    }

    public void setX(double newx){
        x = newx;
    }

    public void setY(double newy){
        y = newy;
    }

    public Vector expandIntoVector(Vector directionOfExpansion, double expansion){
        Vector v = new Vector(new Point(0,0), directionOfExpansion.unitVector().getScaled(2 * expansion));
        v.centerOn(this);

        return v;

    }
    
    public static boolean comparePoints(Point a, Point b){
    	if(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2) < 0.01) {
    		return true;
    	}
    	
    	return false;
    }
    
    @Override
    public String toString() {
    	return "("+x+", "+ y+")";
    }
    
    public Point clone() {
    	return new Point(this.x, this.y);
    }
	
}
