package GUI.RegistrationCarousel;

import GUI.GUI_Util;
import GUI.Navigators.NavigablePane;
import GUI.Navigators.StartScreenNavigator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;

public class RegistrationCarousel implements Initializable {


    private static final double SCREEN_WIDTH = Screen.getPrimary().getBounds().getWidth();
    private final Stack<Pane> slides = new Stack<>();
    @FXML
    private Label paginationLabel;
    @FXML
    private AnchorPane parentPane;
    @FXML
    private Button prevBtn;
    @FXML
    private Button nextBtn;
    private int current = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            addSlide(FXMLLoader.load(RegistrationCarousel.class.getResource("../fxml/Slide1.fxml")));
            addSlide(FXMLLoader.load(RegistrationCarousel.class.getResource("../fxml/Slide2.fxml")));
            addSlide(FXMLLoader.load(RegistrationCarousel.class.getResource("../fxml/Slide3.fxml")));
            checkButtons();
            setPaginationText(current);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    @FXML
    private void prev() {
        translateAnimation(0.5, slides.get(current - 1), SCREEN_WIDTH);
        decreaseCurrentPage();
        checkButtons();
    }

    @FXML
    private void next() {
        translateAnimation(0.5, slides.get(current), -SCREEN_WIDTH);
        increaseCurrentPage();
        checkButtons();

    }

    @FXML
    private void cancel(ActionEvent actionEvent) {
        StartScreenNavigator.changeMainScreen(NavigablePane.LOGIN_XML);
        while (current > 0) {
            prev();
        }
    }

    private void translateAnimation(double duration, Node node, double width) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(duration), node);
        translateTransition.setByX(width);
        translateTransition.play();
    }

    private void checkButtons() {
        System.out.println(current);
        nextBtn.setDisable(current >= slides.size() - 1);
        prevBtn.setDisable(current <= 0);
    }

    private void addSlide(Pane pane) {
        GUI_Util.addChildToParentAnchorPane(parentPane, pane);
        slides.push(pane);
    }

    private void increaseCurrentPage() {
        current++;
        setPaginationText(current);
    }

    private void decreaseCurrentPage() {
        current--;
        setPaginationText(current);
    }

    private void setPaginationText(int current) {
        paginationLabel.setText(current + 1 + " / " + slides.size());
    }


}
