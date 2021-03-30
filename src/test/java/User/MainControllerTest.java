package User;

import org.junit.jupiter.api.Test;

class MainControllerTest {


    @Test
    void createAccount() {
        IMainController mainController = MainController.getInstance();

        final String password = "123156";
//         mainController.createAccount(password, "132", "Denis");
    }

    @Test
    void loginToAccount() {
        IMainController mainController = MainController.getInstance();
        final String password = "123156";
        final String secretPassword = "132";
//        mainController.loginToAccount(password, secretPassword, "Denis");
    }


}