package pp.block6.cp.server;

import pp.block6.cp.Connection;

public class ServerConnection extends Connection {
    
    public ServerConnection(String host, int port, String id) {
        super(host, port, id);
        write("setup");
    }
}
