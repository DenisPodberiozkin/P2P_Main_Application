package GUI;

public class ControllerFactory {

    private static TestController testController;

    public static TestController getTestController() {
        return testController;
    }

    public static void setTestController(TestController testController) {
        ControllerFactory.testController = testController;
    }
}
