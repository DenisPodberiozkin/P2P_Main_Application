package GUI;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.util.LinkedList;

public class GUI_Util {

    private static final String RED_BORDER_STYLE = "text-field-red";

    public static void setChildToParentAnchorPane(Pane parent, Pane child) {
        setAnchors(child);
        parent.getChildren().setAll(child);
    }

    public static void addChildToParentAnchorPane(Pane parent, Pane child) {
        setAnchors(child);
        final ObservableList<Node> children = parent.getChildren();
        final LinkedList<Node> linkedList = new LinkedList<>(children);
        linkedList.addFirst(child);
        children.setAll(linkedList);
    }

    private static void setAnchors(Pane child) {
        AnchorPane.setBottomAnchor(child, 0.0);
        AnchorPane.setTopAnchor(child, 0.0);
        AnchorPane.setLeftAnchor(child, 0.0);
        AnchorPane.setRightAnchor(child, 0.0);
    }

    public static boolean checkMandatoryFields(LinkedList<TextInputControl> mandatoryFields) {
        boolean status = true;
        for (TextInputControl field : mandatoryFields) {
            ObservableList<String> styles = field.getStyleClass();
            if (styles.contains(RED_BORDER_STYLE)) {
                styles.removeAll(RED_BORDER_STYLE);
            }
            if (field.getText().equals("")) {
                status = false;
                styles.add(RED_BORDER_STYLE);
            }
        }

        return status;
    }

    public static void clearMandatoryFieldsStyles(LinkedList<TextInputControl> mandatoryFields) {
        for (TextInputControl mandatoryField : mandatoryFields) {
            mandatoryField.getStyleClass().removeAll(RED_BORDER_STYLE);
        }
    }


    // Returns listener of the main passField and its progress bar
    private static ChangeListener<? super String> getPassFieldListener(PasswordProgressBarListener progressBarListener, PasswordField passField, PasswordField repeatPassField) {
        return (observable, oldValue, newValue) -> {
            progressBarListener.setPasswordLengthBarValue(GUI_PasswordUtil.getPasswordStrength(passField.getText()));
            GUI_PasswordUtil.checkRepeatPassword(passField, repeatPassField);
        };
    }

    // returns listener of the repeat passwordField
    private static ChangeListener<? super String> getRepeatPassFieldListener(PasswordField passField, PasswordField repeatPassField) {
        return (observable, oldValue, newValue) -> GUI_PasswordUtil.checkRepeatPassword(passField, repeatPassField);
    }


    public static void setUpPasswordFieldPair(PasswordField passField, PasswordField repeatPasswordField, ProgressBar passProgressBar) {
        PasswordProgressBarListener<Number> progressBarListener = new PasswordProgressBarListener<>(passProgressBar);
        passField.textProperty().addListener(GUI_Util.getPassFieldListener(progressBarListener, passField, repeatPasswordField));

        repeatPasswordField.textProperty().addListener(GUI_Util.getRepeatPassFieldListener(passField, repeatPasswordField));

        passProgressBar.progressProperty().addListener(progressBarListener);
    }

}
