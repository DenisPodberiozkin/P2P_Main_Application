package User.CommunicationUnit.Server;

public enum InboundTokens {
	HAS_NEIGHBOURS("HN"), GET_NODE_INFORMATION("GNI"), PING("PING"), FIND("FIND"), NEW_PREDECESSOR_NOTIFICATION("NPN"), GET_PREDECESSOR("GP"),
	GET_SUCCESSORS_QUEUE("GSQ"), TRANSFER_MESSAGE("TM"), CREATE_SECURE_CHANNEL("CSC"), CREATE_SECURE_MESSAGE_SESSION("CSMS");

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

	public String getToken() {
		return token;
	}
}
