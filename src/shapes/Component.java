package shapes;

import java.util.ArrayList;
import java.util.List;

import vectors.Point;

public class Component extends Polygon{
	
	protected double xOffset;
	protected double yOffset;
	
	protected double density;
	
	public Component(List<Point> points) {
		this(points, 0, 0);
	}
	
	public Component(List<Point> points, double xOffset, double yOffset) {
		super(points);
		
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		
		density = 0.1;
	}
	
	public Component(Component hitbox) {
		super();
		
		this.points = hitbox.points;
		
		center = hitbox.center;
		area = hitbox.area;
		areaOfInertia = hitbox.areaOfInertia;
		
		alignment = hitbox.alignment;
		
		xOffset = hitbox.xOffset;
		yOffset = hitbox.yOffset;
		
		density = 0.1;
	}
	
	public void setOffsets(double xOffset, double yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public double getOffsetSquared() {
		return Math.pow(xOffset, 2) + Math.pow(yOffset, 2);
	}
	
	public double getOffset() {
		return Math.sqrt(getOffsetSquared());
	}
	
	public double getXOffset() {
		return xOffset;
	}
	
	public double getYOffset() {
		return yOffset;
	}
	
	@Override
	public Component clone() {
		List<Point> points = new ArrayList<Point>();
		
		for(Point p : this.points) {
			points.add(new Point(p.x(), p.y()));
		}
		
		Component clone = new Component(points);
		
		clone.center = new Point(center.x(), center.y());
		
		clone.alignment = this.alignment;
		clone.area = this.area;
		clone.areaOfInertia = areaOfInertia;
		
		clone.xOffset = xOffset;
		clone.yOffset = yOffset;
		
		return clone;
	}
	
}
