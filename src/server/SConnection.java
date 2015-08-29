package server;

import general.message.Message;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class SConnection implements Runnable {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String name;
    private static boolean isInput = false;
    private static boolean working = true;

    public SConnection(Socket socket, String name, ObjectInputStream in, ObjectOutputStream out) {
        try {
            this.socket = socket;
            this.in = in;
            this.out = out;
            this.name = name;
            new Thread(this, name).start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            LinkedList<Message> messages = new LinkedList<>();
            while (working) {
                Message message = (Message) in.readObject();
                messages.addFirst(message);
                if (!isInput) {
                    while (messages.size() > 0) {
                        synchronized (System.out) {
                            System.out.println(messages.removeLast());
                        }
                    }
                    SListener.broadcast(message);
                }
            }
        } catch (Exception e) {
            try {
                in.close();
                if (!SListener.deleteStreamFromBroadcast(out)) {
                    throw new NoSuchElementException();
                }
                out.close();
                socket.close();
                System.out.println("Client " + name + " disconnected");
            } catch (IOException ioe) {
                e.printStackTrace();
            }
            
        }
    }

    public static void startInput() { isInput = true; }
    public static void endInput() { isInput = false; }
    public void close() {
        try {
            in.close();
            //out stream already close
            socket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }
}
