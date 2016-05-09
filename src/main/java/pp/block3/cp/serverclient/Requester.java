package pp.block3.cp.serverclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class Requester implements Runnable {
    private String host;
    private int port;

    private Random rand;
    
    private int localThreadNumber;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    private RequestCountLogger logger;
    private ClientSettings settings;
    private LocalCache cache;

    /** Set up the thread. */ 
    public Requester(String host_, int port_, int localThreadNumber_, 
              RequestCountLogger logger_, ClientSettings settings_, 
              LocalCache cache_) {

        host = host_;
        port = port_;
        rand = new Random();
        
        localThreadNumber = localThreadNumber_;
        logger = logger_;
        settings = settings_;
        cache = cache_;

        try {
            socket = new Socket(host, port);
            input  = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            // Perform the request and get the response.
            try {
                long startTime = System.nanoTime();
                String request = "Hello from " + rand.nextInt(10);
                
                // Try to fetch a cached response first.
                String response = cache.get(request);
                if (response == null) {
                    output.writeUTF(request);
                    response = input.readUTF();
                    cache.put(request, response);
                }
                
                int delay = (int) (System.nanoTime() - startTime);
                logger.logRequest(localThreadNumber, request, response, delay / 1000);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        System.err.println("Socket not connected anymore.");
    }
}
