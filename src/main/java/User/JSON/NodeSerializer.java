package User.JSON;

import User.NodeManager.Node;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class NodeSerializer extends StdSerializer<Node> {

    public NodeSerializer(Class<Node> t) {
        super(t);
    }

    public NodeSerializer() {
        this(null);
    }

    @Override
    public void serialize(Node node, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("id", node.getId());
        jsonGenerator.writeStringField("ip", node.getIp());
        jsonGenerator.writeStringField("public_ip", node.getPublicIp());
        jsonGenerator.writeNumberField("port", node.getPort());
        jsonGenerator.writeBinaryField("public_key", node.getPublicKey().getEncoded());
        jsonGenerator.writeEndObject();
    }
}
