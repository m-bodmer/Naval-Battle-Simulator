
package clientshipgame;
import java.lang.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.util.ArrayList;




 

/**
 * Ship handles different ship types, and keeps track of a specific ships variables
 */
public class Ship {

    
public static final int paintTimer = 1000/40; // 1000 milleseconds/40 frames per second = 25 milleseconds
public static final int framesPerSecond = 1000/paintTimer;

public int id;
private int type;
public double speed;
public Point point = new Point();
Game game;
public int heading;
public int health;
public int status;
public boolean firing;
public int target;
public double remainderx = 0;
public double remaindery = 0;


public static ArrayList<BufferedImage> sloop_normal = new ArrayList();
public static ArrayList<BufferedImage> frigate_normal = new ArrayList();
public static ArrayList<BufferedImage> manofwar_normal = new ArrayList();
public static boolean initialized = false;


BufferedImage sloop_sunk = null;
BufferedImage frigate_sunk = null;
BufferedImage manofwar_sunk = null;
//I think ship might have to be made into an array list.
ArrayList<BufferedImage> ship = null;

/**
 * Ship constructor. Initializes all needed values
 * @param g
 */
public Ship(Game g){
    health = 100;
    game = g;
    ship = new ArrayList();
    BufferedImage sloopimg = null;
    BufferedImage frigateimg = null;
    BufferedImage mowimg = null;
        try {
            sloopimg = ImageIO.read(this.getClass().getResourceAsStream("sloop.png"));
            frigateimg = ImageIO.read(this.getClass().getResourceAsStream("frigate.png"));
            mowimg = ImageIO.read(this.getClass().getResourceAsStream("man_of_war.png"));
        } catch (IOException ex) {
            System.out.println("Error finding Ship Images " + ex);
        }
    

    
    try {
        if(initialized == false)
        {
            int i = 0;
            int j = 0;
            int k = 0;
            RotateImage r = new RotateImage();
                        
            
            for(j = 0; j < 4; j++)
            {
                if(j == 0)
                {
                    for(i = 0; i <= 90; i++)
                    {
                        if(i == 0)
                        {
                            sloop_normal.add(sloopimg);
                            frigate_normal.add(frigateimg);
                            manofwar_normal.add(mowimg);
                        }
                        else
                        {
                            sloop_normal.add(r.RotateImage(sloopimg,i));
                            frigate_normal.add(r.RotateImage(frigateimg,i));
                            manofwar_normal.add(r.RotateImage(mowimg,i));
                        }

                    }
                }
                else
                {
                    for(i = 1; i <91; i++)
                    {
                        sloop_normal.add(r.RotateImage(sloop_normal.get(i + ((j-1)*90)),90 ));
                        frigate_normal.add(r.RotateImage(frigate_normal.get(i + ((j-1)*90)),90 ));
                        manofwar_normal.add(r.RotateImage(manofwar_normal.get(i + ((j-1)*90)),90 ));
                    }
                    
                }
            }
            
            sloop_normal.add(r.RotateImage(sloop_normal.get(90),90));
            frigate_normal.add(r.RotateImage(sloop_normal.get(90),90));
            manofwar_normal.add(r.RotateImage(sloop_normal.get(90),90));
           
            
            for(i = 1; i <180; i++)
            {
                sloop_normal.add(r.RotateImage(sloop_normal.get(i),179));
                frigate_normal.add(r.RotateImage(frigate_normal.get(i),179));
                manofwar_normal.add(r.RotateImage(manofwar_normal.get(i),179));
            }
                
            
            //initialized = true;
        }
        sloop_sunk = ImageIO.read(this.getClass().getResourceAsStream("sloop_sunk.png"));
        frigate_sunk = ImageIO.read(this.getClass().getResourceAsStream("frigate_sunk.png"));
        manofwar_sunk = ImageIO.read(this.getClass().getResourceAsStream("man_of_war_sunk.png"));
    } catch (IOException ex) {
        System.out.println("Error: Ship images not found! " + ex);
    }
}

/**
 * Set the type of ship the user will be controlling
 * @param type
 */
public void setType(int type){
    this.type = type;
   //If ship is an arraylist, we can just do ship = sloop_normal. ect.
    if (type == 0) {
        ship = sloop_normal;
    } else if (type == 1) {
        ship = frigate_normal;
    } else if (type == 2) {
        ship = manofwar_normal;
    }
}

public void setSpeed(double speed){
    this.speed = speed;
}

public void setPos(Point p){
    this.point.x = p.x;
    this.point.y = p.y;
}

public void setHeading(int heading){
    if(heading > 180)        
        heading = heading - 360;
    if(heading < -180)
        heading = heading + 360;
    this.heading = heading;
}

public void setHealth(int health){
    this.health = health;

}

public void setStatus(int status){
    this.status = status;
}

public double getSpeed(){
    return speed;
}

public Point getPos(){
    return this.point;
}

public int getHeading(){
    return heading;
}

public int getHealth(){
    return health;
}

public int getStatus(){
    return status;
}

public int getType(){
    return type;
}
public boolean isFiring(){
    return firing;
}

public int getTarget(){
    return target;
}

public double getAdjustedSpeed(){
    
double newspeed = 0; 
int shipangle = getHeading();
int windangle = game.world.wDirection;
double currentspeed = getSpeed();


if(windangle > 180)
    windangle = windangle - 360; // turn it from (0,360) to (-180,180) so we can do math with it


int deltatheta = windangle-shipangle;

if(deltatheta < -180)
    deltatheta = deltatheta + 360;

if(deltatheta > 180)
    deltatheta = deltatheta - 360;

if(deltatheta < 0)
    deltatheta = deltatheta * -1;
    
if(currentspeed < 2 && currentspeed > -2)
{
    if(getType() == 0)//Sloop
        newspeed = 90.0 * currentspeed;

    if(getType() == 1) // Frigate
        newspeed = 70.0 * currentspeed;

    if(getType() == 2) // MOW
        newspeed = 50.0 * currentspeed;
}


return newspeed*game.world.windMod[deltatheta];    
}

public void moveShips()
{
   double adjustedspeed = getAdjustedSpeed();
   double xadjust = 0;
   double yadjust = 0;
   int currentx = 0;
   int currenty = 0;
   int newx = 0;
   int newy = 0;
   Point po = new Point();
   
   double pi = 3.14159265358979;
   xadjust = Math.sin((this.getHeading())*pi/180.0) * (adjustedspeed/framesPerSecond); 
   yadjust = Math.cos((this.getHeading())*pi/180.0) * (adjustedspeed/framesPerSecond);
   
   //System.out.println("x adjust calculated to be:" + xadjust + "    And yadjust calculated to be:" + yadjust);
   yadjust = yadjust * -1; //Calculated backwards.
   
   
   po = getPos();
   currentx = po.x;
   currenty = po.y;
   
      //System.out.println("Current x is:" + currentx + "       Calculated xadjust to be: " + xadjust);
   
   
   newx = currentx + (int)Math.round(xadjust); 
   newy = currenty + (int)Math.round(yadjust);
   
   
   
   remainderx = remainderx + xadjust - (int)Math.round(xadjust);
   remaindery = remaindery + yadjust - (int)Math.round(yadjust);
   
   if(remainderx > 1)
   {
       newx++;
       remainderx--;
   }
   if(remainderx < 1)
   {
       newx--;
       remainderx++;
   }
   if(remaindery > 1)
   {
       newy++;
       remaindery--;
   }
   if(remaindery < 1)
   {
       newy--;
       remaindery++;
   }
   
  
   
   po.x = newx;
   po.y = newy;
   
   setPos(po);
   
}

/**
 * Draws required images to screen 
 */
public void paint (Graphics g) {  
    

    int relativex = 0;
    int relativey = 0;
    
    int correctedheading = 0;
    correctedheading = this.getHeading();
    if(this.getHeading() < 0)
        correctedheading = this.getHeading() + 360;
    /*try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException ex) {
                System.out.println("Error Occured: " + ex);
            }*/
    //System.out.println (game.shiplist.get(this.id).point.x + game.shiplist.get(this.id).point.y);
   // System.out.println(point.x + "      " + point.y);
    
    
    
   if( game.shiplist.get(0).id == this.id && game.serverisready)//users ship
   {
      
            if(ship == manofwar_normal)
            {
                g.drawImage(manofwar_normal.get(correctedheading), (900/2) - (ship.get(correctedheading).getWidth()/2), (650/2)-(ship.get(correctedheading).getHeight()/2), null);
                //setHeading(heading-1);
            }

            if(ship == frigate_normal)
            {
                g.drawImage(frigate_normal.get(correctedheading), (900/2) - (ship.get(correctedheading).getWidth()/2), (650/2)-(ship.get(correctedheading).getHeight()/2), null);
                //setHeading(heading-2);
            }

                if(ship == sloop_normal)
            {
                g.drawImage(sloop_normal.get(correctedheading), (900/2) - (ship.get(correctedheading).getWidth()/2), (650/2)-(ship.get(correctedheading).getHeight()/2), null);
                //setHeading(heading-3);
            }
       
   }
   else
   {//other ship

      
            int playerx = 0;
            int playery = 0;
            playerx = game.shiplist.get(0).getPos().x;
            playery = game.shiplist.get(0).getPos().y;
            
            Point ppoint = new Point();
            ppoint = getPos();
            
            relativex = ppoint.x - playerx;
            relativey = ppoint.y - playery;
            
            if(ship == manofwar_normal)
            {
                g.drawImage(manofwar_normal.get(correctedheading), -(ship.get(correctedheading).getWidth()/2)+(900/2) + relativex, -(ship.get(correctedheading).getHeight()/2)+(650/2) + relativey, null);
               // g.drawImage(manofwar_normal.get(correctedheading), (900/2) + relativex, (650/2) + relativey, null);
                //setHeading(heading-1);
            }

            if(ship == frigate_normal)
            {
                g.drawImage(frigate_normal.get(correctedheading), relativex - (ship.get(correctedheading).getWidth()/2) + (900/2), relativey -(ship.get(correctedheading).getHeight()/2) + (650/2), null);
                //setHeading(heading-2);
            }

                if(ship == sloop_normal)
            {
                g.drawImage(sloop_normal.get(correctedheading), relativex + (900/2) - (ship.get(correctedheading).getWidth()/2), relativey  + (650/2) - (ship.get(correctedheading).getHeight()/2), null);
                //setHeading(heading-3);
            }
            
        
   }
    
    
    if(!game.serverisready && !game.clientisready)
    {
        if(ship == manofwar_normal)
        {
            g.drawImage(manofwar_normal.get(correctedheading), 550, 350, null);
            //setHeading(heading-1);
        }

        if(ship == frigate_normal)
        {
            g.drawImage(frigate_normal.get(correctedheading), 400, 350, null);
            //setHeading(heading-2);
        }

            if(ship == sloop_normal)
        {
            g.drawImage(sloop_normal.get(correctedheading), 250, 350, null);
            //setHeading(heading-3);
        }
    }
    else
    {
      
        
    }
    
    
    //g.drawImage(ship.get(correctedheading), 500, 400, null);
    if (health <= 0) {
       // g.drawImage(ship.get(this.heading), point.x, point.y, null);
    }
    
}

/**
 * Handles updates to the game and calls draw with the updated components
 */
public void update() {

}

}
