package User;

import User.CommunicationUnit.Server.ServerController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        Platform.setImplicitExit(true);
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/Test.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();


        Security.setProperty("crypto.policy", "unlimited");
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Before closing")));


        final MainController mainController = MainController.getInstance();
        mainController.connectToRing();
//        System.exit(0);
    }
}
