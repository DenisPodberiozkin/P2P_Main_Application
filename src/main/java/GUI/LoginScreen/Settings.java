package GUI.LoginScreen;

import GUI.Dialogs.ErrorAlert;
import GUI.Exceptions.IpValidationException;
import GUI.GUI_Util;
import GUI.Navigators.NavigablePane;
import GUI.Navigators.StartScreenNavigator;
import User.Settings.ApplicationSettingsModel;
import User.Settings.ConnectionSettingsModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import static GUI.GUI_Util.checkMandatoryFields;

public class Settings implements Initializable {

    private final LinkedList<TextInputControl> localServerOptionMandatoryFields = new LinkedList<>();
    private final LinkedList<TextInputControl> existingUserOptionMandatoryFields = new LinkedList<>();
    private final LinkedList<TextInputControl> applicationSettingsMandatoryFields = new LinkedList<>();
    @FXML
    private TabPane tabs;
    @FXML
    private TextField applicationPortField;
    @FXML
    private RadioButton localServerOptionRadioBtn;
    @FXML
    private ToggleGroup connectionToggle;
    @FXML
    private TextField localServerIpField;
    @FXML
    private TextField localServerPortField;
    @FXML
    private RadioButton userOptionRadioBtn;
    @FXML
    private TextField existingUserIpField;
    @FXML
    private TextField existingUserPortField;
    @FXML
    private RadioButton bothOptionRadioBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        localServerOptionMandatoryFields.add(localServerIpField);
        localServerOptionMandatoryFields.add(localServerPortField);

        existingUserOptionMandatoryFields.add(existingUserIpField);
        existingUserOptionMandatoryFields.add(existingUserPortField);

        applicationSettingsMandatoryFields.add(applicationPortField);

        revertSettings();
        checkToggleFields(connectionToggle.getSelectedToggle());

        applicationPortField.setTextFormatter(new TextFormatter<>(new PortFilter(applicationPortField)));
        localServerPortField.setTextFormatter(new TextFormatter<>(new PortFilter(localServerPortField)));
        existingUserPortField.setTextFormatter(new TextFormatter<>(new PortFilter(existingUserPortField)));

