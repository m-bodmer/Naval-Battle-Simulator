package clientshipgame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.audio.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;

public class Game extends JFrame implements Runnable{
   //Variables for Game
    public int playerid;
    public World world;
    public ArrayList<Ship> shiplist;    
    public Communicate com;
    public GUI gui;
    public Minimap mini;

    //Variables to share with other classes
    public boolean serverisready = false;
    public boolean clientisready = false;
    public boolean shipSelected = false;
    public int cannonTarget;
    public boolean firePressed = false;
    public boolean rightPressed = false;
    public boolean leftPressed = false;
    
    //Window Variables
    private static final int UI_HEIGHT = 750;
    private static final int UI_WIDTH = 900;

    //Mouse variables for detecting input
    public int mouseX;
    public int mouseY;
    
    AudioStream cannonSound = null;
    AudioStream bgdMusic = null;
    /*
     * Game constructor puts everything together and adds it onto a JFrame Window
     * Whenever the server pushes an update, Communicate will send a message to handleMessage.
     * From there, handle message will have to take the parsed info, and use it to update the game.
     *
     */
    public Game (String ip, int port, int shiptype) throws IOException{
        cannonSound = new AudioStream(this.getClass().getResourceAsStream("cannon_blast.wav"));
        bgdMusic = new AudioStream(this.getClass().getResourceAsStream("backgroundmusic.wav"));
        
        Ship s = new Ship(this);
        shiplist = new ArrayList();
        
        s.health = 100;
        shiplist.add(s);
        shiplist.get(0).setType(0);
        cannonTarget = 0;
        //Create a new world
        world = new World(this);
        //Create a new GUI which is the bottom bar
        gui = new GUI(this);
        //Create communicate which connects to server
        com = new Communicate(ip, 5283, this);
        //Create a new minimap
        mini = new Minimap(this);
        //Add all frames to JPanel
        setLayout(new BorderLayout());
        add(world, BorderLayout.CENTER);
        add(gui, BorderLayout.SOUTH);
        //Key Listeners are added for keys up down left right and space, which is sent to Communicate
        addKeyListener (new KeyListener() {
            public void keyTyped(KeyEvent ke) { 
            }

            public void keyPressed(KeyEvent ke) {
                int keyPress = ke.getKeyCode();
                if (keyPress == 32) {
                    try {
                        //Play cannon firing sound
                        cannonSound = new AudioStream(this.getClass().getResourceAsStream("cannon_blast.wav"));
                    } catch (IOException ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    AudioPlayer.player.start(cannonSound);
                    
                }
                com.generateMessage(keyPress);
            }
            public void keyReleased(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
		}

            }
        });
        //Mouse listeners
        addMouseListener (new MouseListener () {
            public void mouseClicked(MouseEvent me) {
                //Each if statement below is when we are selecting the ship
                if (shipSelected == false){
                    if ((mouseX > 255 && mouseX < 375) && (mouseY > 185 && mouseY < 265)){
                        shipSelected = true;
                        shiplist.get(0).setType(0);
                    }
                    else if ((mouseX > 405 && mouseX < 525) && (mouseY > 185 && mouseY < 265)){
                        shipSelected = true;
                        shiplist.get(0).setType(1);
                    }
                    else if ((mouseX > 555 && mouseX < 675) && (mouseY > 185 && mouseY < 265)){
                        shipSelected = true;
                        shiplist.get(0).setType(2);
                    }
                }
                //This if statement is for the ready button
                else if (serverisready == false) {
                    if ((mouseX >= 640 && mouseX <= 875) && (mouseY >=620 && mouseY <= 726)){
                        clientisready = true;                     
                    }
                }
                else {
                    for (int i = 1; i < shiplist.size(); i++){
                        int mouseX = me.getX();
                        int mouseY = me.getY();
                        int x =shiplist.get(i).relativeXco;
                        int y = shiplist.get(i).relativeYco;
                        if ((mouseX > x && mouseX < x + 120)&&(mouseY > y && mouseY < y+120)){
                            shiplist.get(0).target = shiplist.get(i).id;
                            gui.setLogMessage ("Enemy ship "+shiplist.get(i).id+" is selected.");
                        }
                    }
                }
            }
        
            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }
            
            public void mouseMoved (MouseEvent me) {
            }
        });
        
        addMouseMotionListener (new MouseMotionListener(){

            public void mouseDragged(MouseEvent me) {
            }
            //Updates the mouses x,y coordinates on the screen
            public void mouseMoved(MouseEvent me) {
                mouseX = me.getX();
                mouseY = me.getY();   
            }   
        });
        
