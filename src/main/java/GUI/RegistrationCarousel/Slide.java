package GUI.RegistrationCarousel;

import javafx.scene.layout.Pane;

public class Slide {
    private final Pane pane;
    private final SlideController controller;

    public Slide(Pane pane, SlideController controller) {
        this.pane = pane;
        this.controller = controller;
    }

    public Pane getPane() {
        return pane;
    }

    public SlideController getController() {
        return controller;
    }
}
