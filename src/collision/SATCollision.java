package collision;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import shapes.Polygon;
import vectors.Point;
import vectors.Vector;

public class SATCollision {

	private static Vector minimumDisplacement;

    private double plx;
    private double ply;
    
    private static List<Vector> projections;
    private static List<Vector> overlaps;

    
    public SATCollision(){
        minimumDisplacement = null;

        plx = ply = -1;
    }

    public static Vector checkForCollision(Polygon a, double radius, double cx, double cy){
    	projections = new ArrayList<Vector>();
    	overlaps = new ArrayList<Vector>();
    	
        minimumDisplacement = null;

        List<Vector>shadowAxes = new ArrayList<Vector>();
        
        shadowAxes = addUniqueAxes(shadowAxes, a);
        shadowAxes = addCircleAxes(shadowAxes, cx, cy);
        
        for(Vector surface : shadowAxes){

            Vector aProj = getShadow(surface, a);
//            projections.add(aProj.clone());

            Vector cProj = surface.getProjectionOfPoint(new Point(cx, cy)).expandIntoVector(aProj, radius);
//            projections.add(cProj.clone());
            
            Vector overlap = getOverlap(aProj, cProj);
//            overlaps.add(overlap.clone());
            
            if(overlap.magSquared() > 0){//Displacement needed
                if((minimumDisplacement == null) ? true : overlap.magSquared() < minimumDisplacement.magSquared()){
                    minimumDisplacement = overlap;
                }
            }else{
                if(overlap.magSquared() == 0){
                    minimumDisplacement = null;

                    break;
                }
            }

        }
        
        return minimumDisplacement;

    }
    
    public static Vector checkForCollision(Polygon a, Polygon b){
    	
    	projections = new ArrayList<Vector>();
    	overlaps = new ArrayList<Vector>();
    	
        minimumDisplacement = null;

        List<Vector>shadowAxes = new ArrayList<Vector>();
        
        shadowAxes = addUniqueAxes(shadowAxes, a);
        shadowAxes = addUniqueAxes(shadowAxes, b);

        for(Vector surface : shadowAxes){

            Vector aProj = getShadow(surface, a);
//            projections.add(aProj.clone());

            Vector bProj = getShadow(surface, b);
//            projections.add(bProj.clone());

            Vector overlap = getOverlap(aProj, bProj);
//            overlaps.add(overlap.clone());

            if(overlap.magSquared() > 0){//Displacement needed
                if((minimumDisplacement == null) ? true : overlap.magSquared() < minimumDisplacement.magSquared()){
                    minimumDisplacement = overlap;
                }
            }else{
                if(overlap.magSquared() == 0){
                    minimumDisplacement = null;

                    break;
                }
            }

        }
        
        return minimumDisplacement;

    }
    
    public static Vector getShadow(Vector surface, Polygon body) {

    	 Point tail = surface.getProjectionOfPoint(body.points().get(body.points().size() - 1));
    	 Point head = surface.getProjectionOfPoint(body.points().get(0));
    	 Vector aProj = new Vector(tail, head);
    			 
         for(int i = 1; i < body.points().size() - 1; i++){
             Point proj = surface.getProjectionOfPoint(body.points().get(i));

             Point midPoint = aProj.getMiddlePoint(proj);
             if(midPoint == aProj.tail){
                 aProj.tail = proj;
             }else if(midPoint == aProj.head){
                 aProj.head = proj;
             }
         }

         return aProj;
    }

  

