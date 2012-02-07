package clientshipgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 * This class contains message sending/recieving functions and is used to communicate with the server
 * @author Marc
 */
public class Communicate implements Runnable{

    int forkage = 0;
    PrintWriter oos = null;
    BufferedReader ois = null;
    Game game;
    Socket socket;
    String ipaddress;
    int portnum;
    Message m = new Message();
    int cannonTarget;
    String username = "Patisdaboss";
    Communicate c;
    long time = 0;
    double shipSpeed = 0;

    /**
     * Constructor that initializes all appropriate values
     * @param ip
     * @param port
     * @param g
     * @throws IOException 
     */
    public Communicate(String ip, int port, Game g) throws IOException{
        m.args = new ArrayList();
        portnum = port;
        ipaddress = ip;
        cannonTarget = 0;
        game = g;
        
        try {
            socket = new Socket (ipaddress, portnum);
            oos = new PrintWriter(socket.getOutputStream(), true);
            ois = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e){
            System.err.println ("Do not see host on server: "+ ipaddress);
            System.err.println (e);
            System.exit(0);
        } catch (IOException e){
            System.err.println ("Could not get I/O for the connection to: " + ipaddress);
            System.err.println (e);
            System.exit(0);
        }
        
        Thread t = new Thread(this);
        t.start();
   }


    /**
     * Takes care of which ship was selected and return the ship type
     * @return 
     */
    public int shipIncrement (){
        int type = game.shiplist.get(0).getType();
        if (type == 0) return 3;
        if (type == 1) return 2;
        if (type == 2) return 1;
        else return 0;
    }

    /**
     * Listen for new messages coming from the server
     */
    public void listen() {
        Message message = new Message();
        String servermessage = null;

        try {
            //Listen for near stuff
            while (true){
                if((servermessage = ois.readLine()) != null)
                {
                    message = parseMessage(servermessage);
                    game.handleMessage(message);
                }   
             }
        } catch (Exception e){
            System.err.println ("Error with sending and receiving");
            System.err.println (e);
        }
    }

    /**
     * Send the message to the server
     * @param message
     * @return 
     */
    public boolean sendMessage(String message){
        oos.println(message);
        System.out.println ("Sending message: "+message);
            game.shiplist.get(0).ship.get(Math.abs(game.shiplist.get(0).getHeading())).getHeight();
        return true;
        
      }
 
    /**
     * Create an appropriate message to send to the server
     * @param key
     * @return 
     */
    public boolean generateMessage (int key){
        String message = null;
        int increment = shipIncrement();
        //Pressed left key, negative increment
        //Check to see if 100ms has passed between sending fire
        if ((System.currentTimeMillis() - time)  > 100){
            if (key == 37) {
                message = "setHeading:"+(-1*increment)+";";
                //int heading = game.shiplist.get(0).getHeading() - shipIncrement();
                //game.shiplist.get(0).setHeading(heading--);
            }
            //Pressed up key, increase speed
            else if (key == 38) {
                shipSpeed = shipSpeed + 0.1;
                if (shipSpeed > 1.0) shipSpeed = 1.0;
                message = "speed:"+shipSpeed+";";
            }
            //Pressed right key, positive increment
            else if (key == 39) {
                message = "setHeading:"+increment+";";
                //int heading = game.shiplist.get(0).getHeading() + shipIncrement();
                //game.shiplist.get(0).setHeading(heading);
            }
            //Pressed down key, decrease speed        
            else if (key == 40) {
                shipSpeed = shipSpeed - 0.1;
                if (shipSpeed < 0) shipSpeed = 0.0;
                message = "speed:"+shipSpeed+";";
            }
            //Pressed space, fire
            else if (key == 32) {
                int target = 0;
                //Get position user wants to fire at
                int position = 0;
                
                game.firePressed = true;
                game.shiplist.get(0).firing = true;
                //message = "fire:"+target+":"+position+";";
                message = "fire:" + game.shiplist.get(0).target + ";";
                game.gui.setLogMessage("Firing at target: " + game.shiplist.get(0).target);
             }
            time = System.currentTimeMillis();
        }
        boolean sent = false;
        if (message != null){
            sent = sendMessage(message);
        }
        //Send message to server through sendMessage
        return sent;
    }
    
    /**
     * If gameState is true, we send a ready message, false we send a disconnect
     */
    public boolean generateMessage (boolean gameState){
        String message = null;
        
        if (gameState == true){
            message = "ready;";
        }
        else if (gameState == false){
            message = "disconnect;";
        }
        else System.err.println ("Boolean neither true or false");
        return (sendMessage (message));
    }

    /**
     * Parse a message recieved from the server
     * @param message
     * @return 
     */
    private Message parseMessage(String message){
        int i = 0;
        String temp = "";
        m.args = new ArrayList();

        StringTokenizer strtok = new StringTokenizer(message,"[:;,]");
        while(strtok.hasMoreTokens()){
            try{
                if(i == 0)
                {
                    m.messageName = strtok.nextToken();
                    i++;
                }
                else
                    temp = strtok.nextToken().toString();
                if(temp.equals("") == false)
                    m.args.add(temp);
            } catch(NullPointerException e){
                System.err.println ("Null Pointer Exception in parseMessage ");
                System.err.println (e);
                break;
            }
        }
        
        return m;
    }
    /**
     * We use the message Class to send message objects containing the message type, and an arraylist of strings
     * to communicate between communicate and Game.
     */
    public void run() {
        
        
        if(forkage == 0)// fork-exec another thread that will start listening.
        {
            forkage = 1;
             Thread t = new Thread(this);
            t.start();
        }
        else
            listen();
        
     
        while (true){//Send the server our shiptype once the user clicks a boat image.
            
            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException ex) {
                System.out.println("Error Occured: " + ex);
            }

            if (game.shipSelected) {
                String register = "register:"+game.shiplist.get(0).getType()+";";//+username+";";
                //Register
                System.out.println ("Registering with: "+register);
                try {
                    oos.println(register);
                } catch (Exception e){
                    System.out.println (e);
                }
                break;
            }   
        }
        
        while(true){//Wait until user clicks ready button.
            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException ex) {
                System.out.println("Error Occured: " + ex);
            }
            
            if (game.clientisready) {
                String ready = "ready;";
                //Register
                System.out.println ("Indicating to the server we are ready with message: "+ready);
                try {
                    oos.println(ready);
                } catch (Exception e){
                    System.out.println (e);
                }
                break;
            }
        }
    }
}
