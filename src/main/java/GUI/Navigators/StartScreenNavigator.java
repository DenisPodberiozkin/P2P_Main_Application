package GUI.Navigators;

import GUI.StartScreen;

public class StartScreenNavigator extends Navigator {
    private static StartScreen startScreenController;

    public static void setStartScreenController(StartScreen startScreenController) {
        StartScreenNavigator.startScreenController = startScreenController;
    }

    public static void changeMainScreen(NavigablePane pane) {
        startScreenController.changeMainScreen(loadPane(pane));
    }
}
