package clientshipgame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Controller takes care of any key strokes and handles them appropriately
 * @author Marc
 */
public class Controller implements KeyListener {
    private Communicate com = null;
    
    /**
     * Contructor that initializes values
     * @param com 
     */
    Controller (Communicate com){
        this.com = com;
    }

    public void keyTyped(KeyEvent ke) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Send the keypress key to communicate
     * @param ke 
     */
    public void keyPressed(KeyEvent ke) {
        com.generateMessage(ke.getKeyCode());
    }

    public void keyReleased(KeyEvent ke) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
