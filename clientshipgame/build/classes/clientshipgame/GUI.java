package clientshipgame;
import java.awt.image.*;
import java.awt.*; 
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
//Wood Panel taken from http://zygat3r.deviantart.com/art/Dark-Wood-58266349
//Compass taken from http://www.dreamstime.com/royalty-free-stock-image-compass-rose-illustration-image4389596

/*
 * TODO: Implement Game Log to recieve messages from the server
 */

/**
 * This is the main GUI class that handles all our images and draws applicable the applicable GUI on the screen
 */
public class GUI extends JPanel implements Runnable{
    //public Minimap minimap; Minimap object which we may or may not use
    //public BufferedImage wSock; Image to show wind speed and direction
    public JTextArea log;

    //Variables to display for Player Feedback
    public int health;
    public double speed;
    public int wSpeed;
    public int wDirection;
    public int heading;
    Game game;
    
    //GUI properties
    //private static final int UI_WIDTH = 1200;
    //private static final int UI_LENGTH = 850;
    BufferedImage button = null;
    BufferedImage wood_panel = null;
    BufferedImage ready_button = null;
    BufferedImage ready_button_hover = null;
    BufferedImage compass = null;
    BufferedImage compass_n = null;
    BufferedImage compass_ne = null;
    BufferedImage compass_e = null;
    BufferedImage compass_se = null;
    BufferedImage compass_s = null;
    BufferedImage compass_sw = null;
    BufferedImage compass_w = null;
    BufferedImage compass_nw = null;
    
    //Log Message strings
    String log_message_1 = "";
    String log_message_2 = "";
    String log_message_3 = "";
    String log_message_4 = "";
    String log_message_5 = "";
    
    /**
     * Constructor for GUI, initialize all required variables
     */
    public GUI(Game gm) {
        game = gm;
        
        Thread thread = new Thread (this);
        start(thread);
        try {
            wood_panel = ImageIO.read(this.getClass().getResourceAsStream("wood_panel2.png"));
            ready_button = ImageIO.read(this.getClass().getResourceAsStream("ready_button.png"));
            ready_button_hover = ImageIO.read(this.getClass().getResourceAsStream("ready_button_hover.png"));
            compass = ImageIO.read(this.getClass().getResourceAsStream("compass.png"));
//            compass_n = ImageIO.read(this.getClass().getResourceAsStream("compass_n.png"));
//            compass_ne = ImageIO.read(this.getClass().getResourceAsStream("compass_ne.png"));
//            compass_e = ImageIO.read(this.getClass().getResourceAsStream("compass_e.png"));
//            compass_se = ImageIO.read(this.getClass().getResourceAsStream("compass_se.png"));
//            compass_s = ImageIO.read(this.getClass().getResourceAsStream("compass_s.png"));
//            compass_sw = ImageIO.read(this.getClass().getResourceAsStream("compass_sw.png"));
//            compass_w = ImageIO.read(this.getClass().getResourceAsStream("compass_w.png"));
//            compass_nw = ImageIO.read(this.getClass().getResourceAsStream("compass_nw.png"));
            button = ready_button;
        } catch (IOException ex) {
            System.out.println("Error: Images not found!");
        }
        
        setPreferredSize(new Dimension(900,150));       
        setVisible(true);
    }
    
    public static void start (Thread thread){
        thread.start();
    }

    /**
     * Set our on screen display values for ship health, speed and heading
     */
    public void setShipInfo () {
        health = game.shiplist.get(0).getHealth();
        speed = game.shiplist.get(0).getSpeed();
        heading = game.shiplist.get(0).getHeading();
    }
    
    //This will display a passed in message to the game log
    public void setLogMessage(String message) {
        if (log_message_1.equals("")) {
            log_message_1 = message;
        } else if (log_message_2.equals("")) {
            log_message_2 = message;
        } else if (log_message_3.equals("")) {
            log_message_3 = message;
        } else if (log_message_4.equals("")) {
            log_message_4 = message;
        } else if (log_message_5.equals("")) {
            log_message_5 = message;
        } else { //Reset the messages and set log message 1
            log_message_1 = message;
            log_message_2 = "";
            log_message_3 = "";
            log_message_4 = "";
            log_message_5 = "";
        }
    }
    
    public void drawCompass(Graphics g) {
        //TODO Create images and find out direction values
        //If Direction is N
        //g.drawImage(compass_n, 20, 15, null);
        //If Direction is N-E
        //g.drawImage(compass_ne, 20, 15, null);
        //If Direction is E
        //g.drawImage(compass_north, 20, 15, null);
        //If Direction is E-S
        //g.drawImage(compass_se, 20, 15, null);
        //If Direction is S
        //g.drawImage(compass_s, 20, 15, null);
        //If Direction is S-W
        //g.drawImage(compass_sw, 20, 15, null);
        //If Direction is W
        //g.drawImage(compass_w, 20, 15, null);
        //If Direction is W-N
        //g.drawImage(compass_nw, 20, 15, null);
        g.setColor(Color.WHITE);
        g.fillRoundRect(17, 12, 140, 135, 10, 10);
        g.drawImage(compass, 19, 8, null);
    }

    /**
     * Draw all required images to the screen
     */
    @Override
    public void paint (Graphics g) {
        setShipInfo();
        g.drawImage(wood_panel, 0, 0, null);
        g.setColor(Color.lightGray);
        g.fillRect(197,17,406,121);
        g.setColor(Color.BLACK);
        g.fillRect(200,20,400,115);
        //g.fillRect(20,17,150,121);
         //Graphics2D a = (Graphics2D) british_flag.getGraphics();
         //a.rotate(1);
        
        //Draw compass with wind direction arrow
        drawCompass(g);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Helvetica", Font.BOLD,  16));
        
        if (game.clientisready == false){
            //Write this text to the game log
            g.drawString("Welcome, Captain, to the Drake!", 211, 50);
            g.drawString("Press Ready to Begin.", 211, 90);
            g.drawImage(button, 636, 20, null);
        }
        else {
            g.setColor(Color.lightGray);
            g.fillRect(635,17,176,121);
            g.setColor(Color.BLACK);
            g.fillRect(638,20,170,116);
            
            //This is where we draw game log messages
            g.setColor(Color.WHITE);
            g.drawString(log_message_1, 211, 45);
            g.drawString(log_message_2, 211, 65);
            g.drawString(log_message_3, 211, 85);
            g.drawString(log_message_4, 211, 105);
            g.drawString(log_message_5, 211, 125);
            
            //Ship Status Info
            g.setColor(Color.RED);
            g.setFont(new Font("Helvetica", Font.BOLD,  16));
            g.drawString("Ship Health: " + health, 645, 45);
            g.drawString("Ship Speed: " + speed, 645, 80);
            g.drawString("Ship Direction: " + heading, 645, 115);
        }
    }

    /**
     * Handles updates to the game and calls draw with the updated components
     */
    public void update() {
        repaint();
    }

    /**
     * Listens for key events and calls repaint when needed
     */
    public void run() {
        while (true){
            this.repaint();
            if ((game.mouseX >= 640 && game.mouseX <= 875) && (game.mouseY >=620 && game.mouseY <= 726)){
                button = ready_button_hover;
            }
            else button = ready_button;
            /*try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                System.out.println("Running Exception in GUI: " + ex);
            }*/
        }
    }
    
}
