package User.Settings;

public class ApplicationSettingsModel {
    private static int applicationPort = 4444; //Default Port is 4444

    public static int getApplicationPort() {
        return applicationPort;
    }

    public static void setApplicationPort(int applicationPort) {
        ApplicationSettingsModel.applicationPort = applicationPort;
    }

}