    public static Vector getOverlap(Vector a, Vector b){

    	b = a.face(b);
    	
    	Point bpt = a.getMiddlePoint(b.tail);
        Point bph = a.getMiddlePoint(b.head);

        //IF 
        if(!(bpt == b.tail ^ bph == b.head) && !(bpt == b.tail && bph == b.head) && !(bpt == a.tail && bph == a.head)) {
        	return new Vector();
        }
        
        Vector throughHead = new Vector(b.tail, a.head);
        Vector throughTail = new Vector(b.head, a.tail);
        
        	
        return (throughHead.magSquared() < throughTail.magSquared()) ? throughHead : throughTail;
        
    }

//    public static Vector getOverlap(Vector a, Vector b) {
//		Point aMin = a.tail;
//		Point aMax = a.head;
//		
//		Point bMin = b.tail;
//		Point bMax = b.head;
//				
//		if(a.dx() == 0) {
//			
//			if(aMin.y() > aMax.y()) {
//				aMin = aMax;
//				aMax = a.tail;
//			}
//			
//			if(bMin.y() > bMax.y()) {
//				bMin = bMax;
//				bMax = b.tail;
//			}
//			
//			if(a.dy() == 0) {
//				if(bMin.y() <= a.tail.y() && a.tail.y() <= bMax.y() && b.dy() != 0) {
//					return new Vector(a.tail, a.tail);
//				}
//				
//				return null;
//			}
//			
//			if(b.dy() == 0) {
//				if(aMin.y() <= b.tail.y() && b.tail.y() <= aMax.y()) {
//					return new Vector(b.tail, b.tail);
//				}
//				
//				return null;
//			}
//
//			if(aMin.y() > bMax.y() || bMin.y() > aMax.y()) {
//				return null;
//			}
//			
//			return new Vector(aMin.y() < bMin.y() ? bMin : aMin, aMax.y() > bMax.y() ? bMax : aMax);
//			
//		}
//		
//		if(aMin.x() > aMax.x()) {
//			aMin = aMax;
//			aMax = a.tail;
//		}
//		
//		if(bMin.x() > bMax.x()) {
//			bMin = bMax;
//			bMax = b.tail;
//		}
//		
//		if(aMin.x() > bMax.x() || bMin.x() > aMax.x()) {
//			return null;
//		}
//
//		return new Vector(aMin.x() < bMin.x() ? bMin : aMin, aMax.x() > bMax.x() ? bMax : aMax);
//	}
    
    private static List<Vector> addUniqueAxes(List<Vector> shadowAxes, Polygon a){

    	Vector baseAxis = new Vector(a.points().get(0), a.points().get(a.points().size() - 1));
    	
    	addAxes(shadowAxes, baseAxis);
    	
        //Adds all unique axes
        for(int i = 0; i < a.points().size() - 1; i++){
            Vector v = new Vector(a.points().get(i), a.points().get(i + 1));

            addAxes(shadowAxes, v);

        }

        return shadowAxes;
    }
    
    private static void addAxes(List<Vector> axes, Vector axis) {
    	
    	if(!isDuplicate(axes, axis)) {
    		axes.add(axis);
        }
         
         Vector normal = axis.clone().getPerpendicular();

         if(!isDuplicate(axes, normal)) {
        	 axes.add(normal);
         }
    	
    }
    
    private static boolean isDuplicate(List<Vector> axes, Vector newAxis) {
         for(int j = 0; j < axes.size(); j++){
             if(axes.get(j).parallelTo(newAxis)){
                 return true;
             }
         }

         return false;
    }
    
    private static List<Vector> addCircleAxes(List<Vector> axes, double cx, double cy){
    	int axesToAdd = axes.size();
    	
    	for(int i = 0; i < axesToAdd; i++) {
    		axes.add(axes.get(i).getPerpendicular());
    	}
    	
    	return axes;
    }

    public void setTarget(double newx, double newy){
        plx = newx;
        ply = newy;
    }

    /***********************GETTERS********************/

    public Vector minimumDisplacement(){
        return minimumDisplacement;
    }
    
    public void render(Graphics2D g2d) {
    	g2d.setColor(new Color(0xff0000aa));
    	
    	if(projections != null) {
    		for(int i = 0; i < projections.size(); i++) {
        		Vector v = projections.get(i);
        		
        		if(i == projections.size() - 1) {
        			g2d.setColor(new Color(0xffffff00));
        		}else if(i == projections.size() - 2) {
        			g2d.setColor(new Color(0xffff0000));
        		}
        		
        		g2d.drawLine((int)v.head.x(), (int)v.head.y(), (int)v.tail.x(), (int)v.tail.y());
        	}
    	}
    	
    	g2d.setColor(new Color(0xffaa0000));
    	
    	if(overlaps != null) {
    		for(int i = 0; i < overlaps.size(); i++) {
        		Vector v = overlaps.get(i);
        		
        		g2d.drawLine((int)v.head.x(), (int)v.head.y(), (int)v.tail.x(), (int)v.tail.y());
        	}
    	}
    }
}
