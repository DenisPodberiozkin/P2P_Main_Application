package JSON;

import User.JSON.NodeDeserializer;
import User.JSON.NodeSerializer;
import User.NodeManager.Node;
import User.NodeManager.User;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

public class JSONTest {

    @Test
    public void saveJSON() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("NodeSerializer", new Version(1, 0, 0, null, null, null));
        module.addSerializer(Node.class, new NodeSerializer());
        module.addDeserializer(Node.class, new NodeDeserializer());
        objectMapper.registerModule(module);
//        Node node = new Node(EncryptionController.getInstance().generateRSAKeyPair().getPublic(), 4444);
//        node.setIp("123.123.123.123");
//        node.setPublicIp("456.456.456.456");

        User user = new User();
        final String path = "src/main/resources/JSON/node.json";
        System.out.println(user.getJSONString());
//        try {
//            objectMapper.writeValue(new File(path), user);
//            System.out.println(objectMapper.writeValueAsString(user));
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        }

//        File jsonFile = new File(path);
//        try {
//            Node n = objectMapper.readValue(jsonFile, Node.class);
//            System.out.println(n.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    @Test
    public void threadTest() {
        class Test {
            private int a = 5;

            public int getA() {
                return a;
            }

            public synchronized void setA(int a) {

                this.a = a;
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Test test = new Test();
        try {
            Thread t1 = new Thread(() -> test.setA(6));
            t1.start();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread t2 = new Thread(() -> System.out.println(test.getA()));
        t2.start();
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
