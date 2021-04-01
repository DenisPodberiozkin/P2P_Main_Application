package GUI.Dialogs;

import javafx.scene.control.Alert;

public class InformationAlert extends Alert {

	public InformationAlert(String title, String header, String content) {
		super(AlertType.INFORMATION);
		setTitle(title);
		setHeaderText(header);
		setContentText(content);
		getDialogPane().getStylesheets().add(getClass().getResource("../../styles/CSS/style.css").toExternalForm());
		getDialogPane().getStylesheets().add(getClass().getResource("../../styles/CSS/customStyle.css").toExternalForm());
	}
}
