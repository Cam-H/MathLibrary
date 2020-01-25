package shapes;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collision.SATCollision;
import physics.Environment;
import physics.Force;
import vectors.Point;
import vectors.Vector;

public class Body {
	
	protected Environment environment;
	
	protected List<Component> components;
	
	protected List<Force> externalForces;
	private boolean forceUpdateRequired;
	
	protected Point CoM;
	protected double mass;
	
	private double xVelocity;
	private double yVelocity;
	
	private Vector netForce;
	
	protected double momentOfInertia;
	
	protected double alignment;
	private double w;//Angular velocity
	
	private double netMoment;
	
	private double radius;//Distance from the CoM to the farthest point
	
	public Body(Component component, double xOffset, double yOffset) {
		this.components = new ArrayList<>(Arrays.asList(component));
		
		externalForces = new ArrayList<Force>();
		forceUpdateRequired = true;
		
		netForce = new Vector();
		momentOfInertia = component.density * component.getAreaOfInertia();

		mass = component.area * component.density;
		CoM = new Point(xOffset, yOffset);

		double distance = 0;
		for(Point p : component.points) {
			double temp = new Vector(p, component.center).magSquared();
			if(temp > distance) {
				distance = temp;
			}
		}
		
		radius = Math.sqrt(distance);
		
		environment = new Environment();
	}
	
	public Body(List<Component> components) {		
		this.components = components;
		
		for(int i = 0; i < components.size(); i++) {
			Component component = components.get(i);
			
			boolean split = false;
			
			do {
				split = false;
				
				List<Point> concaveVertices = component.getConcaveVertices();
				
				if(concaveVertices.size() > 0) {
					System.out.println("mmmda");
					List<Polygon[]> possibleCuts = component.getCutOptions(concaveVertices.get(0));
					
					int z = 0;
					int baselineCutCount = -1;
					
					for(int j = 0; j < possibleCuts.size(); j++) {
						Polygon[] cuts = possibleCuts.get(j);
						
						int concaveVertexCount = cuts[0].getConcaveVertices().size() + cuts[1].getConcaveVertices().size();
						
						if(baselineCutCount == -1 || baselineCutCount > concaveVertexCount) {
							z = j;
							baselineCutCount = concaveVertexCount;
							
							if(concaveVertexCount == 0) {
								break;
							}
						}
						
					}
					
					
					components.remove(i);
					components.add(i, new Component(possibleCuts.get(z)[0].points));
					components.add(i, new Component(possibleCuts.get(z)[1].points));
//					for(int z = j + 2; z != j; z++) {
//						if(z > component.points.size()) {
//							z = 0;
//						}
//					}
//					
//					System.out.println(j);
				}
				
//				if(concave != null) {
//					
//					split = false;
//				}
				
			}while(split);
			
		}
		System.out.println(components.size());
		externalForces = new ArrayList<Force>();
		forceUpdateRequired = true;
		
		netForce = new Vector();
		momentOfInertia = 0;
		
		radius = 0;
		alignment = 0;
		
		calculateCenterOfMass(components);
		
		for(Component component : components) {

			component.xOffset += component.center.x() - CoM.x();
			component.yOffset += component.center.y() - CoM.y();
			
			component.moveToOrigin();
						
			momentOfInertia += component.density * (component.getAreaOfInertia() + component.getArea() * component.getOffsetSquared());
		}
		
		calculateCharacteristics();
		
		xVelocity = yVelocity = w = 0;
		
		environment = new Environment();
				
	}
	

	
	private void calculateCharacteristics() {
		radius = 0;
		
		for(Component component : components) {

			Point temp = new Point(-component.xOffset, -component.yOffset);
			double distance = component.highestDistance(temp);
			
			if(distance > radius) {
				radius = distance;
			}
		}
		
		radius = Math.sqrt(radius);

	}
	
	private void calculateCenterOfMass(List<Component> components) {
		
//		if(components.size() == 1) {
//			CoM = components.get(0).center;
//			
//			this.mass = components.get(0).getArea() * components.get(0).density;
//		}
		
		double weightedXSum = 0, weightedYSum = 0, totalMass = 0;
		
		for(Component component : components) {
			double mass = component.getArea() * component.density;
			
			weightedXSum += (component.center.x() + component.xOffset) * mass;
			weightedYSum += (component.center.y() + component.yOffset) * mass;
			
			totalMass += mass;
		}
		
		CoM = new Point(weightedXSum / totalMass, weightedYSum / totalMass);
		
		this.mass = totalMass;
	}
	
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	public void offset(double dx, double dy) {
		moveTo(new Point(CoM.x() + dx, CoM.y() + dy));
	}
	
