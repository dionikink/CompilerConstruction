package pp.block3.cp.serverclient;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class RequestCountLogger extends TimerTask {
    @SuppressWarnings("unused")
    private int numConnections;
    private int numRequests;
    private Set<Integer> delaySet;
    
    public RequestCountLogger(int numConnections_) {
        numConnections = numConnections_;
        numRequests = 0;
        delaySet = new HashSet<Integer>();
        
        Timer timer = new Timer(true);
        timer.schedule(this, 0, 1000);
    }
    
    public void logRequest(int localThreadNumber, String request, 
                           String response, int delay) {
        numRequests++;
        delaySet.add(delay);
    }
    
    @Override
    public void run() { 
        long totalDelay = 0;
        int totalDelays = 0;
        
        for (Integer i : delaySet) {
            totalDelay += i;
            ++totalDelays;
        }
    
        float delay = (float) totalDelay / totalDelays;
        System.out.println("Handled requests: " + numRequests + 
                           "\t Average delay: " + delay + " us");
        
        numRequests = 0;
        delaySet.clear();
    }
}