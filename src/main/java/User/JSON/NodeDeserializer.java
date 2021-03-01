package User.JSON;

import User.Encryption.EncryptionController;
import User.Encryption.IEncryptionController;
import User.NodeManager.Node;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.security.PublicKey;

public class NodeDeserializer extends StdDeserializer<Node> {

    public NodeDeserializer() {
        this(null);
    }

    public NodeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Node deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode jsonNode = codec.readTree(jsonParser);

        JsonNode ipNode = jsonNode.get("ip");
        JsonNode publicIpNode = jsonNode.get("public_ip");
        JsonNode portNode = jsonNode.get("port");
        JsonNode publicKeyNode = jsonNode.get("public_key");
        String ip = ipNode.asText();
        String publicIp = publicIpNode.asText();
        int port = portNode.asInt();
        byte[] publicKeyData = publicKeyNode.binaryValue();
        IEncryptionController encryptionController = EncryptionController.getInstance();
        PublicKey publicKey = encryptionController.getPublicKeyFromBytes(publicKeyData);
        Node node = new Node(publicKey, port);
        node.setPublicIp(publicIp);
        node.setIp(ip);

        return node;
    }
}
