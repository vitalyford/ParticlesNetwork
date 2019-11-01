import javax.swing.*;
import BreezySwing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

/**
 * This Particle game allows the user to interact with a network of randomly moving particles.
 * The user can click on the canvas to show the mouse circle and click again to hide it.
 * The user can also change the minimum distance in pixels defining when to draw edges.
 * 
 * @author Vitaly Ford
 *
 */
class Particle {
    int x;
    int y;
    int r;
    double angle;
    
    Particle(int x, int y, int radius, double angle) {
        this.x = x;
        this.y = y;
        this.r = radius;
        this.angle = angle * 2 * Math.PI; // convert to radians
    }
    
    void move(double dist, int width, int height) {
        x = (int)(x + Math.cos(angle) * dist);
        y = (int)(y + Math.sin(angle) * dist);
        if (x <= 0 || x > width || y <= 0 || y > height) {
            Random rand = new Random();
            x = rand.nextInt(width);
            y = rand.nextInt(height);
            angle = rand.nextDouble() * 2 * Math.PI; // in radians
        }
    }
}

public class GPanelForGame extends GBPanel implements ActionListener {
    
    // -----------YOU CAN PLAY WITH THESE PARAMETERS----------
    private final int N = 250;    // total number of particles at this level
    private final int STEP = 2;   // how many pixels the particles move through at a time
    private final int TIMER = 35; // how often the canvas is going to be updated in ms, defining the game speed
    private final double MOUSE_RADIUS = 50.0;  // the virtual size of the mouse for the sake of collision fun
    private final int MAX_PARTICLE_RADIUS = 8; // the largest size of the particle
    private final float EDGE_THICKNESS = 5.5f; // maximum thickness of edges
    private int MIN_DIST_TO_DRAW_EDGES = 100;  // distance in pixels defining when to draw edges
    private final Color EDGES_COLOR    = Color.WHITE;
    private final Color PARTICLE_COLOR = Color.WHITE;
    private final Color MOUSE_COLOR    = Color.BLUE;
    // -------------END PLAYING WITH PARAMETERS---------------
	
	// Add a label for scoring
	private JLabel scoreLabel = addLabel("Mouse coordinates: ...", 1,1,1,1);
	
	private List<Shape> shapes;
	private boolean start, done, mousePressed;
    private ArrayList<Particle> particles;
    private Random rand; // just a randomizer for particles parameters
    private Timer timer; // this timer is used to repaint the panel so that it will look like the ground is moving 
    private int mouseX, mouseY; // coordinates of the mouse
    
	public void start() {
	    mouseX = -100;
	    mouseY = -100;
	    
	    createParticles();
	    
		mousePressed = false;
		start        = true;
		done         = false;
		
		shapes = new ArrayList<Shape>(); // saving all shapes that we need to draw
		
		scoreLabel.setText("Mouse coordinates: ...");
    	
		// Set the timer for repainting the game, it defines the speed of the game in this case
    	timer = new Timer(TIMER, this); 
    	timer.start();
    	
    	// Set focus on the panel -> the panel will be forced to be focused on
    	// Focusing on the panel will allow for reading keyboard presses
    	this.setFocusable(true);
    	this.requestFocusInWindow();
	}
	
	public void stop() {
	    done = true;
	    start = false;
	}
	
	public void changeMinDistToDrawEdges(int minDist) {
	    if (minDist >= 10 && minDist <= 400)
	        MIN_DIST_TO_DRAW_EDGES = minDist;
	}

    public GPanelForGame(Color color) {
    	// Initializing all variables in this constructor
        setBackground(color);
        start = mousePressed = false;
        done  = true;
    }
    
    private void createParticles() {
    	rand = new Random();
    	int w = this.getWidth();
    	int h = this.getHeight();
    	particles = new ArrayList<Particle>();
    	for (int i = 0; i < N; i++) {
    	    particles.add(new Particle(rand.nextInt(w), rand.nextInt(h), rand.nextInt(MAX_PARTICLE_RADIUS) + 2, rand.nextDouble()));
    	}
    }
    
    private void moveParticles() {
        for (Particle p : particles) {
            p.move(STEP, this.getWidth(), this.getHeight());
            // Process collisions with the mouse
            if (mouseX > 0 && mouseX < this.getWidth() && mouseY > 0 && mouseY < this.getHeight()) {
                Ellipse2D mouseCircle = new Ellipse2D.Double(mouseX - MOUSE_RADIUS, mouseY - MOUSE_RADIUS, MOUSE_RADIUS * 2, MOUSE_RADIUS * 2);
                Rectangle2D squaredParticle = new Rectangle2D.Double(p.x, p.y, p.r * 2, p.r * 2);
                if (mouseCircle.intersects(squaredParticle)) {
                    p.angle += Math.PI;
                    p.move(STEP + MOUSE_RADIUS * 4, this.getWidth(), this.getHeight());
                }
            }
        }
    }
    
