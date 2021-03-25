package GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextInputControl;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class LoginForm implements Initializable {


    // FXML Fields

    private final LinkedList<TextInputControl> mandatoryFields = new LinkedList<>();
    @FXML
    private PasswordField passField;

    // Normal Fields
    @FXML
    private PasswordField secretPassField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mandatoryFields.add(passField);
        mandatoryFields.add(secretPassField);
    }

    @FXML
    private void login(ActionEvent actionEvent) {
        if (GUI_Util.checkMandatoryFields(mandatoryFields)) {

        }
    }

    @FXML
    private void showLogOnForm(ActionEvent actionEvent) {
        LoginSideBarNavigator.changeSideBar(LoginSideBarNavigator.LOG_ON_FORM_XML);
        clearFields();
        GUI_Util.clearMandatoryFieldsStyles(mandatoryFields);
    }

    private void clearFields() {
        passField.clear();
        secretPassField.clear();
    }


}
