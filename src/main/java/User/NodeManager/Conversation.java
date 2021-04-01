package User.NodeManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Conversation {
    private final ObservableList<Message> messages = FXCollections.observableArrayList();
    private final String participantId;
    private final StringProperty conversationName = new SimpleStringProperty();
    private final StringProperty lastMessage = new SimpleStringProperty("No chat history");
    private String draftMessage;

    public Conversation(String participantId) {
        this.participantId = participantId;
        this.conversationName.set(participantId);
    }

    public Message addMessage(String text, String senderId, boolean isSentByUser) {
        String senderName = isSentByUser ? "username" : conversationName.get(); //TODO change later username to user.getUsername()

        Message message = new Message(text, senderId, senderName, isSentByUser);
        messages.add(message);
        lastMessage.set(text);
        return message;
    }

    public StringProperty getConversationNameProperty() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {

        this.conversationName.set(conversationName);

        for (Message message : messages) {
            if (!message.isSentByUser()) {
                message.setSenderName(conversationName);
            }
        }


    }

    public StringProperty getLastMessageProperty() {
        return lastMessage;
    }

    public StringProperty lastMessageProperty() {
        return lastMessage;
    }

    public ObservableList<Message> getMessages() {
        return messages;
    }

    public String getDraftMessage() {
        return draftMessage;
    }

    public void setDraftMessage(String draftMessage) {
        this.draftMessage = draftMessage;
    }
}
