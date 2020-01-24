package physics;

import vectors.Vector;

public class Force {
	
	private Vector force;
	
	public Force(Vector forceAxis, double forceMagnitude) {
		force = forceAxis;
		
		if(forceAxis.magnitude() != 1) {
			force = force.unitVector();
		}

		force.scale(forceMagnitude);
	}
	
	public Force(Vector force) {
		this.force = force;
	}
	
	public void offset(double dx, double dy) {
		force.offset(dx, dy);
	}
	
	public Vector getForce() {
		return force;
	}

}
