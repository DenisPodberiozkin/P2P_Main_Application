package User.NodeManager;


public class Message {
    private final String text;
    private final String senderId;

    public Message(String text, String senderId) {
        this.text = text;
        this.senderId = senderId;
    }
}
