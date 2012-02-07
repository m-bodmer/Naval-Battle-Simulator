/*
 * TODO STILL:
 * 
 */

package clientshipgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ptorrens
 */
public class clientshipgame{

    public void parseargs(int port, String ip, String[] args){
    int arglength;

    arglength = args.length;
    if(arglength == 2)
    {
        port = Integer.parseInt(args[0]);
        ip = args[1];
    }
    else
    {
        System.out.println("Invalid arguments. Format = portnum IPadress.\n");
        System.exit(0);
    }

    }

    public static void main(String[] args) throws IOException {
        //int port = 5283;
        //String ip = "localhost";
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println ("Please enter in an ip");
        String ip = br.readLine();
        System.out.println ("Please enter in the port.");
        String portStr = br.readLine();
        int port = Integer.parseInt(portStr);
         
                
        int shiptype = 0;

        clientshipgame c = new clientshipgame();
        c.parseargs(port,ip, args);

        try {
             Game g = new Game (ip, port, shiptype);
             /*
             * In main I assume we are just going to make a game instance and run it. We also have to add a ready
             * state and what not since this is not covered by this skeleton.
             *
             */
        } catch (IOException ex) {
            Logger.getLogger(clientshipgame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
