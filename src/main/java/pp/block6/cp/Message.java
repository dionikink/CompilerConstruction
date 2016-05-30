package pp.block6.cp;

import java.text.ParseException;

/*
 * Parse, construct and some getters.
 * No setters, to maintain guaranteed thread safety : class is immutable
 */

public final class Message {

    private final String identifier;
    private final String content;
    protected final String splitToken = "\t"; 
    
    public Message(String msg) throws ParseException {
        
        if(!msg.contains(splitToken)) {
            throw new ParseException(msg, 0);
        }
        String[] parts = msg.split(splitToken);
        
        identifier = parts[0];
        
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 1; i < parts.length; i++) {
           strBuilder.append(parts[i]);
        }
        
        content = strBuilder.toString();
    }
    
    public Message(String i, String c) {
        identifier = i;
        content = c;
    }
    
    public String getContent() {
        return content;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public String toString() {
        return identifier+"\t"+content;
    }
}
