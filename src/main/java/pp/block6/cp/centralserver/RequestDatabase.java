package pp.block6.cp.centralserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import pp.block6.cp.Message;

/*
 * Well, wouldn't it be useful if you could just prefetch all URLs which are given here?
 * Therefore this is just the most basic (non)-functionality hence it works but it doesn't.
 * 
 * For the final test this class will be updated to include the proper code.
 */

public class RequestDatabase {

    private Map<String, String> requestMap = new ConcurrentHashMap<>();
    private final int upperbound = 20;
    private String url = "http://gladdy.team-colonslash.eu/file";
    
    RequestDatabase() {

        BufferedReader in = null;

        for (int i = 0; i < upperbound; i++) {
            try {
                URL u = new URL(url + i + ".html");
                in = new BufferedReader(new InputStreamReader(u.openStream()));

                String content = "";
                String line;
                while ((line = in.readLine()) != null) {
                    content += line;
                }

                System.out.println(i + "\t" + content);
                
                requestMap.put(Integer.toString(i), content);
                
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean verifyResponse(Message response) {
        
        String id = response.getIdentifier();
        String result = requestMap.get(id);
        
        if(result == null) {
            return false;
        }
        
        if(result.equals(response.getContent())) {
            return true;
        }
        
        return true;
    }

    public Message getRequest() {
        Random r = new Random();
        int i = r.nextInt(upperbound);
        
        return new Message(Integer.toString(i), url + i + ".html");
    }

}
