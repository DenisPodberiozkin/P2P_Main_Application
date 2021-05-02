package User.Main;

public class RegistrationModelBuilder {
    private String username;
    private String password;

    public RegistrationModelBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public RegistrationModelBuilder setPassword(String password) {
        this.password = password;
        return this;
    }


    public RegistrationModel createRegistrationModel() {
        return new RegistrationModel(username, password);
    }
}