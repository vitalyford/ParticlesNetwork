import javax.swing.*;
import BreezySwing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

/**
 * This GamePlay class starts a Particles game
 * This game uses BreezeSwing framework: http://home.wlu.edu/~lambertk/BreezySwing/index.html
 * 
 * @author Vitaly Ford
 *
 */
public class GamePlay extends GBFrame {

	GPanelForGame game = new GPanelForGame(Color.RED);
    GBPanel panel = addPanel(game, 1,1,4,1);
    JButton start = addButton("Start", 2,1,1,1);
    JButton stop = addButton("Stop", 2,2,1,1);
    IntegerField minDistToDrawEdges = addIntegerField(100, 2,3,1,1);
    JButton changeMinDist = addButton("Change min dist", 2,4,1,1);
    
    public void buttonClicked(JButton button) {
    	if (button == start) {
    		game.start(); // start the game when the user clicks on Start button
    	}
    	else if (button == stop) {
    	    game.stop(); // stop the game
    	}
    	else if (button == changeMinDist) {
    	    game.changeMinDistToDrawEdges(minDistToDrawEdges.getNumber());
    	}
    }

    public static void main (String[] args) {
    	// Create the frame for our game window
        JFrame frm = new GamePlay();
        // Set the size of our game window to full size
        frm.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Uncomment the following line to make it full-screen size for real
        //frm.setUndecorated(true);
        
        // Make it visible to the user
        frm.setVisible(true);
    }

}
