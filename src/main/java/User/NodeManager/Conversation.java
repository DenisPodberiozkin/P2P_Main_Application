package User.NodeManager;

import java.util.LinkedList;

public class Conversation {
    private final LinkedList<Message> messages = new LinkedList<>();
    private final String participantId;

    public Conversation(String participantId) {
        this.participantId = participantId;
    }

    public Message addMessage(String text, String senderId) {
        Message message = new Message(text, senderId);
        messages.add(message);
        return message;
    }


}
