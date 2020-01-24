package vectors;

public class Vector {

	public Point tail;
    public Point head;

    public Vector(){
        this(new Point(0, 0), new Point(0, 0));
    }

    public Vector(double x0, double y0, double x1, double y1){
       this(new Point(x0, y0), new Point(x1, y1));
    }
    
    public Vector(Point head){
        this(new Point(), head);
    }

    public Vector(Point tail, Point head){
        this.tail = tail;
        this.head = head;
    } 
    
    public Vector(double heading, double magnitude) {
    	this(new Point(magnitude * Math.cos(heading), magnitude * Math.sin(heading)));
    }

    public void moveTail(Point newTail){
        tail = newTail;
    }

    public void moveHead(Point newHead){
        head = newHead;
    }
    
    public void moveTo(Point loc) {
    	moveTo(loc.x, loc.y);
    }
    
    public void moveTo(double x, double y) {
    	double dx = dx();
    	double dy = dy();
    	
    	tail = new Point(x, y);
    	head = new Point(x + dx, y + dy);
    }

    public void centerOn(Point p){
        moveTail(new Point(p.x() + tail.x() - head.x() / 2, p.y() + tail.y() - head.y() / 2));
        moveHead(new Point(p.x() + head.x() / 2, p.y() + head.y() / 2));
    }

    public void addVector(Vector displacement){
        moveHead(new Point(head.x() + displacement.dx(), head.y() + displacement.dy()));
    }

    public void subtractVector(Vector subtractor){
        moveHead(new Point(head.x() - subtractor.dx(), head.y() - subtractor.dy()));
	}
    
    public void offset(double dx, double dy) {
    	tail.setX(tail.x() + dx);
    	tail.setY(tail.y() + dy);

    	head.setX(head.x() + dx);
    	head.setY(head.y() + dy);

    }

    public void scale(double scalar) {
    	head.setX(tail.x + dx() * scalar);
    	head.setY(tail.y + dy() * scalar);
    }
    
    public Point getScaled(double scalar){
        return new Point((head.x() - tail.x()) * scalar, (head.y() - tail.y()) * scalar);
    }

    public Vector unitVector(){
        return new Vector(new Point(), new Point((head.x() - tail.x()) / magnitude(), (head.y() - tail.y()) / magnitude()));
    }

    public double magnitude(){ 	
        return Math.sqrt(magSquared());
    }

    public double magSquared(){
        return Math.pow(head.x() - tail.x(), 2) + Math.pow(head.y() - tail.y(), 2);
    }

    public Vector getPerpendicular(){
        Vector v = new Vector(new Point(-(dy()), dx()));
        v = v.unitVector();

        return v;
    }
    
    public Vector face(Vector toFace) {
    	if(dotProduct(this, toFace) < 0) {
    		return toFace.flipHeadTail();
    	}
    	
    	return toFace;
    }
    
    public Vector flipHeadTail() {    	
    	return new Vector(new Point(head.x(), head.y()), new Point(tail.x(), tail.y()));
    }

    //Does not handle zero vectors as inputs
    public boolean parallelTo(Vector b){
    	if(Math.abs(dx() * b.dy() - b.dx() * dy()) < 0.01) {//First check whether edges are parallel (within a certain accuracy)
			return true;
		}
		
		return false;
    }

    public Point headAtLength(int length){
        return getScaled(length / (float)magnitude());
    }

    public double dotProduct(Vector v){
        return dotProduct(this, v);
    }

    public static double dotProduct(Vector a, Vector b){
        return (a.head.x() - a.tail.x()) * (b.head.x() - b.tail.x()) + (a.head.y() - a.tail.y()) * (b.head.y() - b.tail.y());
    }
    
    public double angleBetween(Vector b) {
    	return Math.acos(dotProduct(this, b) / magnitude() / b.magnitude());
    }

    public Point getMiddlePoint() {
    	double x = (tail.x() + head.x()) / 2;
    	double y = (tail.y() + head.y()) / 2;
    	
    	return new Point(x, y);
    }
    
    public Point getMiddlePoint(Point p){
        Vector pToTail = new Vector(p, tail);
        Vector pToHead = new Vector(p, head);

        if(pToTail.magSquared() > magSquared()){
            if(pToHead.magSquared() > pToTail.magSquared()){
                return tail;
            }

            return head;
        }

        if(pToHead.magSquared() > magSquared()){
            return tail;
        }

        return p;
    }
    
    public Vector zero() {
    	return new Vector(new Point(dx(), dy()));
    }

