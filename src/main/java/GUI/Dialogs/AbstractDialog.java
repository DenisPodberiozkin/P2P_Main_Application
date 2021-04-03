package GUI.Dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.io.IOException;

public abstract class AbstractDialog extends javafx.scene.control.Dialog<Pair<String, String>> {
	private AnchorPane dialogContent;

	public AbstractDialog(String FXML) {
		getDialogPane().getStylesheets().add(getClass().getResource("../../styles/CSS/style.css").toExternalForm());
		getDialogPane().getStylesheets().add(getClass().getResource("../../styles/CSS/customStyle.css").toExternalForm());
		loadDialogContent(FXML);
		setGraphic(dialogContent);
		getDialogPane().setPrefWidth(dialogContent.getPrefWidth() + 20.0);
	}

	private void loadDialogContent(String FXML) {
		try {
			if (dialogContent == null) {
				dialogContent = FXMLLoader.load(getClass().getResource(FXML));
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}
