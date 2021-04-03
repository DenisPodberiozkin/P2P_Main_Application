package User;

import GUI.ControllerFactory;
import GUI.Debugger;
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
        final IMainController mainController = MainController.getInstance();
        mainController.initSettings();


        Platform.setImplicitExit(true);
        final Parent mainRoot = FXMLLoader.load(getClass().getResource("../fxml/StartScreen.fxml"));
        primaryStage.setTitle("Westwood v0.1");
        final Scene mainScene = new Scene(mainRoot);
        mainScene.getStylesheets().add(getClass().getResource("../styles/CSS/style.css").toExternalForm());
        mainScene.getStylesheets().add(getClass().getResource("../styles/CSS/customStyle.css").toExternalForm());
        primaryStage.setScene(mainScene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> System.exit(0));

        final Stage debugStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/Debugger.fxml"));
        final Parent debugRoot = loader.load();
        final Scene debugScene = new Scene(debugRoot);
        debugScene.getStylesheets().add(getClass().getResource("../styles/CSS/style.css").toExternalForm());
        debugScene.getStylesheets().add(getClass().getResource("../styles/CSS/customStyle.css").toExternalForm());
        debugStage.setTitle("Westwood debugger");
        debugStage.setScene(debugScene);
        Debugger debugger = loader.getController();
        debugger.setStage(debugStage);
        debugStage.setOnCloseRequest(e -> ControllerFactory.getChatScreenController().enableDebugToggle());

        Security.setProperty("crypto.policy", "unlimited");

        setDebugLevel(Level.FINE);
    }
}
