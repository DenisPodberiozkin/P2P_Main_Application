package GUI.Dialogs;

import javafx.scene.control.Alert;

public class ErrorAlert extends Alert {

	public ErrorAlert(String title, String header, String content) {
		super(AlertType.ERROR);
		setTitle(title);
		setHeaderText(header);
		setContentText(content);
		getDialogPane().getStylesheets().add(getClass().getResource("../../styles/CSS/style.css").toExternalForm());
		getDialogPane().getStylesheets().add(getClass().getResource("../../styles/CSS/customStyle.css").toExternalForm());
	}

}
