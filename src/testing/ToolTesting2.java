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

public class ToolTesting2 extends Thread implements MouseListener, MouseMotionListener{
	
	JFrame frame;
	
	List<Body> bodies;
		
	boolean pressed = false;
	Point pullPoint;
	int mx = 0, my = 0;
	
	double theta = 0;
		
	public ToolTesting2() {
		
		frame = new JFrame();
		
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		
		frame.addMouseListener(this);
		frame.addMouseMotionListener(this);
		
		frame.setVisible(true);
		
		start();
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		
		bodies = new ArrayList<Body>();
		
		List<Component> ps = new ArrayList<Component>();
		
		List<Point> points = new ArrayList<Point>();
		
		points.add(new Point(200, 200));
		points.add(new Point(400, 200));
		points.add(new Point(400, 400));
		points.add(new Point(200, 400));
		ps.add(new Component(points));
		
		points = new ArrayList<Point>();
		points.add(new Point(400, 100));
		points.add(new Point(500, 100));
		points.add(new Point(500, 350));
		points.add(new Point(400, 350));
		ps.add(new Component(points));
		
		points = new ArrayList<Point>();
		points.add(new Point(500, 100));
		points.add(new Point(550, 100));
		points.add(new Point(550, 200));
		points.add(new Point(500, 250));
		ps.add(new Component(points));
		
		points = new ArrayList<Point>();
		points.add(new Point(400, 600));
		points.add(new Point(500, 600));
		points.add(new Point(500, 450));
		points.add(new Point(400, 450));
		ps.add(new Component(points));

		points = new ArrayList<Point>();
		points.add(new Point(500, 400));
		points.add(new Point(550, 400));
		points.add(new Point(550, 600));
		points.add(new Point(500, 600));
		ps.add(new Component(points));
		
		points = new ArrayList<Point>();
		points.add(new Point(550, 400));
		points.add(new Point(700, 400));
		points.add(new Point(700, 600));
		points.add(new Point(550, 600));
		ps.add(new Component(points));
		
		
		Body b = new Body(ps);
		b.moveTo(frame.getWidth() / 2, frame.getHeight() / 2);
		bodies.add(b);
		
		boolean testSplit = true;
		
		if(testSplit) {
			for(int z = 0; z < 1; z++) {
				List<Body> splits = bodies.get(z).attemptSplit();
				
				for(Body s : splits) {
					bodies.add(s);
				}

			}

			bodies.get(0).attemptSplit();
		}

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
		

		/////////////////////////////////////
		
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args) {
		new ToolTesting2();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
		
		bodies.get(1).moveTo(mx, my);

		
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
