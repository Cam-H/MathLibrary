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

import collision.SATCollision;
import functions.Trigonometry;
import shapes.Body;
import shapes.Component;
import shapes.Polygon;
import vectors.Point;
import vectors.Vector;

public class ToolTesting5 extends Thread implements MouseListener, MouseMotionListener{
	
	JFrame frame;
	
	Polygon p;
	
	Body b;
			
	boolean pressed = false;
	int mx = 0, my = 0;
	
	double theta = 0;
		
	boolean updateError = false;
	
	public ToolTesting5() {
		
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
		points.add(new Point(300, 100));
		points.add(new Point(200, 500));
		points.add(new Point(400, 900));
		points.add(new Point(150, 900));
		points.add(new Point(50, 500));

		
		List<Component> ps = new ArrayList<Component>();				
		ps.add(new Component(points));

//		p = new Polygon(points);
		
		b = new Body(ps);
		b.moveTo(frame.getWidth() / 2, frame.getHeight() / 2);


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
		b.render(g);
		
		g.setColor(new Color(0xff0000));
		System.out.println("udogauwida");
		if(b != null) {
			Polygon p = b.getComponents().get(0);
			List<Vector> edges = p.getEdgeList();

			Point concave = null;

			for(int i = 0; i < edges.size(); i++) {
				
				Vector a = edges.get(i).clone();a.flip();
				Vector b = edges.get(i < edges.size() - 1 ? i + 1 : 0);
				Vector c = a.unitVector();
				c.addVector(b.unitVector());
				c.scale(10);
				
//				c.offset(this.b.getCenterOfMass().x() + b.x0(), this.b.getCenterOfMass().y() + b.y0());
				double internalAngle = Math.acos(a.dotProduct(c) / a.magnitude() / c.magnitude()) + Math.acos(b.dotProduct(c) / b.magnitude() / c.magnitude());

				System.out.println(internalAngle);
				if(SATCollision.checkForCollision(p, 1, c.head.x(), c.head.y()) == null) {//The point is concave if the internal angle around the point is greater than 180
					System.err.println('a');
					concave = new Point(b.x0() + this.b.getCenterOfMass().x(), b.y0() + this.b.getCenterOfMass().y());
				}
				
				g.drawLine((int)c.x0(), (int)c.y0(), (int)c.x1(), (int)c.y1());
			}
			
			if(concave != null) {
				g.fillOval((int)concave.x() - 5, (int)concave.y() - 5, 10, 10);
			}
		}
		
		
		g.drawString(mx + " " + my, 100, 100);

		/////////////////////////////////////
		
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args) {
		new ToolTesting5();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mx = e.getX();
		my = e.getY();		
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
			
//			Body entity = new Body(Arrays.asList(outline.clone()));
//			entity.moveTo(mx, my);
//			
//			entities.add(entity);
		}
		
		pressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		pressed = false;
	}


}
