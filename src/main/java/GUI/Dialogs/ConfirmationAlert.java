package GUI.Dialogs;

import javafx.scene.control.Alert;

public class ConfirmationAlert extends Alert {

	public ConfirmationAlert(String title, String header, String content) {
		super(AlertType.CONFIRMATION);
		setTitle(title);
		setHeaderText(header);
		setContentText(content);
		getDialogPane().getStylesheets().add(getClass().getResource("../../styles/CSS/style.css").toExternalForm());
		getDialogPane().getStylesheets().add(getClass().getResource("../../styles/CSS/customStyle.css").toExternalForm());
	}
}