	public void moveTo(double x, double y) {
		moveTo(new Point(x, y));
	}
	
	public void moveTo(Point newCoM) {
		CoM = newCoM;
	}
	
	public void alignWith(double angle) {
		rotateAbout(angle - alignment, CoM.x(), CoM.y());
	}
	
	public void rotateAbout(double rotation, Point point) {
		rotateAbout(rotation, point.x(), point.y());
	}
	
	public void rotateAbout(double rotation, double cx, double cy) {
		
		for(Component component : components) {
			component.rotateAbout(rotation, cx - CoM.x() - component.xOffset, cy - CoM.y() - component.yOffset);
		}

		CoM = new Point((Math.cos(rotation) * (CoM.x() - cx) - Math.sin(rotation) * (CoM.y() - cy)) + cx, (Math.sin(rotation) * (CoM.x() - cx) + Math.cos(rotation) * (CoM.y() - cy)) + cy);
		
		alignment += rotation;
	}
	
	public void setVelocity(double xVelocity, double yVelocity) {
		this.xVelocity = xVelocity;
		this.yVelocity = yVelocity;
	}
	
	public void setAngularVelocity(double angularVelocity) {
		w = angularVelocity;
	}
	
	protected double calculateMoment(Force force) {		
		Vector r = new Vector(CoM, force.getForce().head);
		Vector f = force.getForce();

		double moment = r.dx() * f.dy() - f.dx() * r.dy();
				
		return moment;
	}
	
	public void addForce(Force force) {
		externalForces.add(force);
		forceUpdateRequired = true; 
	}
	
	public void removeForce(Force force) {
		externalForces.remove(force);
		forceUpdateRequired = true; 
	}
	
	public void removeForces() {
		externalForces.clear();
		forceUpdateRequired = true; 
	}
	
	private void removeComponent(Component component) {
		components.remove(component);
	}
	
	private void removeComponents(Body body) {
		for(Component c : body.components) {
			removeComponent(c);
		}
	}
	
	public void removeComponents(List<Body> bodies) {
		for(Body b : bodies) {
			removeComponents(b);
		}
		
		Point temp = CoM;
		calculateCenterOfMass(components);

		for(Component c : components) {
			c.xOffset -= CoM.x();
			c.yOffset -= CoM.y();
		}
		
		CoM.offset(temp.x(), temp.y());
		calculateCharacteristics();
		
	}
	
	public List<Body> attemptSplit() {
		List<Body> freeBodies = splitIntoComponentBodies();
		List<Body> splits = new ArrayList<Body>();

		for(int i = 1; i < freeBodies.size(); i++) {

			if(freeBodies.get(0).sharesEdgeWith(freeBodies.get(i))) {

				for(int j = 1; j < freeBodies.size(); j++) {//Skip root, it is not necessary to check it
					if(i == j) {//Skip comparing body with itself
						continue;
					}

					if(freeBodies.get(i).sharesEdgeWith(freeBodies.get(j))) {
						freeBodies.remove(j);
						
						if(j < i) {
							i--;
						}
						
						j--;
						
					}
					
				}

				freeBodies.remove(i);
				i--;
			}
			
		}
		
		freeBodies.remove(0);//Do not include root body in remove list

		for(int i = 0; i < freeBodies.size();) {//See if any of the separated components are connected together, if so merge. Then, separate them from the root body

			for(int j = 1; j < freeBodies.size(); j++) {
//				if(i == j) {
//					continue;
//				}
				
				if(freeBodies.get(i).sharesEdgeWith(freeBodies.get(j))) {
					
					for(int n = 0; n < freeBodies.size(); n++) {
						if(i == n || j == n) {//Skip comparing body with itself
							continue;
						}
						
						if(freeBodies.get(j).sharesEdgeWith(freeBodies.get(n))) {

							freeBodies.get(i).mergeWith(freeBodies.get(n));
							freeBodies.remove(n);
							n--;
						}
						
					}
					
					freeBodies.get(i).mergeWith(freeBodies.get(j));
					freeBodies.remove(j);
					j--;
				}
				
			}

			freeBodies.get(i).completeMerge();
			splits.add(freeBodies.get(i));
			
			freeBodies.remove(i);

		}

		removeComponents(splits);

		
		return splits;
	}
	
