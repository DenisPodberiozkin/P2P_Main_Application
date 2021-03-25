package GUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;

public class PasswordProgressBarListener<N> implements ChangeListener<N> {
    private static final String RED_BAR = "red-bar";
    private static final String YELLOW_BAR = "yellow-bar";
    private static final String ORANGE_BAR = "orange-bar";
    private static final String DARK_GREEN_BAR = "dark-green-bar";
    private static final String GREEN_BAR = "green-bar";
    private static final String[] barColorStyleClasses = {RED_BAR, ORANGE_BAR, YELLOW_BAR, DARK_GREEN_BAR, GREEN_BAR};
    private final ProgressBar passProgressBar;


    public PasswordProgressBarListener(ProgressBar passProgressBar) {
        this.passProgressBar = passProgressBar;
    }

    @Override
    public void changed(ObservableValue<? extends N> observable, N oldValue, N newValue) {
        Number nv = (Number) newValue;
        double progress = newValue == null ? 0 : nv.doubleValue();
        if (progress == 0.2) {
            setBarStyleClass(passProgressBar, RED_BAR);
        } else if (progress == 0.4) {
            setBarStyleClass(passProgressBar, ORANGE_BAR);
        } else if (progress == 0.6) {
            setBarStyleClass(passProgressBar, YELLOW_BAR);
        } else if (progress == 0.8) {
            setBarStyleClass(passProgressBar, DARK_GREEN_BAR);
        } else if (progress == 1.0) {
            setBarStyleClass(passProgressBar, GREEN_BAR);
        }
    }


    private void setBarStyleClass(ProgressBar bar, String barStyleClass) {
        bar.getStyleClass().removeAll(barColorStyleClasses);
        bar.getStyleClass().add(barStyleClass);
    }

    public void setPasswordLengthBarValue(int value) {
        double v = value / 5.0;
        passProgressBar.setProgress(v);
    }
}
