package pp.block6.cp.centralserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import pp.block6.cp.Message;

/*
 * Example code for the central testing server.
 * This will be used to test your implementations of an asynchronous proxy
 * 
 * It's an example of a somewhat complex concurrent program with transfer of ownership.
 * However, I haven't been very active at adding comments, but I thought it was useful to 
 * put it on blackboard anyway.
 */

public class CentralServer {

    private Map<String, CentralServerConnectionGroup> connectionMap = new HashMap<>();
    private ServerSocket listenSocket;
    private RequestDatabase requestDatabase;

    CentralServer(int port) throws IOException {
        listenSocket = new ServerSocket(port);
        requestDatabase = new RequestDatabase();
    }

    private class ConnectionWaiter implements Runnable {

        private Socket socket;
        private CentralServerConnection connection;

        public ConnectionWaiter(Socket s) throws IOException {
            socket = s;
            connection = new CentralServerConnection(socket);
        }

        @Override
        public void run() {

            try {
                Message message = connection.readWithWait();

                String id = message.getIdentifier();
                String content = message.getContent();

                if (content.equals("setup")) {

                    synchronized (connectionMap) {
                        if (!connectionMap.containsKey(id)) {
                            connectionMap.put(id, new CentralServerConnectionGroup(id, requestDatabase));
                        }
                        connectionMap.get(id).insert(socket);
                    }

                } else {
                    throw new ParseException("Not a setup packet", 0);
                }

            } catch (IOException e) {
                System.err.println("Took too long to start read the setup message");
            } catch (ParseException e) {
                System.err.println("Invalid packet, discarding socket");
                // Close the socket whenever something went wrong
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();

            }
        }
    }

    public void acceptConnections() {
        while (true) {
            try {
                Socket clientSocket = listenSocket.accept();
                Thread t = new Thread(new ConnectionWaiter(clientSocket));
                t.start();
            } catch (IOException e) {
                System.err.println("OVERFLOW OF FILE DESCRIPTORS!");
                connectionMap.clear();
            }
        }
    }

    public static void main(String[] args) {
        
        if(args.length < 1) {
            System.err.println("Invalid cmd arugments: CentralServer [port]");
        }
        
        int portNumber = Integer.parseInt(args[0]);
        System.out.println("port: " + portNumber);
        
        try {
            CentralServer server = new CentralServer(portNumber);
            server.acceptConnections();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}