	public void mergeWith(Body mergingBody) {
		for(Component mc : mergingBody.components) {
			components.add(mc);
		}
	}
	
	public void completeMerge() {

		Point temp = CoM;
		calculateCenterOfMass(components);

		for(Component c : components) {
			c.xOffset -= CoM.x();
			c.yOffset -= CoM.y();
		}

		
		CoM.offset(temp.x(), temp.y());

		calculateCharacteristics();
	}

	public void update() {

		if(forceUpdateRequired) {
			
			netForce = new Vector();
			netMoment = 0;
			
			for(Force force : externalForces) {
				netForce.addVector(force.getForce());
				
				netMoment += calculateMoment(force);
			}
			
			forceUpdateRequired = false;
		}
		
		xVelocity += netForce.dx() / mass;
		yVelocity += netForce.dy() / mass;
		
		CoM.setX(CoM.x() + xVelocity);
		CoM.setY(CoM.y() + yVelocity);
		
		if(momentOfInertia != 0) {
			w += netMoment / momentOfInertia;
		}

		rotateAbout(w, CoM);
		
		//Apply friction
		double friction = environment.getFriction();
		
		xVelocity *= friction;
		yVelocity *= friction;
		
		w *= friction;
		
	}
	
	
	
	public Point getCenterOfMass() {
		if(CoM == null) {
			calculateCenterOfMass(components);
		}
		
		return CoM;
	}
	
	public Vector getOverlap(Body collider) {
		if(!new Circle(CoM.x(), CoM.y(), radius).isColliding(new Circle(collider.CoM.x(), collider.CoM.y(), collider.radius))) {
			return null;
		}
		
		Vector overlap = null;
		
		for(Component component : components) {
			for(Component cComponent : collider.components) {
				Polygon a = component;
				Polygon b = new Polygon();
				
				double xOffset = cComponent.xOffset + collider.CoM.x() - component.xOffset - CoM.x();
				double yOffset = cComponent.yOffset + collider.CoM.y() - component.yOffset - CoM.y();
				
				for(Point p : cComponent.points) {
					b.addPoint(p.x() + xOffset, p.y() + yOffset);
				}
				
				Vector temp = SATCollision.checkForCollision(a, b);
				
//				if(temp != null && (overlap == null || temp.magSquared() > overlap.magSquared())) {
//					overlap = temp;
//				}
				
				if(temp != null) {
					if(overlap == null) {
						overlap = temp;
					}else {
						overlap.addVector(temp);
					}
				}
			}
		}
				
		return overlap;
	}
	
	Vector contact = null;
	
	public void checkCollision(Body collider) {
		Vector overlap = getOverlap(collider);
		Vector temp = overlap;
		
		if(overlap == null) {
			return;
		}
		
		int i = 10;
		while(temp != null && i-- > 0) {
			double totalMass = mass + collider.mass;
			
			//Splits displacements between bodies based on their mass
			offset(-temp.dx() * (1 - mass / totalMass), -temp.dy() * (1 - mass / totalMass));
			collider.offset(temp.dx() * (1 - collider.mass / totalMass), temp.dy() * (1 - collider.mass / totalMass));
			
			temp = getOverlap(collider);

			if(temp != null) {
				overlap = temp;
			}
		}
		
//		collider.applyReactionForces(this, overlap);

		Vector normal = overlap.unitVector();
		
		Vector contact = getContactSurface(collider, overlap);
		this.contact = contact;
		
		Point contactPoint = new Point(contact.x0() + contact.dx() / 2, contact.y0() + contact.dy() / 2);
		
		if(getMomentumRelativeTo(contactPoint) < collider.getMomentumRelativeTo(contactPoint)) {
			collider.applyReactionForces(this, normal, contactPoint);
		}else {
			applyReactionForces(collider, normal, contactPoint);
		}
		
	}
	
