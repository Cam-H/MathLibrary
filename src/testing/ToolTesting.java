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

import collision.SATCollision;
import functions.Trigonometry;
import physics.Force;
import shapes.Body;
import shapes.Component;
import shapes.Polygon;
import vectors.Point;
import vectors.Vector;

public class ToolTesting extends Thread implements MouseListener, MouseMotionListener{
	
	JFrame frame;
	
	Polygon p;
	
	Body b;
	Body c;
	
	Vector thrust;
	Vector contact = new Vector();
	
	boolean pressed = false;
	Point pullPoint;
	int mx = 0, my = 0;
	
	double theta = 0;
	
	Vector overlap = new Vector();
	
	boolean updateError = false;
	
	public ToolTesting() {
		
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
		points.add(new Point(100, 100));
		points.add(new Point(400, 100));
		points.add(new Point(150, 200));

		List<Component> ps = new ArrayList<Component>();
		ps.add(new Component(points));
		
		points = new ArrayList<Point>();
		points.add(new Point(200, 200));
		points.add(new Point(400, 200));
		points.add(new Point(400, 400));
		points.add(new Point(200, 400));

		
		ps.add(new Component(points));
		
		b = new Body(ps);
		b.moveTo(frame.getWidth() / 2, frame.getHeight() / 2);
//		b.setVelocity(2, 0);
		
		List<Point> cps = new ArrayList<Point>();
		cps.add(new Point(100, 100));
		cps.add(new Point(100, 150));
		cps.add(new Point(150, 150));
		cps.add(new Point(150, 100));
		
		c = new Body(new ArrayList<Component>(Arrays.asList(new Component(cps))));

		
		thrust = new Vector(400, 400, 100, 100);

		while(true) {
			
			if(!updateError) {
				try {
					update();
				}catch(Exception e) {updateError = true;}
			}
			
			render();
			
			try {
				Thread.sleep(1000 / 60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void update() {

		b.update();
		c.update();
		
		b.removeForces();
		c.removeForces();

		if(pullPoint != null) {
			if(!pressed) {
				Vector velocity = new Vector(c.getCenterOfMass(), pullPoint);
				c.setVelocity(velocity.dx() / 100, velocity.dy() / 100);

				pullPoint = null;
			}	
		}
		
		b.checkCollision(c);
				
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
		c.render(g);
		
		g.setColor(new Color(0x000000));
		b.render(g);
		
		
		if(pullPoint != null) {
			g.drawLine((int)pullPoint.x(), (int)pullPoint.y(), mx, my);
		}
				
		g.setColor(new Color(0xff0000));
		g.drawLine((int)contact.tail.x(), (int)contact.tail.y(), (int)contact.head.x(), (int)contact.head.y());
		
//		g.drawLine((int)thrust.x0(), (int)thrust.y0(), (int)thrust.x1(), (int)thrust.y1());
		g.drawString(mx + " " + my, 100, 100);

		/////////////////////////////////////
		
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args) {
		new ToolTesting();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
		
		c.moveTo(mx, my);
		c.setVelocity(0, 0);
		
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
			pullPoint = new Point(mx, my);
		}
		
		pressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		pressed = false;
	}

}
