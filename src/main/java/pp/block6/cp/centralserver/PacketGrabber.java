package pp.block6.cp.centralserver;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import pp.block6.cp.Message;

public class PacketGrabber implements Runnable {

    private List<CentralServerConnection> connections;
    private volatile int receivedPackets;
    private RequestDatabase db;
    private CentralServerConnection controlConnection;
    
    
    public PacketGrabber(List<CentralServerConnection> cl,
            CentralServerConnection cc, RequestDatabase rdb) {

        connections = cl;
        controlConnection = cc;
        db = rdb;
        receivedPackets = 0;
    }
    
    public int getReceivedPackets() {
        return receivedPackets;
    }
    
    private boolean spin = true;
    
    public void setStop() {
        spin = false;
        System.out.println("Total packages correctly grabbed: " + receivedPackets);
    }

    @Override
    public void run() {
        
        while (spin) {
            
            for(int i = 0; i < connections.size(); i++) {
                CentralServerConnection c = connections.get(i);
                
                try {
                    Message response = c.read();
                    
                    if(db.verifyResponse(response)) {
                        receivedPackets++;
                    } else if(controlConnection != null) {
                        controlConnection.write(response.getIdentifier(),"Wrong response!");
                    } else {
                        System.err.println("unable to verify response");
                    }
                    
                } catch (IOException e) {
                    //Read timed out -> nothing to read
                } catch (ParseException e) {
                    //Malformed packet
                }
            }
        }
    }
}
