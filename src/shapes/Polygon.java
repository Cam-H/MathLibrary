package shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import vectors.Point;
import vectors.Vector;

public class Polygon {

	protected List<Point> points;
	
	protected Point center;
	protected double area;
	protected double areaOfInertia;
	
	protected double alignment;// Angle from 0 - 6.28

	public Polygon() {
		this(new ArrayList<Point>());
	}

	public Polygon(List<Point> points) {
		this.points = points;
		
		center = null;
		area = -1;
		areaOfInertia = -1;
		
		if(points.size() > 0) {
			center();
		}
		
		alignment = 0;
	}

	public void addPoint(double x, double y) {
		points.add(new Point(x, y));
		
		center = null;
		area = areaOfInertia = -1;
	}
	
	public void scale(double scalar) {
		Point center = center();
		
		for(int i = 0; i < points.size(); i++) {
			points.set(i, new Point((points.get(i).x() - center.x()) * scalar + center.x(), (points.get(i).y() - center.y()) * scalar + center.y()));
		}
		
		area = areaOfInertia = -1;
	}
	
	private void calculateArea() {
		List<Triangle> triangles = getTriangles();
		
		double area = 0;
		
		for(Triangle tri : triangles) {
			area += tri.getArea();
		}
		
		this.area = area;
	}
	
	private void calculateAreaOfInertia() {
		List<Triangle> triangles = getTriangles();
		
		double areaOfInertia = 0;
		
		for(Triangle tri : triangles) {
			areaOfInertia += tri.getAreaOfInertia() + tri.getArea() * new Vector(tri.center(), center).magSquared();
		}
		
		this.areaOfInertia = areaOfInertia;
				
	}

	public void alignWith(double angle) {
		rotateAbout(angle - alignment, center.x(), center.y());
	}
	
	public void rotateAbout(double rotation, Point point) {
		rotateAbout(rotation, point.x(), point.y());
	}
	
	public void rotateAbout(double rotation, double cx, double cy) {
		
		for (int i = 0; i < points.size(); i++) {
			
			double x = points.get(i).x() - cx;
			double y = points.get(i).y() - cy;
			
			double rotatedX = (Math.cos(rotation) * x - Math.sin(rotation) * y);
			double rotatedY = (Math.sin(rotation) * x + Math.cos(rotation) * y);

			points.get(i).setX(rotatedX + cx);
			points.get(i).setY(rotatedY + cy);
		}

		center = new Point((Math.cos(rotation) * (center.x() - cx) - Math.sin(rotation) * (center.y() - cy)) + cx, (Math.sin(rotation) * (center.x() - cx) + Math.cos(rotation) * (center.y() - cy)) + cy);
		
		alignment += rotation;
	}
	
	public void moveToOrigin() {
		if(center == null) {
			center();
		}
		
		for(int i = 0; i < points.size(); i++) {
			points.get(i).offset(-center.x(), -center.y());
		}
		
		center = new Point(0, 0);
	}

	/*************** GETTERS *****************/

	public List<Point> points() {
		return points;
	}

//	public Point center() {
//		int xTotal = 0;
//		int yTotal = 0;
//
//		for (Point p : points) {
//			xTotal += p.x();
//			yTotal += p.y();
//		}
//		
//		return center = new Point(xTotal / points.size(), yTotal / points.size());
//	}
	
	public Point center() {
		
		double xTotal = 0;
		double yTotal = 0;
		
		double totalArea = 0;
		
		for(int i = 1; i < points.size() - 1; i++) {
			Triangle tri = new Triangle(points.get(0), points.get(i), points.get(i + 1));
			
			xTotal += tri.center().x() * tri.getArea();
			yTotal += tri.center().y() * tri.getArea();

			totalArea += tri.getArea();
		}
		
//		int xTotal = 0;
//		int yTotal = 0;
//
//		for (Point p : points) {
//			xTotal += p.x();
//			yTotal += p.y();
//		}
		
		return center = new Point(xTotal / totalArea, yTotal / totalArea);
	}
	