    // This method is called when the timer is activated
    public void actionPerformed(ActionEvent ev) {
    	// Repaint the whole panel when the timer is kicked and the user pressed Start button
    	if (ev.getSource() == timer && start) {
    	    // Move the particles towards the angle set separately for each of them
    	    moveParticles();
    	    // Repaint the whole canvas by calling paintComponent
    		repaint();
    		// Update the number of particles
            scoreLabel.setText("Mouse coordinates: (" + Integer.toString(mouseX) + ", " + Integer.toString(mouseY) + ") ¯\\_(ツ)_/¯");
    	}

        // If the user won or lost or stopped the app, we stop the timer
        if (done) {
        	timer.stop();
        }
    }

    public void paintComponent(Graphics g) {
    	if (!start) return; // if we did not start yet, don't even try to draw
    	
        // Redraw the whole app, it will clear up all the previous drawings
        super.paintComponent(g);
        
    	shapes.clear(); // clear the shapes

    	// Add particles to the shapes
    	for (int i = 0; i < N; i++) {
    		shapes.add(new Ellipse2D.Double(particles.get(i).x, particles.get(i).y, particles.get(i).r * 2, particles.get(i).r * 2)); // x, y, width, height
    	}
    	
    	// Set color before drawing all particles
        g.setColor(PARTICLE_COLOR);
        // Add all shapes (particles) to the panel on the screen
        for (Shape s : shapes) {
        	((Graphics2D)g).fill(s);
        }
        
        shapes.clear(); // clear the shapes
        
        // Draw the edges if necessary
        drawEdges(g, shapes, MIN_DIST_TO_DRAW_EDGES);
        
        // Draw the mouse circle if the mouse is pressed
        if (mousePressed) {
            g.setColor(MOUSE_COLOR);
            ((Graphics2D)g).fill(new Ellipse2D.Double(mouseX - MOUSE_RADIUS, mouseY - MOUSE_RADIUS, MOUSE_RADIUS * 2, MOUSE_RADIUS * 2));
        }
    }
    
    private float getDist(Particle p1, Particle p2) {
        return (float)(Math.sqrt(Math.pow(p1.x + p1.r - p2.x - p2.r, 2.0) + Math.pow(p1.y + p1.r - p2.y - p2.r, 2.0)));
    }
    
    private void drawEdges(Graphics g, List<Shape> shapes, float minDistanceToDraw) {
    	g.setColor(EDGES_COLOR); // set the color for the edges
    	for (int i = 0; i < N; i++) {
    	    for (int j = i + 1; j < N; j++) {
    	        float dist = getDist(particles.get(i), particles.get(j));
    	        if (dist < minDistanceToDraw) {
    	            ((Graphics2D)g).setStroke(new BasicStroke(EDGE_THICKNESS - (dist / minDistanceToDraw) * EDGE_THICKNESS));
    	            ((Graphics2D)g).draw(new Line2D.Double(particles.get(i).x + particles.get(i).r, particles.get(i).y + particles.get(i).r, particles.get(j).x + particles.get(j).r, particles.get(j).y + particles.get(j).r));
    	        }
    	        // check if the particles are collided and then change their angles
    	        if (dist < particles.get(i).r + particles.get(j).r) {
    	            particles.get(i).angle = 2 * particles.get(j).angle - particles.get(i).angle;
    	            particles.get(j).angle = 2 * particles.get(i).angle - particles.get(j).angle;
    	        }
    	    }
    	}
    }
    
    public void mouseMoved(int x, int y) {
        mouseX = x;
        mouseY = y;
    }
    
    public void mouseClicked(int x, int y) {
    	mousePressed = !mousePressed;
    }
    
    // Process keyboard presses
    protected void processComponentKeyEvent(KeyEvent e) {
    	// Check what event happened on the keyboard and then check what key was pressed
    	switch(e.getID()) {
    	case KeyEvent.KEY_PRESSED:
    		break;
    	case KeyEvent.KEY_RELEASED:
    		break;
    	}
    	// Make sure that all other elements are aware of this key press
    	super.processComponentKeyEvent(e);
    }

}

