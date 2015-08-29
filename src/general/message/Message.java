package general.message;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {

    private MessageType type;
    private String sender;
    private String text;
    private Date date;
    private static final SimpleDateFormat format = new SimpleDateFormat("[MM:dd hh:mm:ss]");

    public Message(String sender, String text, MessageType type) {
        this.sender = sender;
        this.text = text;
        this.type = type;
        date = new Date();
    }

    @Override
    public String toString() {
        switch (type) {
            case notice:
                return Message.getFormat().format(date) + " " + "Notice: " + text;
            case error:
                return Message.getFormat().format(date) + " " + "Error: " + text;
            default:
                return Message.getFormat().format(date) + " " + sender + ": " + text; // [MM:dd hh:mm:ss] Sender: %message%
        }
    }
    public String getSender() { return sender; }
    public String getMessage() { return text; }
    public MessageType getMessageType() { return type; }
    public Date getDate() { return date; }
    public static SimpleDateFormat getFormat() { return format; }

}

