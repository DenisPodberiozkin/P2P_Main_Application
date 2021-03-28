package GUI.RegistrationCarousel;

import GUI.ControllerFactory;
import User.RegistrationModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SecretPasswordSlide implements Initializable, SlideController {

    @FXML
    private TextField secretPasswordField;
    @FXML
    private CheckBox checkBox1;
    @FXML
    private CheckBox checkBox2;

    private RegistrationCarousel registrationCarouselController;
    private RegistrationModel registrationModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerFactory.setSecretPasswordSlideController(this);
        this.registrationCarouselController = ControllerFactory.getRegistrationCarouselController();
    }

    @Override
    public void notifyCurrentSlide() {
        checkBoxAction();
        secretPasswordField.setText(registrationModel.getSecretePassword());
    }

    @Override
    public void notifyCancel() {
        checkBox1.setSelected(false);
        checkBox2.setSelected(false);
    }

    public void setRegistrationModel(RegistrationModel registrationModel) {
        this.registrationModel = registrationModel;
    }

    @FXML
    private void checkBoxAction() {
        if (checkBox1.isSelected() && checkBox2.isSelected()) {
            registrationCarouselController.enableContinueButton();
        } else {
            registrationCarouselController.disableContinueButton();
        }
    }
}