	private void applyReactionForces(Body collider, Vector normal, Point contactPoint) {
		
		double elasticity = 0.9;
		
		Vector refVelocity = new Vector(0, 0, xVelocity, yVelocity);
		Vector colliderVelocity = new Vector(0, 0, collider.xVelocity, collider.yVelocity);

		Vector r1 = new Vector(CoM, contactPoint);
		Vector r2 = new Vector(collider.CoM, contactPoint);
		
		double q1 = (r1.dx() * normal.dy() - normal.dx() * r1.dy()) / momentOfInertia;
		double q2 = (r2.dx() * normal.dy() - normal.dx() * r2.dy()) / collider.momentOfInertia;
		
		double num = Vector.dotProduct(refVelocity, normal) - Vector.dotProduct(colliderVelocity, normal) + w * momentOfInertia * q1 - collider.w * collider.momentOfInertia * q2;
		double den = Vector.dotProduct(new Vector(normal.getScaled((1 / mass + 1 / collider.mass))), normal) + q1 * momentOfInertia * q1 + q2 * collider.momentOfInertia * q2;
		
		double lambda = elasticity * 2 * num / den;
		
		refVelocity.subtractVector(new Vector(normal.getScaled(lambda / mass)));
		xVelocity = refVelocity.dx();
		yVelocity = refVelocity.dy();
		w -= lambda * q1;
		
		colliderVelocity.addVector(new Vector(normal.getScaled(lambda / collider.mass)));
		collider.xVelocity = colliderVelocity.dx();
		collider.yVelocity = colliderVelocity.dy();
		collider.w += lambda * q2;
		
	}
	
	public double getMomentumRelativeTo(Point a) {
		Vector CoMLinearMomentum = new Vector(xVelocity * mass, yVelocity * mass);
		double CoMAngularMomentum = w * momentOfInertia;
		
		Vector CoMA = new Vector(CoM, a);
		
		//La = Lcom + rcoma x p
		return CoMAngularMomentum + (CoMA.dx() * CoMLinearMomentum.dy() - CoMLinearMomentum.dx() * CoMA.dy());
		
	}
	
	public Vector getContactSurface(Body collider, Vector overlap) {
		if(overlap == null) {
			return new Vector();
		}
		
		Vector contact = overlap.getPerpendicular();//Get a unit vector parallel to the contact surface
		Point contactPoint = null;
		
		for(Component c : components) {
			for(Vector ce : c.getEdgeList()) {
				
				if(!contact.parallelTo(ce)) {
					continue;
				}
				
				Vector edge = ce.clone();
				edge.offset(c.xOffset + CoM.x(), c.yOffset + CoM.y());
				
				for(Component cc : collider.components) {
					for(Vector cce : cc.getEdgeList()) {
						Vector colliderEdge = cce.clone();
						colliderEdge.offset(cc.xOffset + collider.CoM.x(), cc.yOffset + collider.CoM.y());

						if(edge.parallelTo(colliderEdge)) {//If the two edges are parallel -> 
							if(edge.inLine(colliderEdge.tail)) {//If the two edges are in-line with each other
								Vector edgeOverlap = getOverlap(edge, colliderEdge);

								if(edgeOverlap != null) {
									return edgeOverlap;
								}
							}
							
							continue;
						}
						
						if(edge.contains(colliderEdge.tail)) {
							contactPoint = colliderEdge.tail;
						}
						
						if(edge.contains(colliderEdge.head)) {
							contactPoint = colliderEdge.head;
						}
						
						
					}
				}
				
			}
		}
		
		for(Component cc : collider.components) {
			for(Vector cce : cc.getEdgeList()) {
				
				if(!contact.parallelTo(cce)) {
					continue;
				}
				
				Vector edge = cce.clone();
				edge.offset(cc.xOffset + collider.CoM.x(), cc.yOffset + collider.CoM.y());
				
				for(Component c : components) {
					for(Vector ce : c.getEdgeList()) {
						Vector colliderEdge = ce.clone();
						colliderEdge.offset(c.xOffset + CoM.x(), c.yOffset + CoM.y());
						
						if(edge.contains(colliderEdge.tail)) {
							contactPoint = colliderEdge.tail;
						}
						
						if(edge.contains(colliderEdge.head)) {
							contactPoint = colliderEdge.head;
						}
						
						
					}
				}
				
			}
		}
		
		if(contactPoint != null) {
			return new Vector(contactPoint, contactPoint);
		}

		return contact;
	}

	
	private Vector getOverlap(Vector a, Vector b) {
		Point aMin = a.tail;
		Point aMax = a.head;
		
		Point bMin = b.tail;
		Point bMax = b.head;
				
		if(a.dx() == 0) {
			
			if(aMin.y() > aMax.y()) {
				aMin = aMax;
				aMax = a.tail;
			}
			
			if(bMin.y() > bMax.y()) {
				bMin = bMax;
				bMax = b.tail;
			}
			
			if(a.dy() == 0) {
				if(bMin.y() <= a.tail.y() && a.tail.y() <= bMax.y() && b.dy() != 0) {
					return new Vector(a.tail, a.tail);
				}
				
				return null;
			}
			
			if(b.dy() == 0) {
				if(aMin.y() <= b.tail.y() && b.tail.y() <= aMax.y()) {
					return new Vector(b.tail, b.tail);
				}
				
				return null;
			}

			if(aMin.y() > bMax.y() || bMin.y() > aMax.y()) {
				return null;
			}
			
			return new Vector(aMin.y() < bMin.y() ? bMin : aMin, aMax.y() > bMax.y() ? bMax : aMax);
			
		}
		
		if(aMin.x() > aMax.x()) {
			aMin = aMax;
			aMax = a.tail;
		}
		
		if(bMin.x() > bMax.x()) {
			bMin = bMax;
			bMax = b.tail;
		}
		
		if(aMin.x() > bMax.x() || bMin.x() > aMax.x()) {
			return null;
		}

		return new Vector(aMin.x() < bMin.x() ? bMin : aMin, aMax.x() > bMax.x() ? bMax : aMax);
	}
	
