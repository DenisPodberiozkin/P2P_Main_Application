package GUI.Navigators;

public enum NavigablePane {

    LOGIN_FORM_XML("../fxml/LoginForm.fxml"),
    LOG_ON_FORM_XML("../fxml/LogonForm.fxml"),
    REGISTRATION_CAROUSEL_XML("../fxml/RegistrationCarousel.fxml"),
    LOGIN_XML("../fxml/Login.fxml");

    private final String xml;

    NavigablePane(String xml) {
        this.xml = xml;
    }

    public String getXml() {
        return xml;
    }
}
