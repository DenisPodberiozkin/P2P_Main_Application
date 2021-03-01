package JSON;

import User.Encryption.EncryptionController;
import User.JSON.NodeDeserializer;
import User.JSON.NodeSerializer;
import User.NodeManager.Node;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class JSONTest {

    @Test
    public void saveJSON() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("NodeSerializer", new Version(1, 0, 0, null, null, null));
        module.addSerializer(Node.class, new NodeSerializer());
        module.addDeserializer(Node.class, new NodeDeserializer());
        objectMapper.registerModule(module);
        Node node = new Node(EncryptionController.getInstance().generateKeyPair().getPublic(), 4444);
        node.setIp("123.123.123.123");
        node.setPublicIp("456.456.456.456");
        final String path = "src/main/resources/JSON/node.json";

        try {
            objectMapper.writeValue(new File(path), node);
            System.out.println(objectMapper.writeValueAsString(node));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        File jsonFile = new File(path);
        try {
            Node n = objectMapper.readValue(jsonFile, Node.class);
            System.out.println(n.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
