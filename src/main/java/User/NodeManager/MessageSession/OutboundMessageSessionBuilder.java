package User.NodeManager.MessageSession;

public class OutboundMessageSessionBuilder {
    private String participantNodeId;
    private String messageText;

    public OutboundMessageSessionBuilder setParticipantNodeId(String participantNodeId) {
        this.participantNodeId = participantNodeId;
        return this;
    }

    public OutboundMessageSessionBuilder setMessageText(String messageText) {
        this.messageText = messageText;
        return this;
    }

    public OutboundMessageSession createOutboundMessageSession() {
        return new OutboundMessageSession(participantNodeId, messageText);
    }
}