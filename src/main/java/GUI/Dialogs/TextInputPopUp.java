package GUI.Dialogs;

import javafx.scene.control.TextInputDialog;

public class TextInputPopUp extends TextInputDialog {

	public TextInputPopUp(String title, String header, String content) {
		setTitle(title);
		setHeaderText(header);
		setContentText(content);
		getDialogPane().getStylesheets().add(getClass().getResource("../../styles/CSS/style.css").toExternalForm());
		getDialogPane().getStylesheets().add(getClass().getResource("../../styles/CSS/customStyle.css").toExternalForm());
	}
}
