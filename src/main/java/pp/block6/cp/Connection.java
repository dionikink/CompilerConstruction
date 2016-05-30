package pp.block6.cp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.text.ParseException;

import pp.block6.cp.Message;

/*
 * Wrapper around the socket, in order to take Messages with some utility methods.
 */

public class Connection {
    protected DataInputStream input;
    protected DataOutputStream output;
    protected Socket socket;
    protected final String identifier;
    
    public Connection(String host, int port, String i) {
        
        identifier = i;
        try {
            socket = new Socket(host,port);
            setTimeout();
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTimeout() throws SocketException {
        socket.setSoTimeout(5);
    }
    
    public Connection(Socket s) {
        
        socket = s;
        identifier = "empty";
        
        try {
            setTimeout();
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized Message readWithWait() throws IOException, ParseException {
        return new Message(input.readUTF());
    }
    
    public synchronized Message read() throws IOException, ParseException {
        if(input.available() > 0) {
            return new Message(input.readUTF());
        }
        throw new IOException();
    }
    
    public synchronized void write(Message msg) {
        try {
            output.writeUTF(msg.toString());
            output.flush();
        } catch (IOException e) {}
    }
    
    public void write(String content) {
        write(new Message(identifier,content));
    }
    
    public void write(String id, String content) {
        write(new Message(id,content));
    }
    
    public void close() {
        try {
            input.close();
            output.close();
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
