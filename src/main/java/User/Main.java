package User;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.security.Security;
import java.util.logging.*;

public class Main extends Application {

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
    }

    public static void main(String[] args) {
//        Security.addProvider(new BouncyCastleProvider());
        ConnectionsData.setUserServerPort(Integer.parseInt(args[0]));
        launch(args);
    }


    public static void setDebugLevel(Level newLvl) {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        rootLogger.setLevel(newLvl);
        for (Handler h : handlers) {
            if (h instanceof FileHandler)
                h.setLevel(newLvl);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(true);
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/Test.fxml"));
        primaryStage.setTitle("Hello World");
        Scene mainScene = new Scene(root);
        mainScene.getStylesheets().add(getClass().getResource("../styles/CSS/style.css").toExternalForm());
        primaryStage.setScene(mainScene);
        primaryStage.show();


        Security.setProperty("crypto.policy", "unlimited");
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Before closing")));

        setDebugLevel(Level.FINE);
        final MainController mainController = MainController.getInstance();
        mainController.connectToRing();
//        System.exit(0);
    }
}
