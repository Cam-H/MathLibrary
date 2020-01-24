package testing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import shapes.Body;
import shapes.Component;
import shapes.Polygon;
import vectors.Point;
import vectors.Vector;

public class ToolTesting4 extends Thread implements MouseListener, MouseMotionListener{
	
	JFrame frame;
	
	Polygon p;
	
	Body b;
	
	Component outline;
	List<Body> entities = new ArrayList<Body>();
	
	Body arena;
	
	Vector thrust;
	Vector contact = new Vector();
	
	boolean pressed = false;
	int mx = 0, my = 0;
	
	double theta = 0;
	
	Vector overlap = new Vector();
	
	boolean updateError = false;
	
	public ToolTesting4() {
		
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
		
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(0, 0));
		points.add(new Point(75, 0));
		points.add(new Point(75, 800));
		points.add(new Point(0, 800));

		
		List<Component> ps = new ArrayList<Component>();				
		ps.add(new Component(points));
		
		points = new ArrayList<Point>();
		points.add(new Point(0, 0));
		points.add(new Point(200, 0));
		points.add(new Point(200, 50));
		points.add(new Point(0, 50));

//		Component flipper = new Component(points);
//		flipper.scale(0.5);
//		
//		entities.add(new Body(Arrays.asList(flipper)));
//		entities.add(new Body(Arrays.asList(flipper.clone())));
//		entities.add(new Body(Arrays.asList(flipper.clone())));
//		entities.add(new Body(Arrays.asList(flipper.clone())));

		b = new Body(ps);
		b.moveTo(frame.getWidth() / 2, frame.getHeight() / 2);
		
		ps = new ArrayList<Component>();
		
		points = new ArrayList<Point>();
		points.add(new Point(-400, -25));
		points.add(new Point(400, -25));
		points.add(new Point(400, 25));
		points.add(new Point(-400, 25));
		
		Component edge = new Component(points, 0, -500);
		
		ps.add(edge);
		edge = edge.clone(); edge.setOffsets(0, 500);
		ps.add(edge);
		
		edge = edge.clone(); edge.rotateAbout(Math.PI / 2, edge.center()); edge.setOffsets(-500, 0);
		ps.add(edge);
		edge = edge.clone(); edge.setOffsets(500, 0);
		ps.add(edge);
//		thrusterShape.rotateAbout(Math.PI, thrusterShape.center());

		arena = new Body(ps);
		
		List<Point> cps = new ArrayList<Point>();
		cps.add(new Point(100, 100));
		cps.add(new Point(100, 150));
		cps.add(new Point(150, 150));
		cps.add(new Point(150, 100));
		
		outline = new Component(cps);

		
		thrust = new Vector(400, 400, 100, 100);

		while(true) {

			if(!updateError) {
				try {
					update();
				}catch(Exception e) {updateError = true;e.printStackTrace();}
			}
			
			try {
				render();
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(1000 / 60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void update() {
		
//		entities.get(0).moveTo(frame.getWidth() / 2 + 375, frame.getHeight() / 2 + 375);
//		entities.get(1).moveTo(frame.getWidth() / 2 - 375, frame.getHeight() / 2 + 375);
//		entities.get(2).moveTo(frame.getWidth() / 2 - 375, frame.getHeight() / 2 - 375);
//		entities.get(3).moveTo(frame.getWidth() / 2 + 375, frame.getHeight() / 2 - 375);
		
		b.update();
		b.removeForces();
		
		for(int i = 0; i < entities.size(); i++) {
			Body e = entities.get(i);
			
//			if(i < 4) {
//				e.setAngularVelocity(-Math.PI / 64);
//				e.setVelocity(0, 0);
//			}
			
			e.update();
			e.removeForces();
			
			b.checkCollision(e);
			arena.checkCollision(e);
			
			for(int j = 0; j < entities.size(); j++) {
				if(i == j) {
					continue;
				}
				
				e.checkCollision(entities.get(j));
			}
			
			Point CoM = e.getCenterOfMass();
			
			if(CoM.x() < 0 || CoM.x() > frame.getWidth() || CoM.y() < 0 || CoM.y() > frame.getHeight()) {
				entities.remove(i);
				i--;
			}

		}		

		b.setAngularVelocity(Math.PI / 32);
		b.setVelocity(0, 0);
		b.moveTo(frame.getWidth() / 2, frame.getHeight() / 2);
		
		
		arena.setAngularVelocity(0);
		arena.setVelocity(0, 0);
		arena.moveTo(frame.getWidth() / 2, frame.getHeight() / 2);
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
		for(Body e : entities) {
			e.render(g);
		}
		
		g.setColor(new Color(0x000000));
		b.render(g);
		
		g.setColor(new Color(0x000000));
		arena.render(g);
				
		g.setColor(new Color(0xff0000));
		g.drawLine((int)contact.tail.x(), (int)contact.tail.y(), (int)contact.head.x(), (int)contact.head.y());
		
		/////////////////////////////////////
		
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args) {
		new ToolTesting4();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
		
		thrust = new Vector(400, 400, mx, my);
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
			
			Body entity = new Body(Arrays.asList(outline.clone()));
			entity.moveTo(mx, my);
			
			entities.add(entity);
		}
		
		pressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		pressed = false;
	}

}
