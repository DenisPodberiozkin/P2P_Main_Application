package GUI.LoginScreen;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class IpFilter implements UnaryOperator<TextFormatter.Change> {

    public IpFilter() {
    }

    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        if (change.getControlNewText().matches(makePartialIPRegex())) {
            return change;
        } else
            // if not adding any text (delete or selection change), accept as is
            if (change.getText().isEmpty()) {
                return change;
            }
        // otherwise veto change
        return null;
    }

    private String makePartialIPRegex() {
        String partialBlock = "(([01]?[0-9]{0,2})|(2[0-4][0-9])|(25[0-5]))";
        String subsequentPartialBlock = "(\\." + partialBlock + ")";
        String ipAddress = partialBlock + "?" + subsequentPartialBlock + "{0,3}";
        return "^" + ipAddress;
    }
}
