package server;

import general.message.Message;
import general.message.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;

public class SListener {

    private final int PORT = 1234;
    private ServerSocket serverSocket;
    private static HashSet<ObjectOutputStream> clientsOutput;
    private static HashSet<SConnection> connections;

    public SListener() {
        clientsOutput = new HashSet<>(5);
        connections = new HashSet<>(5);
        try {

            serverSocket = new ServerSocket(PORT);
            System.out.println("Start server on port " + PORT + "\nWaiting for a client...");
            SReader sReader = new SReader(this);
            while (true) {
                Socket socket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());//INIT OUTPUT STREAM BEFORE INPUT
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                clientsOutput.add(out);
                Message auth = (Message) in.readObject();
                connections.add(new SConnection(socket, auth.getSender(), in, out));
                System.out.println(new Message("System", auth.getSender() + " connected", MessageType.notice));
            }
        }
        catch (SocketException se) {
            //do nothing?
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(Message message) {
        try {
            for (ObjectOutputStream out : clientsOutput) {
                out.writeObject(message);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteStreamFromBroadcast(ObjectOutputStream out) {
        return clientsOutput.remove(out);
    }

    public void close() {
        try {
            for (ObjectOutputStream out : clientsOutput) {
                out.close();
            }
            for (SConnection sConnection : connections) {
                sConnection.close();
            }
            serverSocket.close();
            System.out.println("App closed");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}