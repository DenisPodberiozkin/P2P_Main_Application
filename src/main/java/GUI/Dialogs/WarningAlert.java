package GUI.Dialogs;

import javafx.scene.control.Alert;

public class WarningAlert extends Alert {

	public WarningAlert(String title, String header, String content) {
		super(AlertType.WARNING);
		setTitle(title);
		setHeaderText(header);
		setContentText(content);
		getDialogPane().getStylesheets().add(getClass().getResource("../../styles/CSS/style.css").toExternalForm());
		getDialogPane().getStylesheets().add(getClass().getResource("../../styles/CSS/customStyle.css").toExternalForm());
	}
}
