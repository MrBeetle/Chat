package client.rw;

import general.message.Message;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.LinkedList;

public class CWriter implements Runnable {

    private String name;
    private ObjectInputStream in;
    private boolean isInput;
    private boolean working; // if false - app will be stopped
    private Thread thread;

    public CWriter(String name, ObjectInputStream in) {
        this.name = name;
        this.in = in;
        working = true;
        thread = new Thread(this, name + "CWriter");
        thread.start();
    }

    @Override
    public void run() {
        isInput = false;
        try {
            LinkedList<Message> messages = new LinkedList<>();
            while (working) {
                messages.addFirst((Message) in.readObject());
                if (!isInput) {
                    while (messages.size() > 0) {
                        Message message = messages.removeLast();
                        if (!message.getSender().equals(name)) {
                            System.out.println(message);
                        }
                    }
                }
            }
        } catch (SocketException se) {
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startInput() { isInput = true; }
    public void endInput() { isInput = false; }
    public void close() {
        working = false;
        try {
            in.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    public Thread getThread() { return thread; }
}
