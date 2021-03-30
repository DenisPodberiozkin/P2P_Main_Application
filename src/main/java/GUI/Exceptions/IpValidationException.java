package GUI.Exceptions;

public class IpValidationException extends Exception {
    public IpValidationException(String ip) {
        super("IP address - " + ip + " is not valid");
    }
}
