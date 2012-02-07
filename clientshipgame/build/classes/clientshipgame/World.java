
package clientshipgame;
import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.*;

/*
 * TODO: Meters to pixel conversion
 * TODO: Draw cannon balls using the algorithm for how we fire cannon balls
 * TODO: We have to check whether or not we draw ships in our viewing screen
 * TODO: Boundaries at edge of map
 * TODO: Scale Images to proper size (AKA Ships, Shore)
 */

/**
 * World class draws our the world environment to our viewing port
 */
public class World extends JPanel implements Runnable{

    public int wDirection;
    public double wSpeed;
    public int time;
    public int fog;
    public int rain;
    public ArrayList<Polygon> shore = new ArrayList();
    public double windMod[];
    public int height;
    public int width;
    Polygon marctotallyisntfatatall;
    public Game game;
    ArrayList<Polygon> offsetShoreList = new ArrayList();
    double currentdelay;

    //Drawing variables
    //BufferedImage water_background = null;
    BufferedImage selectFrigate = null;
    BufferedImage selectSloop = null;
    BufferedImage selectMOW = null;
    BufferedImage selectFrigateh = null;
    BufferedImage selectSlooph = null;
    BufferedImage selectMOWh = null;
    BufferedImage sloopStats = null;
    BufferedImage frigateStats = null;
    BufferedImage MOWStats = null;
    BufferedImage dawn_overlay = null;
    BufferedImage night_overlay = null;
    BufferedImage island_texture = null;
    BufferedImage [] water_background = new BufferedImage[3];
    int wave_image_counter = 0;
    InputStream in = null;
    public double radian_to_90_degrees = 1.57079633;
    long t1;
    long t2 = 0;
    long saved_time = System.currentTimeMillis();;
    boolean timerstarted = false;


    /**
     * TODO: Are we using this?
     */
    public void World(double wDir, double wSp, int time, int weState, int height, int width){

    }

    /**
     * Constructor for world. Initialize all needed variables and start required drawing threads.
     * @param game
     */
    public World (Game game) {
        this.game = game;

        Thread thread = new Thread (this);
        thread.start();
        try {
            //water_background = ImageIO.read(this.getClass().getResourceAsStream("water_background.png"));
            selectFrigate = ImageIO.read(this.getClass().getResourceAsStream("frigate_select.png"));
            selectFrigateh = ImageIO.read(this.getClass().getResourceAsStream("frigate_select_hover.png"));
            selectSloop = ImageIO.read(this.getClass().getResourceAsStream("sloop_select.png"));
            selectSlooph = ImageIO.read(this.getClass().getResourceAsStream("sloop_select_hover.png"));
            selectMOW = ImageIO.read(this.getClass().getResourceAsStream("man_of_war_select.png"));
            selectMOWh = ImageIO.read(this.getClass().getResourceAsStream("man_of_war_select_hover.png"));
            sloopStats = ImageIO.read(this.getClass().getResourceAsStream("sloop_stats.png"));
            frigateStats = ImageIO.read(this.getClass().getResourceAsStream("frigate_stats.png"));
            MOWStats = ImageIO.read(this.getClass().getResourceAsStream("manofwar_stats.png"));
            dawn_overlay = ImageIO.read(this.getClass().getResourceAsStream("dawn_overlay.png"));
            night_overlay = ImageIO.read(this.getClass().getResourceAsStream("night_overlay.png"));
            island_texture = ImageIO.read(this.getClass().getResourceAsStream("island_texture.png"));
            water_background[0] = ImageIO.read(this.getClass().getResourceAsStream("water_background.png"));;
            water_background[1] = ImageIO.read(this.getClass().getResourceAsStream("water_background_1.png"));;
            water_background[2] = ImageIO.read(this.getClass().getResourceAsStream("water_background_2.png"));;
        } catch (IOException ex) {
            System.out.println("Error: World images not found! " + ex);
        }
        getModValues();
        setPreferredSize(new Dimension(900,650));
        //TODO Added this to see if it would help with lag / smooth movement
        setDoubleBuffered(true);
        setVisible(true);
    }
    
