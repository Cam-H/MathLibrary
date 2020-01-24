package testing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import shapes.Body;
import shapes.Component;
import shapes.Polygon;
import vectors.Point;

public class ToolTesting3 extends Thread implements MouseListener, MouseMotionListener{
	
	JFrame frame;
	
	List<Body> bodies;
		
	boolean pressed = false;
	Point pullPoint;
	int mx = 0, my = 0;
	
	double theta = 0;
		
	public ToolTesting3() {
		
		frame = new JFrame();
		
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		
		frame.addMouseListener(this);
		frame.addMouseMotionListener(this);
		
		frame.setVisible(true);
		
		start();
		
	}
	
	public void run() {
		
		bodies = new ArrayList<Body>();
		
		List<Component> ps = new ArrayList<Component>();
		
		List<Point> points = new ArrayList<Point>();
		
		
		List<Component> components = new ArrayList<Component>();
		
		Polygon ts = new Polygon();
		ts.addPoint(0, 0);
		ts.addPoint(150, 20);
		ts.addPoint(150, 80);
		ts.addPoint(0, 100);
		ts.addPoint(0, 50);
		
		ts.scale(0.75f);
		
		Component thrusterShape = new Component(ts.points());
					
		components.add(thrusterShape);
		
		thrusterShape = thrusterShape.clone();
		thrusterShape.scale(0.8f);
		thrusterShape.setOffsets(-50, -100);
		
		components.add(thrusterShape);
		
		thrusterShape = thrusterShape.clone();
		thrusterShape.setOffsets(-50, 100);

		components.add(thrusterShape);

		
		thrusterShape = thrusterShape.clone();
		thrusterShape.rotateAbout(Math.PI, thrusterShape.center());
		thrusterShape.setOffsets(108, 0);

		components.add(thrusterShape);

		
		thrusterShape = thrusterShape.clone();
		thrusterShape.scale(0.5f);
		thrusterShape.rotateAbout(-Math.PI / 6, thrusterShape.center());
		thrusterShape.setOffsets(175, -50);

		components.add(thrusterShape);

		
		thrusterShape = thrusterShape.clone();
		thrusterShape.rotateAbout(Math.PI / 3, thrusterShape.center());
		thrusterShape.setOffsets(175, 50);

		components.add(thrusterShape);
		
		Body body = new Body(components);

		body.moveTo(frame.getWidth() / 2, frame.getHeight() / 2);
		bodies.add(body);

		for(int z = 0; z < bodies.size(); z++) {
			List<Body> splits = bodies.get(z).attemptSplit();
			
			for(Body s : splits) {
				bodies.add(s);
			}

		}

		//		bodies.get(0).attemptSplit();
		
		while(true) {
			
			update();
			render();
			
			try {
				Thread.sleep(1000 / 60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void update() {			
//		b.update();
			
	}
	
	public void render() {
		BufferStrategy bs = frame.getBufferStrategy();
		
		if(bs == null) {
			frame.createBufferStrategy(3);
			return;
		}
		
		Graphics2D g = (Graphics2D)bs.getDrawGraphics();
		
		/////////////////////////////////////
		
		g.setColor(new Color(0xffffff));
		g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
		
		g.setColor(new Color(0x0000ff));
		g.fillOval(frame.getWidth() / 2 - 5, frame.getHeight() / 2 - 5, 10, 10);
		
		g.setColor(new Color(0x000000));		
		
		for(Body b : bodies) {
			b.render(g);
		}
		
		g.drawString(mx + " " + my, 100, 100);

		/////////////////////////////////////
		
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args) {
		new ToolTesting3();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
		
		bodies.get(0).moveTo(mx, my);

		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if(!pressed) {
			pullPoint = new Point(mx, my);
		}
		
		pressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		pressed = false;
	}

}
