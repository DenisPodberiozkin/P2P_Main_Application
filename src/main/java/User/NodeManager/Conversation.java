package User.NodeManager;

import User.Database.DAO.ConversationDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;
import java.util.Objects;

public class Conversation {
    private long id;
    private final ObservableList<Message> messages = FXCollections.observableArrayList();
    private final String participantId;
    private final StringProperty conversationName = new SimpleStringProperty();
    private final StringProperty lastMessage = new SimpleStringProperty("No chat history");
    private ConversationDAO conversationDAO;
    private String draftMessage;

    public Conversation(String participantId) {
        this.participantId = participantId;
        this.conversationName.set(participantId);
    }

    public Conversation(String participantId, String conversationName) {
        this.participantId = participantId;
        this.conversationName.set(conversationName);
    }


    public Message addMessage(String text, String senderId, boolean isSentByUser) {
        final User user = User.getInstance();
        String senderName = isSentByUser ? user.getUsername() : conversationName.get();

        Message message = new Message(text, senderId, senderName, isSentByUser);
        conversationDAO.addMessageToConversation(this, message);
        Platform.runLater(() -> messages.add(message));
        Platform.runLater(() -> lastMessage.set(text));

        return message;
    }

    public void addAllMessages(LinkedList<Message> messages) {
        this.messages.addAll(messages);
        Platform.runLater(() -> lastMessage.set(messages.getLast().getText()));

    }

    public StringProperty getConversationNameProperty() {
        return conversationName;
    }

    public String getConversationName() {
        return conversationName.get();
    }

    public void setConversationName(String conversationName) {

        this.conversationName.set(conversationName);
        conversationDAO.updateConversationName(id, conversationName);
        for (Message message : messages) {
            if (!message.isSentByUser()) {
                message.setSenderName(conversationName);
            }
        }


    }

    public StringProperty conversationNameProperty() {
        return conversationName;
    }

    public void removeAllMessages() {
        messages.clear();
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

    public String getParticipantId() {
        return participantId;
    }

    public void setConversationDAO(ConversationDAO conversationDAO) {
        this.conversationDAO = conversationDAO;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conversation)) return false;
        Conversation that = (Conversation) o;
        return participantId.equals(that.participantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participantId);
    }
}