        localServerIpField.setTextFormatter(new TextFormatter<>(new IpFilter(localServerIpField)));

    }


    @FXML
    void applyBtnAction() {
        try {
            if (checkConnectionSettings() & checkApplicationSettings()) {
                updateApplicationSettings();
                updateConnectionSettings();
                tabs.getSelectionModel().clearAndSelect(0);
                StartScreenNavigator.changeMainScreen(NavigablePane.LOGIN_XML);
            }
        } catch (IpValidationException e) {
            new ErrorAlert("IP validation Error", "Unable to apply settings", e.getMessage()).show();
        }
    }

    @FXML
    void cancelBtnAction() {
        StartScreenNavigator.changeMainScreen(NavigablePane.LOGIN_XML);
        clearAllMandatoryFields();
        tabs.getSelectionModel().clearAndSelect(0);
        revertSettings();
        checkToggleFields(connectionToggle.getSelectedToggle());

    }

    @FXML
    void okBtnAction() {

    }

    @FXML
    void radioBtnAction() {
        final Toggle selectedToggle = connectionToggle.getSelectedToggle();
        checkToggleFields(selectedToggle);
        clearAllMandatoryFields();
    }

    private void updateApplicationSettings() {
        final int currentPort = ApplicationSettingsModel.getApplicationPort();
        final int newPort = Integer.parseInt(applicationPortField.getText());
        if (currentPort != newPort) {
            ApplicationSettingsModel.setApplicationPort(newPort);
        }
    }

    private void updateConnectionSettings() {
        final int selectedConnectionType = getSelectedToggleType(connectionToggle.getSelectedToggle());
        if (selectedConnectionType != ConnectionSettingsModel.getConnectionType()) {
            ConnectionSettingsModel.setConnectionType(selectedConnectionType);
        }
        switch (selectedConnectionType) {
            case ConnectionSettingsModel.LOCAL_SERVER_CONNECTION:
                updateLocalServerConnectionSettings();
                break;
            case ConnectionSettingsModel.EXISTING_USER_CONNECTION:
                updateExistingUserConnectionSettings();
                break;
            case ConnectionSettingsModel.BOTH_CONNECTION:
                updateLocalServerConnectionSettings();
                updateExistingUserConnectionSettings();
                break;
        }

    }

    private void updateExistingUserConnectionSettings() {
        final String newExistingUserIp = existingUserIpField.getText();
        final int newExistingUserPort = Integer.parseInt(existingUserPortField.getText());
        final String currentExistingUserIp = ConnectionSettingsModel.getExistingUserIp();
        final int currentExistingUserPort = ConnectionSettingsModel.getExistingUserPort();
        if (!currentExistingUserIp.equals(newExistingUserIp)) {
            ConnectionSettingsModel.setExistingUserIp(newExistingUserIp);
        }
        if (currentExistingUserPort != newExistingUserPort) {
            ConnectionSettingsModel.setExistingUserPort(newExistingUserPort);
        }

    }

    private void updateLocalServerConnectionSettings() {
        final String newLocalServerIp = localServerIpField.getText();
        final int newLocalServerPort = Integer.parseInt(localServerPortField.getText());
        final String currentLocalServerIp = ConnectionSettingsModel.getLocalServerIp();
        final int currentLocalServerPort = ConnectionSettingsModel.getLocalServerPort();
        if (!currentLocalServerIp.equals(newLocalServerIp)) {
            ConnectionSettingsModel.setLocalServerIp(newLocalServerIp);
        }
        if (currentLocalServerPort != newLocalServerPort) {
            ConnectionSettingsModel.setLocalServerPort(newLocalServerPort);
        }
    }

    private int getSelectedToggleType(Toggle selectedToggle) {
        if (selectedToggle.equals(localServerOptionRadioBtn)) {
            return ConnectionSettingsModel.LOCAL_SERVER_CONNECTION;
        } else if (selectedToggle.equals(userOptionRadioBtn)) {
            return ConnectionSettingsModel.EXISTING_USER_CONNECTION;
        } else if (selectedToggle.equals(bothOptionRadioBtn)) {
            return ConnectionSettingsModel.BOTH_CONNECTION;
        }
        throw new IllegalArgumentException("Selected toggle not found");
    }

    private void checkToggleFields(Toggle selectedToggle) {
        final int toggleType = getSelectedToggleType(selectedToggle);
        switch (toggleType) {
            case ConnectionSettingsModel.LOCAL_SERVER_CONNECTION:
                setDisableLocalServerFields(false);
                setDisableExistingUserFields(true);
                break;
            case ConnectionSettingsModel.EXISTING_USER_CONNECTION:
                setDisableLocalServerFields(true);
                setDisableExistingUserFields(false);
                break;
            case ConnectionSettingsModel.BOTH_CONNECTION:
                setDisableLocalServerFields(false);
                setDisableExistingUserFields(false);
                break;
        }
    }

    private void revertSettings() {
        applicationPortField.setText(ApplicationSettingsModel.getApplicationPort() + "");
        localServerPortField.setText(ConnectionSettingsModel.getLocalServerPort() + "");
        localServerIpField.setText(ConnectionSettingsModel.getLocalServerIp());
        existingUserPortField.setText(ConnectionSettingsModel.getExistingUserPort() + "");
        existingUserIpField.setText(ConnectionSettingsModel.getExistingUserIp());
        selectToggle();


    }

    private void selectToggle() {
        switch (ConnectionSettingsModel.getConnectionType()) {
            case ConnectionSettingsModel.LOCAL_SERVER_CONNECTION:
                connectionToggle.selectToggle(localServerOptionRadioBtn);
                break;
            case ConnectionSettingsModel.EXISTING_USER_CONNECTION:
                connectionToggle.selectToggle(userOptionRadioBtn);
                break;
            case ConnectionSettingsModel.BOTH_CONNECTION:
                connectionToggle.selectToggle(bothOptionRadioBtn);
                break;
        }
    }


    private void clearAllMandatoryFields() {
        GUI_Util.clearMandatoryFieldsStyles(localServerOptionMandatoryFields);
        GUI_Util.clearMandatoryFieldsStyles(existingUserOptionMandatoryFields);
        GUI_Util.clearMandatoryFieldsStyles(applicationSettingsMandatoryFields);
    }

    private boolean checkConnectionSettings() throws IpValidationException {

        final Toggle selectedToggle = connectionToggle.getSelectedToggle();
        final int selectedToggleType = getSelectedToggleType(selectedToggle);
        switch (selectedToggleType) {
            case ConnectionSettingsModel.LOCAL_SERVER_CONNECTION:
                return checkMandatoryFields(localServerOptionMandatoryFields) && verifyIp(localServerIpField.getText());
            case ConnectionSettingsModel.EXISTING_USER_CONNECTION:
                return checkMandatoryFields(existingUserOptionMandatoryFields) && verifyIp(existingUserIpField.getText());
            case ConnectionSettingsModel.BOTH_CONNECTION:
                return (checkMandatoryFields(localServerOptionMandatoryFields) & checkMandatoryFields(existingUserOptionMandatoryFields))
                        && verifyIp(localServerIpField.getText()) && verifyIp(existingUserIpField.getText());
            default:
                return false;
        }

    }

    private boolean verifyIp(String ip) throws IpValidationException {
        final String regex = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
        if (ip.matches(regex)) {
            return true;
        }
        throw new IpValidationException(ip);
    }

    private boolean checkApplicationSettings() {
        return checkMandatoryFields(applicationSettingsMandatoryFields);
    }

    private void setDisableExistingUserFields(boolean b) {
        existingUserIpField.setDisable(b);
        existingUserPortField.setDisable(b);
    }

    private void setDisableLocalServerFields(boolean b) {
        localServerIpField.setDisable(b);
        localServerPortField.setDisable(b);
    }
}
