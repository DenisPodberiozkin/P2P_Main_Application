package User.NodeManager.MessageSession;

import Encryption.EncryptionController;
import Encryption.IEncryptionController;

import javax.crypto.SecretKey;
import java.util.concurrent.Callable;

public abstract class MessageSession implements Callable<String> {
    private static long GLOBAL_ID = 0L;
    protected final IEncryptionController encryptionController = EncryptionController.getInstance();
    protected final long id;
    protected String participantNodeId;
    protected long participantMessageSessionId;
    protected String messageText;
    protected SecretKey secretKey;

    public MessageSession(String participantNodeId, String messageText) {
        this.participantNodeId = participantNodeId;
        this.messageText = messageText;
        this.id = initId();
    }

    public MessageSession(long participantMessageSessionId) {
        this.participantMessageSessionId = participantMessageSessionId;
        this.id = initId();
    }

    private static synchronized long initId() {
        return GLOBAL_ID++;
    }

    public long getId() {
        return id;
    }

    @Override
    public abstract String call() throws Exception;


}
