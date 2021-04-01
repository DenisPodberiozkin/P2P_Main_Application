package GUI.RegistrationCarousel;

import GUI.ControllerFactory;
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
    private final Stack<Slide> slides = new Stack<>();
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
            ControllerFactory.setRegistrationCarouselController(this);
            addSlide("../../fxml/RegistrationCarousel/InformationSlide.fxml");
            addSlide("../../fxml/RegistrationCarousel/SecretPasswordSlide.fxml");
            addSlide("../../fxml/RegistrationCarousel/FinishSlide.fxml");
            setUpSlidesBackground();
            setPaginationText(current);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    @FXML
    private void prev() {
        translateAnimation(0.5, slides.get(current - 1).getPane(), SCREEN_WIDTH);
        decreaseCurrentPage();
        checkButtons();
        notifyCurrentSlideController();
    }

    @FXML
    private void next() {
        translateAnimation(0.5, slides.get(current).getPane(), -SCREEN_WIDTH);
        increaseCurrentPage();
        checkButtons();
        notifyCurrentSlideController();

    }

    @FXML
    private void cancel(ActionEvent actionEvent) {
        StartScreenNavigator.changeMainScreen(NavigablePane.LOGIN_XML);
        resetCarousel();

    }

    public void resetCarousel() {
        while (current > 0) {
            prev();
        }
        notifyCancelToSlides();
    }

    private void notifyCurrentSlideController() {
        slides.get(current).getController().notifyCurrentSlide();
    }

    private void notifyCancelToSlides() {
        for (Slide slide : slides) {
            slide.getController().notifyCancel();
        }
    }

    public void disableContinueButton() {
        nextBtn.setDisable(true);
    }

    public void enableContinueButton() {
        nextBtn.setDisable(false);
    }

    private void setUpSlidesBackground() {
        int i = 1;
        for (Slide slide : slides) {
            final Pane pane = slide.getPane();
            if (i % 2 == 0) {
                pane.getStyleClass().add("slide-dark");
            } else {
                pane.getStyleClass().add("slide-grey");
            }
            i++;
        }
    }

    private void translateAnimation(double duration, Node node, double width) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(duration), node);
        translateTransition.setByX(width);
        translateTransition.play();
    }

    private void checkButtons() {
        nextBtn.setDisable(current >= slides.size() - 1);
        prevBtn.setDisable(current <= 0);
    }

    private void addSlide(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Pane pane = loader.load();
        SlideController controller = loader.getController();
        Slide slide = new Slide(pane, controller);
        GUI_Util.addChildToParentAnchorPane(parentPane, slide.getPane());
        slides.push(slide);
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
