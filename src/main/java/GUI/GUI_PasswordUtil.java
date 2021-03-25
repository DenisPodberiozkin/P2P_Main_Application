package GUI;

import javafx.collections.ObservableList;
import javafx.scene.control.PasswordField;

public class GUI_PasswordUtil {

    public static void checkRepeatPassword(PasswordField passField, PasswordField repeatPasswordField) {
        String style = "text-field-red-text";
        ObservableList<String> styleClass = repeatPasswordField.getStyleClass();
        boolean isSame = repeatPasswordField.getText().equals(passField.getText());
        if (!isSame && !styleClass.contains(style)) {
            styleClass.add(style);
        } else if (isSame && styleClass.contains(style)) {
            styleClass.removeAll(style);
        }
    }

    public static int getPasswordStrength(String pass) {
        int strength = 0;
        if (pass.matches(".*\\W+.*")) {
            strength++;
        }
        if (pass.matches(".*\\d+.*")) {
            strength++;
        }

        if (pass.matches(".*[a-z]+.*")) {
            strength++;
        }

        if (pass.matches(".*[A-Z]+.*")) {
            strength++;
        }
        if (pass.length() > 5) {
            strength++;
        }

        return strength;

    }


}
