package User;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
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


        MainController.getInstance().connectToRing();
    }
}