	public double getVelocity() {
		return Math.sqrt(Math.pow(xVelocity, 2) + Math.pow(yVelocity, 2));
	}
	
	public double getLinearKineticEnergy() {
		return mass * (Math.pow(xVelocity, 2) + Math.pow(yVelocity, 2)) / 2; 
	}
	
	public double getRotationalKineticEnergy() {
		return momentOfInertia * Math.pow(w, 2) / 2;
	}
	
	public double getEnergy() {
		return mass * (Math.pow(xVelocity, 2) + Math.pow(yVelocity, 2)) / 2 + momentOfInertia * Math.pow(w, 2) / 2;
	}
	
	
	public List<Component> getComponents(){
		return components;
	}
	
	public List<Body> splitIntoComponentBodies(){
		List<Body> freeBodies = new ArrayList<Body>();

		for(int i = 0; i < components.size(); i++) {
			freeBodies.add(new Body(components.get(i), CoM.x(), CoM.y()));
		}
		
		return freeBodies;
	}
	
	public boolean sharesEdgeWith(Body body) {

		if(new Circle(CoM.x(), CoM.y(), radius).isColliding(new Circle(body.CoM.x(), body.CoM.y(), radius))) {//Quick check to make sure components are near enough to necessitate checking all edges
			
			for(Component aPoly : components) {
				
				double aXOffset = CoM.x() + aPoly.xOffset;
				double aYOffset = CoM.y() + aPoly.yOffset;
				
				Vector edgeA = new Vector(aPoly.points.get(aPoly.points.size() - 1).clone(), aPoly.points.get(0).clone());
				edgeA.offset(aXOffset, aYOffset);
				int i = 1;

				do {

					for(Component bPoly : body.components) {

						double bXOffset = body.CoM.x() + bPoly.xOffset;
						double bYOffset = body.CoM.y() + bPoly.yOffset;

						Vector edgeB = new Vector(bPoly.points.get(bPoly.points.size() - 1).clone(), bPoly.points.get(0).clone());
						edgeB.offset(bXOffset, bYOffset);
						int j = 1;
						
						do {

							if(checkEdge(edgeA, edgeB)) {
								return true;
							}
							
							if(j < bPoly.points.size()) {
								edgeB = new Vector(edgeB.head, new Point(bPoly.points.get(j).x() + bXOffset, bPoly.points.get(j).y() + bYOffset));
							}
						}while(j++ < bPoly.points.size());

					}

					
					if(i < aPoly.points.size()) {
						edgeA = new Vector(edgeA.head, new Point(aPoly.points.get(i).x() + aXOffset, aPoly.points.get(i).y() + aYOffset));

					}
				}while(i++ < aPoly.points.size());
				
			}
			
		}
		
		return false;
		
	}
	
