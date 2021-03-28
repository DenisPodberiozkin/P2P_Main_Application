package User;

import Encryption.EncryptionController;

public class RegistrationModel {
    private final String username;
    private final String password;
    private final String secretePassword;

    public RegistrationModel(String username, String password) {
        this.username = username;
        this.password = password;
        this.secretePassword = EncryptionController.getInstance().generateSecretPassword();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSecretePassword() {
        return secretePassword;
    }
}
