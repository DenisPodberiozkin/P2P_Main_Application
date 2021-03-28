package GUI.LoginScreen;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;


public class PortFilter implements UnaryOperator<TextFormatter.Change> {

    private final TextField textField;

    public PortFilter(TextField textField) {
        this.textField = textField;
    }

    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        if ((textField.getText() + change.getText()).matches("^([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$")) {
            return change;
        } else
            // if not adding any text (delete or selection change), accept as is
            if (change.getText().isEmpty()) {
                return change;
            }
        // otherwise veto change
        return null;
    }
}
