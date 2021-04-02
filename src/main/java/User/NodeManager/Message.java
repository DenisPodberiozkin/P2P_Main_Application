package User.NodeManager;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Message {
    private final StringProperty text = new SimpleStringProperty();
    private final String senderId;
    private final StringProperty senderName = new SimpleStringProperty();
    private final boolean isSentByUser;

    public Message(String text, String senderId, String senderName, boolean isSentByUser) {
        this.text.setValue(text);
        this.senderId = senderId;
        this.senderName.set(senderName);
        this.isSentByUser = isSentByUser;
    }


    public String getText() {
        return text.get();
    }

    public StringProperty getTextProperty() {
        return text;
    }

    public StringProperty getSenderNameProperty() {
        return senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderName(String senderName) {
        this.senderName.set(senderName);
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }
}