	public boolean centered() {
		return !(center.x() != 0 || center.y() != 0);
	}
	
	public double getAlignment() {
		return alignment;
	}
	
	public double getArea() {
		if(area == -1) {
			calculateArea();
		}
		
		return area;
	}
	
	public double getAreaOfInertia() {
		if(areaOfInertia == -1) {
			calculateAreaOfInertia();
		}
		
		return areaOfInertia;
	}
	
	private List<Triangle> getTriangles(){
		List<Triangle> triangles = new ArrayList<Triangle>();
		
		if(points.size() < 3) {
			area = 0;
			return triangles;
		}
		
		for(int i = 0; i < points.size() - 2; i++) {
			triangles.add(new Triangle(points.get(0), points.get(i + 1), points.get(i + 2)));
		}
		
		return triangles;
	}
	
	public List<Vector> getEdgeList() {
		List<Vector> edges = new ArrayList<Vector>();

		int bodyVertexCount = points.size();

		if (bodyVertexCount < 3) {
			System.err.println("Too few points to generate an edge list, at lest 3 points are needed for a polygon");
			return null;
		}

		edges.add(new Vector(points.get(bodyVertexCount - 1).x(), points.get(bodyVertexCount - 1).y(),
				points.get(0).x(), points.get(0).y()));

		for (int i = 0; i < bodyVertexCount - 1; i++) {
			edges.add(new Vector(points.get(i).x(), points.get(i).y(), points.get(i + 1).x(), points.get(i + 1).y()));
		}

		return edges;
	}

	/**
	 * 
	 * @param reference
	 * @return distance - The square of the largest distance
	 */
	public double highestDistance(Point reference) {
		double distance = 0;

		for (Point p : points) {
			double temp = Math.pow(reference.x() - p.x(), 2) + Math.pow(reference.y() - p.y(), 2);
			if (temp > distance) {
				distance = temp;
			}
		}

		return distance;

	}

	public void printPoints() {
		for (int i = 0; i < points.size(); i++) {
			System.out.println("x: " + points.get(i).x() + " | y: " + points.get(i).y());
		}
	}

	public void render(Graphics2D g2d) {
		render(g2d, 0, 0);
	}

	public void render(Graphics2D g2d, int xOffset, int yOffset) {
		int vertexCount = points.size();

		boolean fill = true;
		
		if(fill) {
			GeneralPath path = new GeneralPath();

			path.moveTo((int)points.get(0).x() + xOffset, (int)points.get(0).y() + yOffset);
			
			for(int i = 1; i < points.size(); i++) {
				path.lineTo((int)points.get(i).x() + xOffset, (int)points.get(i).y() + yOffset);
			}
			
			path.closePath();
			
			g2d.setColor(new Color(0x777777));
			g2d.fill(path);
			
			g2d.setColor(new Color(0x0000ff));

			g2d.drawLine((int) points.get(0).x() + xOffset, (int) points.get(0).y() + yOffset,
					(int) points.get(vertexCount - 1).x() + xOffset, (int) points.get(vertexCount - 1).y() + yOffset);
			
			for (int i = 0; i < vertexCount - 1; i++) {
				g2d.drawLine((int) points.get(i).x() + xOffset, (int) points.get(i).y() + yOffset,
						(int) points.get(i + 1).x() + xOffset, (int) points.get(i + 1).y() + yOffset);
			}
		}else {
			
			
			
		}
		

		
		
	}
	
	@Override
	public Polygon clone() {
		List<Point> points = new ArrayList<Point>();
		
		for(Point p : this.points) {
			points.add(new Point(p.x(), p.y()));
		}
		
		Polygon clone = new Polygon(points);
		
		clone.center = new Point(center.x(), center.y());
		
		clone.alignment = this.alignment;
		clone.area = this.area;
		clone.areaOfInertia = areaOfInertia;
		
		return clone;
	}

}
