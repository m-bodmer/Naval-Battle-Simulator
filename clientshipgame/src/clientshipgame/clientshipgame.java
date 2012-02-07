package clientshipgame;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is our main class that accepts arguments if put in. Otherwise uses localhost and port 5283
 */
public class clientshipgame{

    /**
     * Parse arguments
     * @param port
     * @param ip
     * @param args
     * @return 
     */
    public boolean parseargs(int port, String ip, String[] args){
        int arglength;

        arglength = args.length;
        if(arglength == 2)
        {
            port = Integer.parseInt(args[0]);
            ip = args[1];
        }
        else
        {
            System.out.println("Using localhost and port 5283.\n");
            return false;
        }
        return true;
    }

    /*
     * Call game to start up our program
     */
    public static void main(String[] args) throws IOException {
        int port = 5283;
        String ip = "localhost";
        /*
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println ("Please enter in an ip");
        String ip = br.readLine();
        System.out.println ("Please enter in the port.");
        String portStr = br.readLine();
        int port = Integer.parseInt(portStr);
         */
                
        int shiptype = 0;

        clientshipgame c = new clientshipgame();
        if (c.parseargs(port,ip, args) == false){
            ip = "localhost";
            port = 5283;
        }

        try {
             Game g = new Game (ip, port, shiptype);
        } catch (IOException ex) {
            Logger.getLogger(clientshipgame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
