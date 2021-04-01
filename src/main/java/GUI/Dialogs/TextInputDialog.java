package GUI.Dialogs;

public class TextInputDialog extends javafx.scene.control.TextInputDialog {

	public TextInputDialog(String title, String header, String content) {
		setTitle(title);
		setHeaderText(header);
		setContentText(content);
		getDialogPane().getStylesheets().add(getClass().getResource("../../styles/CSS/style.css").toExternalForm());
		getDialogPane().getStylesheets().add(getClass().getResource("../../styles/CSS/customStyle.css").toExternalForm());
	}
}
