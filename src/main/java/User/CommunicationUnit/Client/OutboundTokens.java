package User.CommunicationUnit.Client;

public enum OutboundTokens {
    GET_LAST_NODE("GETLN"), SET_LAST_NODE("SETLN"), REMOVE_UNREACHABLE_LAST_CONNECTED_NODE("REMOVE");

    private final String token;

    OutboundTokens(String token) {
        this.token = token;
    }

    public static OutboundTokens findByValue(String value) throws IllegalArgumentException {
        for (OutboundTokens token : values()) {
            if (token.getToken().equals(value)) {
                return token;
            }
        }
        throw new IllegalArgumentException("No outbound token found by value " + value);
    }


    public String getToken() {
        return token;
    }
}