        setTitle("Naval Battles Client: Drake");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(UI_WIDTH, UI_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Handle an incoming message from the server
     * @param message 
     */
    public void handleMessage(Message message){
        int i = 0;

        if (message.messageName.equalsIgnoreCase("register") || message.messageName.equalsIgnoreCase("registered"))//The server assigns us our ID.
        {
            if (message.args.size() > 0)
            {
                playerid = Integer.parseInt(message.args.get(0));
                shiplist.get(0).id = playerid; // Our ship is the first ship in the arraylist of ships, and was created
                                               // at the start of game.
              //  System.out.println("Our ship is: " + playerid);
            } else {
                System.out.println("The server has sent an errent message.");
            }
        }

        if (message.messageName.equalsIgnoreCase("start")){//the server has told us we're starting
            serverisready = true;
          Clip clip = null;
  
          AudioInputStream audioIn = null;
            try {
                 AudioStream soundFile = new AudioStream(this.getClass().getResourceAsStream("backgroundmusic.wav"));
                try {
                    audioIn = AudioSystem.getAudioInputStream(this.getClass().getResourceAsStream("backgroundmusic.wav"));
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                   clip = AudioSystem.getClip();
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    clip.open(audioIn);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } catch (IOException e) { System.out.println(e);
            }
        }
        //Reads in the integers from message until x, and stops
        //The points are then added into the shore and a polygon is drawn
        if (message.messageName.equalsIgnoreCase("shore")){
            if(message.args.get(0).equalsIgnoreCase("x") == false)
            {
                int xx = 0;
                int yy = 0;
                Polygon p = new Polygon();

                for(i = 1; i < message.args.size();i++)   {
                    xx = Integer.parseInt(message.args.get(i));
                    i++;
                    yy = Integer.parseInt(message.args.get(i));
                    p.addPoint(xx,yy);
                }
                world.shore.add(p);
            }
        }

        if (message.messageName.equalsIgnoreCase("wind")){
            if(message.args.size() == 2){
                world.changeWDir(Integer.parseInt(message.args.get(1)));
                world.changeWSpeed(Double.parseDouble(message.args.get(0)));
            }
            else
                System.out.println("Unexpected wind message from server");

        }

        if (message.messageName.equalsIgnoreCase("rain")){//Toggle rain
            if(message.args.size() == 1)
                world.changeRain(Integer.parseInt(message.args.get(0)));
            else
                System.out.println("Unexpected rain message from server.");
        }

        if (message.messageName.equalsIgnoreCase("fog")){//Toggle fog
            if (message.args.size() == 1)
                world.changeFog(Integer.parseInt(message.args.get(0)));
            else
                System.out.println("Unexpected fog message from server.");
        }
        
        if (message.messageName.equalsIgnoreCase("ship")){//Add a ship to the game.
            if(message.args.size() >= 2) {
                int idtag = Integer.parseInt(message.args.get(0));
                //If the ship is not the first(clients) ship
                if(idtag != shiplist.get(0).id)
                {
                    Ship s = new Ship(this);
                
                    s.id = idtag;
                    gui.setLogMessage ("Client ID " + s.id + " has connected");
                    System.out.println(s.id + "and" + shiplist.get(0).id);
                
                    s.setType(Integer.parseInt(message.args.get(1)));
                    shiplist.add(s);
                }
            } else {
                System.out.println("Unexpected Ship data from server");
            }
        }

        if (message.messageName.equalsIgnoreCase("shipstate")){
            Point p = new Point();
            int id = 0;
            if (message.args.size() > 5) {
                id = Integer.parseInt(message.args.get(0)); //Ship ID.
                p.x = Integer.parseInt(message.args.get(1)); // X position.
                p.y = Integer.parseInt(message.args.get(2)); // Y position.

                for(i = 0; i < shiplist.size();i++){
                    if(shiplist.get(i).id == id)
                    {
                        shiplist.get(i).setPos(p);
                        shiplist.get(i).setSpeed(Double.parseDouble(message.args.get(3)));//Ships Speed
                        shiplist.get(i).setHeading(Math.round(Float.parseFloat(message.args.get(4)))); //Ships Heading
                        shiplist.get(i).setHealth(Math.round(Float.parseFloat(message.args.get(5)))); //Ships Health
                    }           
                } 
            } else {
                 System.out.println("Bad Shipstate message from server."); // something went wrong with what the server sent us.
            }
        }

        if (message.messageName.equalsIgnoreCase("firing")){
            int id = 0;
            if(message.args.size() == 2) {
                id = Integer.parseInt(message.args.get(0));
                for(i = 0; i < shiplist.size();i++)
                {
                    if(shiplist.get(i).id == id)
                        break;
                }
                shiplist.get(i).firing = true;
                shiplist.get(i).target = Integer.parseInt(message.args.get(1));
            } else {
                System.out.println("Unclear fire message from server");
            }
        }
        
        if (message.messageName.equalsIgnoreCase("gameover")){
            gui.setLogMessage("Game is over. Thanks for playing!");
        }
    }

     /*private void shipVisible(Ship ship1, Ship ship2){

     }*/


    /**
     * Send key input to our communicate class
     * @param key 
     */
    public void sendInput(int key){
        /*Send to communicates generateMessage class. This is when the user hits a key.
         Get a boolean in return*/
        //System.out.println ("Sending input to com");
        boolean result = com.generateMessage (key);
    }
    
    /**
     * This is where everything is redrawn
     */ 
    public void run() {
        this.repaint(); 
    }


}
