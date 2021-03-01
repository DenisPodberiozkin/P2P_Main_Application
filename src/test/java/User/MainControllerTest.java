package User;

import org.junit.jupiter.api.Test;

class MainControllerTest {


    @Test
    void createAccount() {
        IMainController mainController = MainController.getInstance();

        final String password = "123156";
        final String secretPassword = mainController.createAccount(password);
        System.out.println(secretPassword);
    }

    @Test
    void loginToAccount() {
        IMainController mainController = MainController.getInstance();
        final String password = "123156";
        final String secretPassword = "m/4HibxszkM9XyYLrffCt1UKntC3qDEF7YsmO01tm38=";
        mainController.loginToAccount(password, secretPassword);
    }


}