package User.CommunicationUnit.Client;

public enum OutboundTokens {
    GET_LAST_NODE("GETLN"), SET_LAST_NODE("SETLN"), REMOVE_UNREACHABLE_LAST_CONNECTED_NODE("REMOVE");

    private final String token;

    OutboundTokens(String token) {
        this.token = token;
    }


    public String getToken() {
        return token;
    }
}