	private boolean checkEdge(Vector edgeA, Vector edgeB) {
		
		if(edgeA.parallelTo(edgeB)) {//First check whether edges are parallel

			Vector aTtoBT = new Vector(edgeA.tail, edgeB.tail);
			Vector aHtoBH = new Vector(edgeA.head, edgeB.head);
			
			if(((int)aTtoBT.magSquared() == 0 || edgeA.parallelTo(aTtoBT)) && ((int)aHtoBH.magSquared() == 0 || edgeA.parallelTo(aHtoBH))) {//Check whether the two vectors are on the same axis
				Vector overlap = SATCollision.getOverlap(edgeA, edgeB);

				if(overlap.magSquared() > 0) {
					return true;
				}
				
			}
			
		}
		
		return false;
	}
	

	
	//	private boolean containsVertex(Point vertex) {
//		vertex.offset(-CoM.x(), -CoM.y());
//
//		for(Component c : components) {
//			vertex.offset(-c.xOffset, -c.yOffset);
//			
//			for(Point p : c.points) {
//
//				if(Point.comparePoints(vertex, p)) {
//					return true;
//				}
//			}
//			
//			vertex.offset(c.xOffset, c.yOffset);
//		}
//		
//		return false;
//	}
//	
	private Vector getParentEdge(Point child) {
		child.offset(-CoM.x(), -CoM.y());
		
		for(Component c : components) {

			child.offset(-c.xOffset, -c.yOffset);
			
			for(Vector edge : c.getEdgeList()) {

				if(edge.contains(child)) {
					if(!Point.comparePoints(edge.tail, child) && !Point.comparePoints(edge.head, child)) {
						edge.offset(c.xOffset, c.yOffset);
						
						return edge;
					}
				}
			}
			
			child.offset(c.xOffset, c.yOffset);
		}
		
		return null;
	}
//	
//	private List<Vector> getEdgesContaining(Point vertex){
//		List<Vector> edges = new ArrayList<Vector>();
//		
//		vertex.offset(-CoM.x(), -CoM.y());
//		
//		for(Component c : components) {
//
//			vertex.offset(-c.xOffset, -c.yOffset);
//			
//			for(Vector edge : c.getEdgeList()) {
//
//				if(edge.contains(vertex)) {
//					edges.add(edge);
//				}
//			}
//			
//			vertex.offset(c.xOffset, c.yOffset);
//		}
//		
//		return edges;
//	}

	public void render(Graphics2D g) {
		for(Component component : components) {
			component.render(g, (int)(component.xOffset + CoM.x()), (int)(component.yOffset + CoM.y()));
		}
		
		if(CoM != null) {
			g.fillOval((int)CoM.x() - 3, (int)CoM.y() - 3, 6, 6);
//			g.drawOval((int)(CoM.x() - radius), (int)(CoM.y() - radius), (int)(radius * 2), (int)(radius * 2));
			
			//TODO remove
			if(components.size() == 1) {
//				g.drawOval((int)(CoM.x() + components.get(0).xOffset - radius), (int)(CoM.y() + components.get(0).yOffset - radius), (int)(radius * 2), (int)(radius * 2));
			}
		}
		
//		if(contact != null) {
//			g.setColor(new Color(0xff0000));
//			g.drawLine((int)contact.tail.x(), (int)contact.tail.y(), (int)contact.head.x(), (int)contact.head.y());
//			
//			if(contact.magSquared() == 0) {
//				g.fillOval((int)contact.tail.x() - 4, (int)contact.tail.y() - 4, 8, 8);
//			}
//		}
//		
//		for(Force force : externalForces){
//			Vector f = force.getForce();
////			System.out.println(f.tail + " " + f.head);
//			g.drawLine((int)f.tail.x(), (int)f.tail.y(), (int)f.head.x(), (int)f.head.y());
//		}
	}

	@Override
    public String toString() {
		String data = "";
		
		for(Component c : components) {
			for(Point p : c.points) {
				data = data + "(" + (int)(CoM.x() + c.xOffset + p.x()) + ", " + (int)(CoM.y() + c.yOffset + p.y()) + ") ";
			}
			
			data = data + "| ";
		}
		
		return data.substring(0, data.length() - 3);
    }
	
}

//
////
//////
////////
//////////
////////////
//////////////
////////////////
//////////////////
////////////////////
//////////////////////
////////////////////////
//////////////////////////
////////////////////////////
//////////////////////////////
////////////////////////////////
//////////////////////////////////
////////////////////////////////////