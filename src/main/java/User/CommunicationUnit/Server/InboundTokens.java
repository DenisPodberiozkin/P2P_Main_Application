package User.CommunicationUnit.Server;

public enum InboundTokens {
    HAS_NEIGHBOURS("HN"), PING("PING"), FIND("FIND"), NEW_PREDECESSOR_NOTIFICATION("NPN"), GET_PREDECESSOR("GP"),
    GET_SUCCESSORS_LIST("GSL");

    private final String token;

    InboundTokens(String token) {
        this.token = token;
    }

    public static InboundTokens findByValue(String value) throws IllegalArgumentException {
        for (InboundTokens token : values()) {
            if (token.getToken().equals(value)) {
                return token;
            }
        }
        throw new IllegalArgumentException("No inbound token found by value " + value);
    }
/*
    public InboundTokens getTest(String token){
        InboundTokens returnToken;
        switch (token){
            case "HN": return InboundTokens.HAS_NEIGHBOURS;
            default:
                throw new IllegalStateException("Unexpected value: " + token);
        }
    }
*/

    public String getToken() {
        return token;
    }
}
