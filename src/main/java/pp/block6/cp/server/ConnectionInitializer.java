package pp.block6.cp.server;

import java.util.ArrayList;
import java.util.List;

public class ConnectionInitializer {

    private final String host;
    private final int port;
    private final String id;
    
    public ConnectionInitializer(String testHost, int testPort, String i) {
        host = testHost;
        port = testPort;
        id = i;
    }
    
    public List<ServerConnection> setupConnections(int numRequested) {
        
        ArrayList<ServerConnection> connectionContainer = new ArrayList<ServerConnection>(numRequested);
        
        for (int i = 0; i < numRequested; i++) {
            connectionContainer.add(new ServerConnection(host,port,id));
        }
        
        return connectionContainer;
    }
    
    public ServerConnection getControlConnection() {
        return new ServerConnection(host,port,id);
    }

}