    public void getModValues (){
        int i = 0;
        int size = 181;
        windMod = new double [size];
        try {
            Scanner scanner = new Scanner (getClass().getResourceAsStream("windmod.txt"));
            while (scanner.hasNextDouble()){
                scanner.nextDouble();
                windMod[i] = scanner.nextDouble();
                i++;
            }
        } catch (Exception e){
            System.out.println ("Error reading in Wind mod from text.");
            System.out.println (e);
        }
    }

    public Polygon addShore(ArrayList<Integer> pointlist){
        return marctotallyisntfatatall;
    }

    public void changeWDir(int dir){
        wDirection = dir;
    }

    public void changeWSpeed(double speed){
        wSpeed = speed;
    }

    public void changeTime(int time){
        this.time = time;
    }

    public void changeRain(int rainstate){
        rain = rainstate;
    }

    public void changeFog(int fogstate){
        fog = fogstate;
    }

    public void paint (Graphics g) {
        //drawWaves(g);
        createPolygons();
        moveShips();
        if (game.shiplist.get(0).getSpeed() == 0) {
            g.drawImage(water_background[0], 0, 0, null);
        } else {
            g.drawImage(water_background[wave_image_counter], 0, 0, null); 
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font("Helvetica", Font.BOLD,  30));
        if (game.shipSelected == false){
            g.drawString("SELECT YOUR SHIP", 332, 150);
            g.drawImage(sloopStats, 255, 246, null);
            g.drawImage(frigateStats, 405, 246, null);
            g.drawImage(MOWStats, 555, 246, null);
            if ((game.mouseX > 255 && game.mouseX < 375) && (game.mouseY > 185 && game.mouseY < 265)){
                g.drawImage (selectSlooph, 255, 185, null);
            } else {
                g.drawImage(selectSloop, 255, 185, null);
            }
            if ((game.mouseX > 405 && game.mouseX < 525) && (game.mouseY > 185 && game.mouseY < 265)){
                g.drawImage (selectFrigateh, 405, 185, null);
            } else {
                g.drawImage (selectFrigate, 405, 185, null);
            }
            if ((game.mouseX > 555 && game.mouseX < 675) && (game.mouseY > 185 && game.mouseY < 265)){
                g.drawImage (selectMOWh, 555, 185, null);
            } else {
                g.drawImage (selectMOW, 555, 185, null);
            }
        } else {
            drawShips(g);
            if(game.serverisready == true)
                drawPolygons(g);
           
            //TODO Time of day settings, implement when we can
//            if (time == 0) {
//              g.drawImage(dawn_overlay, 0, 0, null);  
//            } else if (time == 2) {
//              g.drawImage(dawn_overlay, 0, 0, null);  
//            }
          /*  try {
                Thread.currentThread().sleep(game.shiplist.get(0).paintTimer);
            } catch (InterruptedException ex) {
                System.out.println("Error Occured: " + ex);
            }*/
        }    
    }

    public void drawWaves (Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(200,20,400,115);
    }
    
    /**
     * Draws all current ships contained in our ship list to the screen
     */
    public void drawShips (Graphics g) {
        for (int i = 0; i < game.shiplist.size(); i++) {   
            game.shiplist.get(i).paint(g);
        }
    }
    
    public void moveShips(){
       int i = 0;
       
       for (i = 0; i < game.shiplist.size(); i++) {
           game.shiplist.get(i).moveShips();
       }       
    }