    public Point intersectionWith(Vector v2) {
    	
    	double xt = dx();
    	double yt = dy();
    	
    	double xtOff = tail.x();
    	double ytOff = tail.y();
    	
    	
    	double xu = v2.dx();
    	double yu = v2.dy();
    	
    	double xuOff = v2.tail.x();
    	double yuOff = v2.tail.y();
    	    	
    	double multiplier = xt / yt;
    	
    	double u;
    	double t;

    	if(yt == 0) {
    		if(yu == 0) {
    			if(ytOff != yuOff) {
    				return null;
    			}
    			
    			u = 0;
    		}else {
        		u = (ytOff - yuOff) / yu;
    		}
    	}else {
    		if(xu - yu != 0) {
            	u = ((xtOff - ytOff * multiplier) - (xuOff - yuOff * multiplier)) / (xu - yu * multiplier); 
    		}else {
    			u = 0;
    		}
    	}

    	if(xt != 0) {
        	t = (xu * u + xuOff - xtOff) / xt;
    	}else {
    		t = 0;
    	}
    	

//    	if(t < 0 || t > 1 || u < 0 || u > 1) {
//    		return null;
//		}
    	
    	double x = xt * t + xtOff;
    	double y = yt * t + ytOff;
    	    	    
		return new Point(x, y);
		
//    	if(Math.abs(x - (xu * u + xuOff)) < 1 && Math.abs(y - (yu * u + yuOff)) < 1) {
//    		return new Point(x, y);
//    	}
//    	    	
//    	return null;
    }
    
    public void flip() {
    	head.setX(tail.x() - dx());
    	head.setY(tail.y() - dy());
    }
    
    public Vector reflect(Vector normal){
    	normal = normalize(normal);
    	
//    	this.face(normal);

    	Vector v = new Vector(new Point(tail.x(), tail.y()), new Point(head.x(), head.y()));

    	double dotProduct = dotProduct(normal);
    	
    	v.subtractVector(new Vector(normal.getScaled((float)(2f * dotProduct))));
        v.offset(dx(), dy());

        return v;
    }
    
    public static Vector normalize(Vector toNormalize) {
    	float magnitude = (float)toNormalize.magnitude();
    	
    	return new Vector(0, 0, toNormalize.dx() / magnitude, toNormalize.dy() / magnitude);
    }
    
    public Point getProjectionOfPoint(Point b){
        Vector v = new Vector(tail, b);

        Point p = getVectorProjection(v);

        return new Point(p.x() + tail.x(), p.y() + tail.y());
    }

    public Point getVectorProjection(Vector projected){
        return getScaled((float)(dotProduct(projected, this) / magSquared()));
    }
    
    public Point confine(Point toConfine) {
    	return getMiddlePoint(toConfine);
    }
    
    public boolean inLine(Point p) {
    	double t;
    	double u;

    	if(dx() == 0) {
    		if(dy() == 0) {
    			return Point.comparePoints(tail, p);
    		}
    		
    		t = (p.y - tail.y) / dy();
    		
    		return Math.abs(tail.x - p.x) < 0.001;
    	}else if(dy() == 0) {
    		t = (p.x - tail.x) / dx();
    		
    		return Math.abs(tail.y - p.y) < 0.001;
    	}
    	
		t = (p.x - tail.x) / dx();
		u = (p.y - tail.y) / dy();
    			
    	return Math.abs(t - u) <= 0.1;
    }
    
    public boolean contains(Point p) {
    	double t;
    	double u;

    	if(dx() == 0) {
    		if(dy() == 0) {
    			return Point.comparePoints(tail, p);
    		}
    		
    		t = (p.y - tail.y) / dy();
    		
    		return Math.abs(tail.x - p.x) < 0.001 && t >= 0 && t <= 1;
    	}else if(dy() == 0) {
    		t = (p.x - tail.x) / dx();
    		
    		return Math.abs(tail.y - p.y) < 0.001 && t >= 0 && t <= 1;
    	}
    	
		t = (p.x - tail.x) / dx();
		u = (p.y - tail.y) / dy();
    			
    	return t >= 0 && t <= 1 && Math.abs(t - u) <= 0.1;
    }
	
    public double dx(){
        return head.x() - tail.x();
    }

    public double dy(){
        return head.y() - tail.y();
    }
    
	public double x0() {
		return tail.x();
	}
	
	public double y0() {
		return tail.y();
	}
	
	public double x1() {
		return head.x();
	}
	
	public double y1() {
		return head.y();
	}
	
	public static double getMagnitudeSquared(double dx, double dy) {
		return Math.pow(dx, 2) + Math.pow(dy, 2);
	}
	
	@Override
	public Vector clone() {
		return new Vector(tail.x(), tail.y(), head.x(), head.y());
	}
	
	@Override
	public String toString() {
		return "["+dx()+", "+dy()+"]";
	}
	
}
