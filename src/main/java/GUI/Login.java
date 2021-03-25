package GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Login implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(Login.class.getName());

    @FXML
    private AnchorPane sideBar;
    @FXML
    private BorderPane borderPane;
    @FXML
    private ImageView img;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerFactory.setLoginController(this);
        LoginSideBarNavigator.setLoginController(this);
        img.fitHeightProperty().bind(borderPane.widthProperty());
        LoginSideBarNavigator.changeSideBar(LoginSideBarNavigator.LOGIN_FORM_XML);
    }


    public void setSideBar(Pane child) {
        GUI_Util.setChildToParentAnchorPane(sideBar, child);
    }
}
