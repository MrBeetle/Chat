package client.rw;

import general.message.Message;
import general.message.MessageType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.AbstractMap;

public class CReader implements Runnable {

    private String name;
    private ObjectOutputStream out;
    private CWriter writer;
    private Thread thread;
    private boolean working; // if false - app will be stopped


    public CReader(String name, ObjectOutputStream out, CWriter writer) {
        this.name = name;
        this.out = out;
        this.writer = writer;
        working = true;

        thread = new Thread(this, name + "CReader");
        thread.start();
    }

    @Override
    public void run() {
        String line;
        try (BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in))){
            while (working) {
                keyboard.read();//wait for press enter for start typing message
                System.out.print(name + ": ");
                writer.startInput(); // stop input while typing message
                line = keyboard.readLine();
                if (line.startsWith("/")) {
                    processingCommand(line);
                } else {
                    out.writeObject(new Message(name, line, MessageType.text));
                    out.flush();
                }
                writer.endInput(); // continue input
            }
        } catch (SocketException se) {
            System.out.println("meow");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processingCommand(String command) {
        switch (command) {
            case "/stop":
                working = false;
                writer.close();
                break;
            case "/help":
                System.out.println(new Message(null, "/stop - stop app", MessageType.notice));
                break;
            default:
                System.out.println(new Message("System", "Incorrect command", MessageType.notice));
        }
    }

    public Thread getThread() { return thread; }
}
