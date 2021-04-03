package GUI;

import GUI.Navigators.NavigablePane;
import GUI.Navigators.StartScreenNavigator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class StartScreen implements Initializable {

    @FXML
    private AnchorPane parent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StartScreenNavigator.setStartScreenController(this);
        StartScreenNavigator.changeMainScreen(NavigablePane.LOGIN_XML);

    }

    public void changeMainScreen(Pane pane) {
        GUI_Util.setChildToParentAnchorPane(parent, pane);
    }
}
