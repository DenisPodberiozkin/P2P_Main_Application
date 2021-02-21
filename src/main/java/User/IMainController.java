package User;

public interface IMainController {
    String createAccount(String password);

    void loginToAccount(String password, String secretPassword);

    void connectToRing();

}
