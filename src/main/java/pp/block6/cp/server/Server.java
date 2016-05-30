package pp.block6.cp.server;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import pp.block6.cp.Message;

/*
 * The class you have to modify to work asynchronously and act as a proxy for GET requests
 * 
 * Current implementation: 
 * 1. It sets up the connections using the ConnectionInitializer
 * 2. It transmits the "start" packet to the server which does 2 things
 *      - identify that connection as the control connection (separating it from the rest)
 *      - start the test
 * 3. It handles the test. For every incoming request from the CentralServer, it transmits a response.
 * 
 *      For the real application the behaviour should be this:
 *    
 *      The requests will have the form "[IDENTIFIER]\t[URL]", 
 *      Example: "id1   d5.colonslash.eu" (in which there should be a tab character between the [ID] and [URL]
 *      
 *      The response you are supposed to give is then "[ID]\t[CONTENT]"
 *      Example: "id1   <html><body><h4>d5 :&gt;</h4></body></html>\n"
 *      
 * 4. After all requests have been handled (with a 90% accuracy to account for packet drop), the CentralServer transmits
 *      the statistics of the test and then "stop" over the control connection, which terminates the Server test
 */

public class Server {

    private ConnectionInitializer connectionInitializer;
    private List<ServerConnection> activeConnections;
    private Thread serverThread;
    private Cacher cacher;
    
    private ServerConnection controlConnection;
    private int packetCounter;
    
    public Server(String h, int p, String id) {
        connectionInitializer = new ConnectionInitializer(h, p, id);
        activeConnections = new ArrayList<>();
        cacher = new Cacher();
    }

    private class ServerThread implements Runnable {

        @Override
        public void run() {

            System.out.println("Started server thread");
            
            while (true) {
                
                //Read from the control socket
                if(controlConnection != null) {
                    try {
                        Message controlMessage = controlConnection.read();
                        if(controlMessage.getContent().equals("stop")) {
                            System.out.println("got a stop message");
                            break;
                        }
                        System.out.println(controlMessage);
                    } catch (IOException e) {
                        //Read timed out -> nothing to read
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                
                //Read from the request sockets (and write if you have something)
                for(int i = 0; i < activeConnections.size(); i++) {
                    ServerConnection c = activeConnections.get(i);
                    
                    try {
                        Message msg = c.read();
                        
                        c.write(cacher.getResponse(msg));
                        packetCounter++;
                    } catch (IOException e) {
                        //Read timed out -> nothing to read
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void startServing(int numConnections) {
        //Set up the main connections and start listening to the server
        activeConnections.addAll(connectionInitializer.setupConnections(numConnections));
        serverThread = new Thread(new ServerThread());
        serverThread.start();
        System.out.println("Started listening for requests");
        
        //Wait a bit to have all connections settle
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void startTest() {
        //Set up the control connection and start the test
        controlConnection = connectionInitializer.getControlConnection();
        controlConnection.write("start");
        System.out.println("Started test");
    }
    
    public void stop() {
        
        try {
            serverThread.join();
        } catch (InterruptedException e) {}

        activeConnections.clear();
        System.out.println("Local reponse counter (sent): " + packetCounter);
        System.out.println("Stopped test");
    }

    public static void main(String[] args) {
        Server server = new Server("192.168.2.114", 15611, "s1340921");
        server.startServing(20);
        server.startTest();
        
        server.stop();
    }
}
