package server;

import general.message.Message;
import general.message.MessageType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

class SReader implements Runnable {

    private boolean working; // if false - app will be stopped
    private SListener sListener;
    private Thread thread;

    public SReader(SListener sListener) {
        this.sListener = sListener;
        working = true;
        thread = new Thread(this, "Server reader");
        thread.start();
    }

    @Override
    public void run() {
        try (
                BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in))
            ) {
            String line;
            while (working) {
                keyboard.read();//wait for press enter for start typing message
                System.out.print("Server: ");
                SConnection.startInput(); // stop input while typing message
                line = keyboard.readLine();
                if (line != null && line.startsWith("/")) {
                    processingCommand(line);
                }
                SConnection.endInput(); // continue input
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processingCommand(String command) {
        switch (command) {
            case "/stop":
                working = false;
                sListener.close();
                break;
            case "/help":
                System.out.println(new Message(null, "/stop - stop app", MessageType.notice));
                break;
            default:
                System.out.println(new Message("System", "Incorrect command", MessageType.notice));
        }
    }
}
