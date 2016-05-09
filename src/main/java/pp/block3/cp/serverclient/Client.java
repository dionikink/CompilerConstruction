package pp.block3.cp.serverclient;

import java.util.Vector;

public class Client {
    private int numConnections;
    private Vector<Thread> requesters = new Vector<Thread>();

    private int port;
    private String host;
    private static RequestCountLogger logger;
    private static ClientSettings settings;
    private static LocalCache cache;
    
    public Client(String host_, int port_, String settingsFilename) {
        host = host_;
        port = port_;
        
        settings = new ClientSettings(settingsFilename);
        numConnections = settings.getSettings().getNumConnections();
        logger = new RequestCountLogger(numConnections);  
        cache = new LocalCache(settings);
    }

    public void runClients() {
        // Create and start the threads
        for (int i = 0; i < numConnections; i++) {
            requesters.add(new Thread(new Requester(host, port, i, logger, 
                                                    settings, cache)));
            requesters.get(i).start();
        }

        // Join the threads (after they might have stopped due to the 
        // server terminating).
        for (int i = 0; i < numConnections; i++) {
            try {
                requesters.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Client c = new Client("192.168.1.2", 1025, "client.properties");
        c.runClients();
    }
}