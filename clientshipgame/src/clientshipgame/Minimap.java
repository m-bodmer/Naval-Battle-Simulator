package clientshipgame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 * Draws a MiniMap window for the player to see
 */
public class Minimap  extends JFrame implements Runnable{
public double scale = 1/10;
public ArrayList<Polygon> scaledShoreList;
Game game;

public Minimap(Game g){
    game = g;
    setSize(new Dimension(500,400));       
    Thread thread = new Thread (this);
    start(thread);
    setVisible(true);
}

    public void run() {
        while(true)
        {
       try {
                Thread.currentThread().sleep(200);
            } catch (InterruptedException ex) {
                System.out.println("Error Occured: " + ex);
            }
        
        this.repaint();
        }
    }

    /**
     * Paints images to minimap frame
     * @param g 
     */
     public void paint (Graphics g) {
         int i = 0;
         BufferedImage ship;
         Polygon po = new Polygon();
         Polygon p2 = new Polygon();
         int x = 0;
         int y = 0;
              
         g.setColor(Color.blue);
         g.fillRect(0, 0, 600, 600);
         for( i = 0; i < game.shiplist.size();i++)
         {
             ship =  game.shiplist.get(0).ship.get(game.shiplist.get(i).getType());
             
             g.drawImage(game.shiplist.get(0).ship.get(game.shiplist.get(i).getType()), game.shiplist.get(i).getPos().x/10, game.shiplist.get(i).getPos().y/10, null);
         }
         
         g.setColor(Color.green);
       
         for(i = 0; i < game.world.shore.size();i++)
         {
             po = game.world.shore.get(i);
             for(int j = 0; j < po.npoints;j++)
             {
                 x =  po.xpoints[j]/10;
                 y = po.ypoints[j]/10+25;
                 
                 p2.addPoint(x, y);
             }
             
             g.fillPolygon(p2);
             po = new Polygon();
             p2 = new Polygon();
         }
         
         
     }

 public static void start (Thread thread){
        thread.start();
    }

public void update() {
        repaint();
    }

}
