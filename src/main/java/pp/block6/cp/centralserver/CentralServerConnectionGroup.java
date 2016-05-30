package pp.block6.cp.centralserver;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import pp.block6.cp.Message;

public class CentralServerConnectionGroup implements Runnable {

    private List<CentralServerConnection> registeredConnections;
    private CentralServerConnection controlConnection;
    private RequestDatabase requestDatabase;

    private Thread grabThread;
    private PacketGrabber packetGrabber;

    private final String id;

    public CentralServerConnectionGroup(String i, RequestDatabase rdb) {
        id = i;
        requestDatabase = rdb;
        registeredConnections = new CopyOnWriteArrayList<>();
        
        Thread centralServerConnectionGroupManager = new Thread(this);
        centralServerConnectionGroupManager.start();
        System.out.println("Created CentralServerConnectionGroup for id: " + id);
    }

    public synchronized void insert(Socket sock) {

        try {
            sock.setSoTimeout(1);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        registeredConnections.add(new CentralServerConnection(sock));
    }

    private final double growthFactor = 1.05;
    private final int maxBurstSize = 1000;
    private final int initSize = 10;
    
    private final double fractionCorrect = 0.9;
    
    private final int totalPacketsToSend = 50000;
    private final long timeoutPeriod = 15 * 1000;

    private synchronized void runTest() {

        int regsize = registeredConnections.size();
        int burst = 0;
        int totalSent = 0;
        long timeSpent = 0;
        boolean abortTest = false;

        //Supply some info on the test
        controlConnection.write("info","Registered connections: " + regsize);
        controlConnection.write("info","Burst growth factor: " + growthFactor);
        controlConnection.write("info","Burst initial size: " + initSize);
        controlConnection.write("info","Target packet amount: " + totalPacketsToSend);
        controlConnection.write("info","Required correctness: " + fractionCorrect);
        controlConnection.write("info","Server-side timeout period: " + timeoutPeriod + "ms");
        

        System.out.println("Starting test for id: " + id + " on " + regsize + " connections");
        // Don't listen for packets , the PackageGrabber does this for us
        grabThread.start();
        long beginTime = System.currentTimeMillis();
        
        
        // Send all the packets which constitute the test
        while(!abortTest) {
            int burstSize = Math.min(initSize * (int) Math.pow(growthFactor, burst++), maxBurstSize);

            //Check whether we are not exceeding the maximum
            if(totalSent > totalPacketsToSend) {
                abortTest = true;
                System.out.println("Sent all packages!");
                break;
            }
            
            //Send the packets
            for (int packet = 0; packet < burstSize; packet++) {
                registeredConnections.get(packet % regsize).write(requestDatabase.getRequest());
                totalSent++;
            }
            
            //Wait for everything to be answered
            while(packetGrabber.getReceivedPackets() <= totalSent * fractionCorrect) {
                Thread.yield();
                
                timeSpent = System.currentTimeMillis() - beginTime;
                
                if(timeSpent > timeoutPeriod) {
                    abortTest = true;
                    System.out.println("Server-side timeout: aborting test");
                    break;
                }
            }
            
            controlConnection.write("status","Correct answers so far: " + packetGrabber.getReceivedPackets() + "/" + totalPacketsToSend);
        }

        if (packetGrabber.getReceivedPackets() >= totalPacketsToSend * fractionCorrect) {
            controlConnection.write("result","Success!");
            controlConnection.write("result", "Performed the test in " + timeSpent + " ms");
        } else {
            controlConnection.write("result","Test timed out");
        }

        controlConnection.write("result","Total requests sent: " + totalSent);
        controlConnection.write("result","Total responses received: " + packetGrabber.getReceivedPackets());
        controlConnection.write("result","Concurrent connections accepted: " + regsize);
        controlConnection.write("result","stop");
        
        // Perform reset of variables after the test
        // Stop checking for messages
        packetGrabber.setStop();

        // Close and discard all sockets
        for (CentralServerConnection c : registeredConnections) {
            c.close();
        }
        registeredConnections.clear();

        System.out.println("Cleaned up CentralServerConnectionGroup for id: " + id);
    }

    @Override
    public void run() {

        while (true) {

            for (int i = 0; i < registeredConnections.size(); i++) {

                CentralServerConnection c = registeredConnections.get(i);

                // Read a packet from the test web server
                try {
                    Message message = c.read();

                    System.out.println(message);

                    if (message.getContent().equals("start")) {
                        // Separate the control connection from the rest.
                        controlConnection = c;
                        packetGrabber = new PacketGrabber(registeredConnections, controlConnection, requestDatabase);
                        grabThread = new Thread(packetGrabber);
                        registeredConnections.remove(i);

                        // Unleash hell.
                        runTest();
                    }

                } catch (IOException e) {
                    // System.err.println("Empty input buffer");
                } catch (ParseException e) {
                    System.err.println("Invalid message");
                }
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}