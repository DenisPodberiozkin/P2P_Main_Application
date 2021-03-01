package User;

import User.CommunicationUnit.Server.ServerController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.security.Security;

public class Main extends Application {

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
    }

    public static void main(String[] args) {
        ConnectionsData.setUserServerPort(Integer.parseInt(args[0]));
        ServerController.getInstance().startServer(ConnectionsData.getUserServerPort());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("../fxml/User.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();


//        Thread t = new Thread();
//
//        Socket socket = new Socket("localhost", 4444);
//        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
//        writer.println("GETLN");
        Security.setProperty("crypto.policy", "unlimited");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Before closing")));


        final MainController mainController = MainController.getInstance();
        mainController.connectToRing();
//        System.exit(0);
    }
}