    /**
     * Create our points for shore from the info that we get from the server
     */
    public void createPolygons (){

        int k = 0;
        int j = 0;
        offsetShoreList.clear();
        Point po = new Point(); 
       
       // po = game.shiplist.get(0).getPos();
        po = game.shiplist.get(0).point;

       // System.out.println("pox is" +po.x);
        
      //  po.x = po.x - (game.shiplist.get(0).ship.get(0).getWidth())/2;
      //  po.y = po.y - (game.shiplist.get(0).ship.get(0).getHeight())/2;
        
       /* if(game.shiplist.get(0).getType() == 0)//Sloop
        {
            //TODO our Sloop: 60x60
            //specs : 20 x 20
            //Everything matches!           
            po.x = po.x - (game.shiplist.get(0).ship.get(game.shiplist.get(0).getHeading()).getWidth())/2;
            po.y = po.y - (game.shiplist.get(0).ship.get(game.shiplist.get(0).getHeading()).getHeight())/2;
        }
        if(game.shiplist.get(0).getType() == 1)//Frigate
        {
            //TODO our Frigate: 60 x 120
            //specs: 20 x 40
            po.x = po.x - 30;
            po.y = po.y - 60;
        }
        if(game.shiplist.get(0).getType() == 2)//MOW
        {
            //TODO our MOW : 60 x 240
            //specs : 20 x 80
            po.x = po.x - 30;
            po.y = po.y - 120;
        }*/
        
        //draw ship in center with these offsets.
        
        for( k = 0; k < shore.size(); k++)//for all shores
        {
            Polygon offsetp = new Polygon();
            for(j = 0; j < shore.get(k).npoints; j++) // for all points
            {
                //System.out.println(shore.get(k).xpoints[j] + "and" + shore.get(k).ypoints[j]);
                offsetp.addPoint(((900/2) - (po.x - shore.get(k).xpoints[j]) - game.shiplist.get(0).ship.get(0).getWidth()/2),((650/2) - (po.y - shore.get(k).ypoints[j]) - game.shiplist.get(0).ship.get(0).getHeight()/2));
                           // System.out.println("This shouldn't change: Offset of:" + (900/2-(po.x - shore.get(k).xpoints[0])));
                
            }

               // System.out.println("Po.x is" + po.x + "and po.y is" + po.y);
            offsetShoreList.add(offsetp);

        }

      
        
        /*
        for(all shores)
         * for(all points)
         *      first of all, the ship is in the middle of the screen. so everything is offset by -(width/2) and -(height/2) in meters.
         *      second - we are using locations relative to the center of the ship.
         *      A = point x of the polygon minus point x of the ship. gives us the offset in meters
         *      and since we are using a 900 meter by 650 meter screen, as well as a 900/650 pixle screen, the conversion is 1:1
         *      so we just draw the polygon at this displacement. so:
         *      A = A + 450
         *      If it goes off the end of the screen, who cares! add this point to the new polygon
         */ 
       //  po.x = po.x + (game.shiplist.get(0).ship.get(0).getWidth())/2;
      //   po.y = po.y + (game.shiplist.get(0).ship.get(0).getHeight())/2;
    }

    /**
     * Draw our shore polygons to the screen using our shore list we created earlier
     * @see createPolygons
     */
    public void drawPolygons (Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        TexturePaint green_texture = new TexturePaint(island_texture, new Rectangle(0, 0, 40, 40));
        g2d.setPaint(green_texture);
        //g2d.fillRect(10, 15, 90, 60);
        //g.setColor(Color.green);

        //g.fillPolygon (offsetShoreList.get(0));
        for(int j = 0; j < offsetShoreList.size(); j++)
        {
              g2d.fillPolygon(offsetShoreList.get(j));
        }
    
       // for(int j = 0; j < shore.size(); j++)
         //     g.fillPolygon (shore.get(j));
    
    }

    /**
     * Repaint all images on the screen
     */
    public void update() {
        repaint();
    }

    /**
     * Constant loop that continuously redraws all images to screen
     */
    public void run() {
        while (true){
            t1 = System.currentTimeMillis();
            if ((t1 - t2)  > game.shiplist.get(0).paintTimer){
                this.repaint();          
                t2 = t1;
            } else if (t1 - saved_time >= 3000) {
                saved_time = t1;
                if (wave_image_counter == 2) {
                    wave_image_counter = 0;
                } else {
                    wave_image_counter += 1;
                }            
            } else  {
                try {
                    Thread.currentThread().sleep(5);
                } catch (InterruptedException ex) {
                    System.out.println("Error Occured: " + ex);
                }
            }
            
        }
    }


}
