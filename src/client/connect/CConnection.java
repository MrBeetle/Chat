package client.connect;

import client.rw.CReader;
import general.message.Message;
import general.message.MessageType;
import client.rw.CWriter;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class CConnection {

    private final int serverPort = 1234;
    private final String address = "127.0.0.1";
    private String name;
    private CWriter cWriter;
    private CReader cCReader;
    ObjectOutputStream out;
    ObjectInputStream in;

    public CConnection() {

        try (
                BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
                Socket socket = new Socket(InetAddress.getByName(address), serverPort); // создаем сокет используя IP-адрес и порт сервера.
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());//INIT OUTPUT STREAM BEFORE INPUT
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
            System.out.println("Connected to " + address + ":" + serverPort);
            System.out.print("Enter you nickname: ");
            name = keyboard.readLine();
            System.out.println("For send message, press enter, type your message, and press enter again.");
            System.out.println("Type /help for view available commands");

            out.writeObject(new Message(name, null, MessageType.auth));//send to server your name

            cWriter = new CWriter(name, in); //create io objects
            cCReader = new CReader(name, out, cWriter);

            cWriter.getThread().join();//waiting for stop io objects
            cCReader.getThread().join();
            System.out.println("App stopped");
        } catch (IOException ce) {
            System.out.println("Server not responding. Application will be stopped.");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
