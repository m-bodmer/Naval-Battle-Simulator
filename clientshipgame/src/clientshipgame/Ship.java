package clientshipgame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;

/**
 * Ship handles different ship types, and keeps track of a specific ships variables
 */
public class Ship {

    public static final int paintTimer = 1000/15; // 1000 milleseconds/40 frames per second = 25 milleseconds
    public static final int framesPerSecond = 1000/paintTimer;
    public static final int cannonballSpeed = 40*framesPerSecond; //Meters per second

    public int relativeXco;
    public int relativeYco;
    public int id;
    private int type;
    public double speed;
    public Point point = new Point();
    public Point cball = new Point();
    public double cballVectorx;
    public double cballVectory;
    public int heading;
    public int health;
    public int status;
    public boolean firing = false;
    public boolean cballExists = false;
    public int target;
    public double remainderx = 0;
    public double remaindery = 0;
    Game game;

    public static ArrayList<BufferedImage> sloop_normal = new ArrayList();
    public static ArrayList<BufferedImage> frigate_normal = new ArrayList();
    public static ArrayList<BufferedImage> manofwar_normal = new ArrayList();
    public static boolean initialized = false;

    BufferedImage cannonball = null;
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
        relativeXco = 0;
        relativeYco = 0;
        health = 100;
        game = g;
        ship = new ArrayList();
        BufferedImage sloopimg = null;
        BufferedImage frigateimg = null;
        BufferedImage mowimg = null;
        try {
            cannonball = ImageIO.read(this.getClass().getResourceAsStream("cannon_ball.png"));
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

    public double getAdjustedSpeed(){//This is used in the case of a discrepancy between client and server
        //To see who's calculations are right.
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

    public void moveShips() {

       double adjustedspeed = getSpeed(); 
      // double adjustedspeed = getAdjustedSpeed(); //Uncomment this line if the server does NOT sync up with our client.
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

       yadjust = yadjust * -1; //Calculated backwards.

       po = getPos();
       currentx = po.x;
       currenty = po.y;

       newx = currentx + (int)Math.round(xadjust); 
       newy = currenty + (int)Math.round(yadjust);

      // remainderx = remainderx + xadjust - (int)Math.round(xadjust);
       //remaindery = remaindery + yadjust - (int)Math.round(yadjust);

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

    public void computeVector(int i){
         int tid = game.shiplist.get(i).getTarget();
         int tspot = i;
         int head = 0;
         int theta = 0;
         int firex, firey;
         int tx, ty;
         int dx, dy;
         double dybydx;
         double pi = 3.14159265358979;
         Point distance = new Point();
         cballVectorx = 0;
         cballVectory = 0;
         head = game.shiplist.get(tspot).getHeading();
         distance = game.shiplist.get(tspot).getPos();
         firex = distance.x;
         firey = distance.y;

         distance = new Point();

         distance = game.shiplist.get(tid).getPos();
         tx = distance.x;
         ty = distance.y;

         dy = ty - firey; //Y distance from target to shooter
         dy = dy *-1;
         dx = tx - firex; //X distance from target to shooter
         //if(dx != 0)
         dybydx = (double)dy/(double)dx;
        // System.out.println(dy + "dy and dx is" + dx + "and dy/dx is" + dy/dx);
         System.out.println("Shp" + game.shiplist.get(i).id + "firing at "+ game.shiplist.get(i).target);
         head = (head * -1) + 90;
         if(head < 0){
            head = head + 360;
         }
         if(dy == 0 && dx == 0)
         {
             System.out.println("Target Firing at itself");
             return;
         }
         if(dx == 0)
         {
             if(dy > 0)
                 theta = 0;
             else
                 theta = 180;
         }
         else
         if(dy == 0)
         {
             if(dx > 0)
                 theta = 90;
             else
                 theta = -90;
         }
         else
         {
                if(dx < 0 && dy > 0)
                {
                    theta = (int)Math.round(Math.atan(Math.abs((float)dybydx))*180/pi) - head+90; 
                }
                else
                if(dx < 0 && dy < 0)
                {
                    theta = (int)Math.round(Math.atan(Math.abs((float)dybydx))*180/pi) - head+180; // Again, wrong quadrant
                }   
                else if(dx > 0 && dy < 0)
                {
                     theta = (int)Math.round(Math.atan(Math.abs((float)dybydx))*180/pi) - head+270;
                }

                else
                theta =  (int)Math.round(Math.atan(Math.abs((float)dybydx))*180/pi) -head+360; 
         }
         if(theta < 0)
             theta = theta * -1;
         //Now we have the angle of their boat relative to our ship.
         //The question is: Which way to we fire? left right or straight?

         System.out.println("Theta is " + theta + "and dybydx is" + dybydx);
           System.out.println("Heading is:" + head);
         if((theta >=-45 && theta <= 45) || (theta<= 405 && theta >= 315))//Fire straight
         {//Vector is equal to the vector of the heading of the ship
             System.out.println("Firing straight");
            // cballVectorx = Math.sin(theta*180/pi);
           //  cballVectory = Math.cos(theta*180/pi);

             cballVectorx = Math.cos(head*pi/180);
             cballVectory = -1*Math.sin(head*pi/180);
         }

         if(theta > 45 && theta <= 135) //Fire left
         {//Vector is perpendicular-left to the heading of the ship
             System.out.println("Firing left");
             double temp = 0;        
             cballVectorx = Math.cos(head*pi/180);
             temp = cballVectorx;
             cballVectory = -1*Math.sin(head*pi/180);
             cballVectorx = cballVectory;
             cballVectory = temp*-1;
         }

         if(theta >45+180 && theta <= 135+180) //Fire right.
         {//Vector is perpendicular-right to the heading of the ship
             System.out.println("Firing right");
             double temp = 0;
             cballVectorx = Math.cos(head*pi/180);
             temp = cballVectorx;
             cballVectory = -1*Math.sin(head*pi/180);
             cballVectorx = cballVectory;
             cballVectory = temp*-1;
         }
         game.shiplist.get(i).cball.x = game.shiplist.get(i).getPos().x;
         game.shiplist.get(i).cball.y = game.shiplist.get(i).getPos().y;
    }

    /**
     * Draw cannon balls to the screen
     * @param i
     * @param g 
     */
     public void drawCannonballs(int i, Graphics g){

          int xrel = 0;
          int yrel = 0;

        game.shiplist.get(i).cball.x = game.shiplist.get(i).cball.x + (int)((game.shiplist.get(i).cballVectorx/framesPerSecond)*cannonballSpeed);  
        game.shiplist.get(i).cball.y = game.shiplist.get(i).cball.y + (int)((game.shiplist.get(i).cballVectory/framesPerSecond)*cannonballSpeed); 

        xrel = game.shiplist.get(i).cball.x - getPos().x + (900/2);
        yrel = game.shiplist.get(i).cball.y - getPos().y + (650/2);
       // System.out.println(xrel + "and" + yrel);
        g.drawImage(cannonball, xrel, yrel, null);
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
           for(int i = 0; i < game.shiplist.size();i++)
                if(game.shiplist.get(i).firing == true)  //We understand that this ship is firing at someone.
                {
                    if(game.shiplist.get(i).target != game.shiplist.get(i).id)
                    {
                        System.out.println("Legit target. Computing vector");
                        computeVector(i);
                        game.shiplist.get(i).cballExists = true;
                    }
                    game.shiplist.get(i).firing = false;
                }


                if(ship == manofwar_normal)
                {
                    if (health <= 0)
                        g.drawImage(manofwar_sunk, (900/2) - (ship.get(correctedheading).getWidth()/2), (650/2)-(ship.get(correctedheading).getHeight()/2), null);
                    else g.drawImage(manofwar_normal.get(correctedheading), (900/2) - (ship.get(correctedheading).getWidth()/2), (650/2)-(ship.get(correctedheading).getHeight()/2), null);
                    //setHeading(heading-1);
                }

                if(ship == frigate_normal)
                {
                    if (health <= 0)
                        g.drawImage(frigate_sunk, (900/2) - (ship.get(correctedheading).getWidth()/2), (650/2)-(ship.get(correctedheading).getHeight()/2), null);
                    else g.drawImage(frigate_normal.get(correctedheading), (900/2) - (ship.get(correctedheading).getWidth()/2), (650/2)-(ship.get(correctedheading).getHeight()/2), null);
                    //setHeading(heading-2);
                }

                    if(ship == sloop_normal)
                {
                    if (health <= 0)
                        g.drawImage(sloop_sunk, (900/2) - (ship.get(correctedheading).getWidth()/2), (650/2)-(ship.get(correctedheading).getHeight()/2), null);
                    else g.drawImage(sloop_normal.get(correctedheading), (900/2) - (ship.get(correctedheading).getWidth()/2), (650/2)-(ship.get(correctedheading).getHeight()/2), null);
                    //setHeading(heading-3);
                }
                for(int i = 0; i < game.shiplist.size();i++)
                {
                    if(game.shiplist.get(i).cballExists)
                        drawCannonballs(i, g);
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
                    relativeXco = -(ship.get(correctedheading).getWidth()/2)+(900/2) + relativex;
                    relativeYco = -(ship.get(correctedheading).getHeight()/2)+(650/2) + relativey;
                    if (health <= 0)
                        g.drawImage(manofwar_sunk, -(ship.get(correctedheading).getWidth()/2)+(900/2) + relativex, -(ship.get(correctedheading).getHeight()/2)+(650/2) + relativey, null);      
                    else g.drawImage(manofwar_normal.get(correctedheading), -(ship.get(correctedheading).getWidth()/2)+(900/2) + relativex, -(ship.get(correctedheading).getHeight()/2)+(650/2) + relativey, null);
                   // g.drawImage(manofwar_normal.get(correctedheading), (900/2) + relativex, (650/2) + relativey, null);
                    //setHeading(heading-1);
                }

                if(ship == frigate_normal)
                {
                    if (health <= 0)
                        g.drawImage(frigate_sunk, relativex - (ship.get(correctedheading).getWidth()/2) + (900/2), relativey -(ship.get(correctedheading).getHeight()/2) + (650/2), null);
                    else g.drawImage(frigate_normal.get(correctedheading), relativex - (ship.get(correctedheading).getWidth()/2) + (900/2), relativey -(ship.get(correctedheading).getHeight()/2) + (650/2), null);
                    relativeXco = -(ship.get(correctedheading).getWidth()/2)+(900/2);
                    relativeYco = relativey -(ship.get(correctedheading).getHeight()/2) + (650/2);
                    //setHeading(heading-2);
                }

                    if(ship == sloop_normal)
                {
                    relativeXco = relativex + (900/2) - (ship.get(correctedheading).getWidth()/2);
                    relativeYco = relativey  + (650/2) - (ship.get(correctedheading).getHeight()/2);
                    if (health <= 0)
                        g.drawImage(sloop_sunk, relativex + (900/2) - (ship.get(correctedheading).getWidth()/2), relativey  + (650/2) - (ship.get(correctedheading).getHeight()/2), null);
                    else g.drawImage(sloop_normal.get(correctedheading), relativex + (900/2) - (ship.get(correctedheading).getWidth()/2), relativey  + (650/2) - (ship.get(correctedheading).getHeight()/2), null);
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
    }
}
