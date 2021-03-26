package GUI.Navigators;

import GUI.LoginScreen.Login;

public class LoginSideBarNavigator extends Navigator {

    private static Login loginController;

    public static void setLoginController(Login loginController) {
        LoginSideBarNavigator.loginController = loginController;
    }

    public static void changeSideBar(NavigablePane pane) {
        loginController.setSideBar(loadPane(pane));
    }

}
