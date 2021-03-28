package GUI.Navigators;

public enum NavigablePane {

    LOGIN_FORM_XML("../../fxml/LoginScreen/LoginForm.fxml"),
    LOG_ON_FORM_XML("../../fxml/LoginScreen/LogonForm.fxml"),
    REGISTRATION_CAROUSEL_XML("../../fxml/RegistrationCarousel/RegistrationCarousel.fxml"),
    LOGIN_XML("../../fxml/LoginScreen/Login.fxml"),
    SETTINGS_XML("../../fxml/LoginScreen/Settings.fxml");

    private final String xml;

    NavigablePane(String xml) {
        this.xml = xml;
    }

    public String getXml() {
        return xml;
    }
}
