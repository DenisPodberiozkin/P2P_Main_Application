package User.Database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDataBase extends Database {

	public UserDataBase() {
		super("src/main/resources/db/");
	}

	/**
	 * Connect to the database or creates and initializes the new database
	 *
	 * @param dbName database name
	 * @return Connection to the database
	 */

	public Connection connectToUserDatabase(String dbName, String username) throws SQLException, FileNotFoundException {
		String fullPath = path + username + "/" + dbName + ".db";
		super.connect(fullPath);
		return connection;

	}

	public void createDb(String dbName, String username) throws IOException {
		final File file = new File(path + username + "/" + dbName + ".db");
		file.getParentFile().mkdir();
		file.createNewFile();
	}

	public void initDb(String dbName, Connection connection) throws SQLException {

		final String userDataSQL =
				"create table " + dbName +
						"(   public_key  blob," +
						"    private_key blob," +
						"    id          text not null" +
						"        constraint " + dbName + "_pk" +
						"            primary key" +
						");";

		final String conversationSQL =
				"create table conversation\n" +
						"(\n" +
						"    participant_id    text    not null,\n" +
						"    conversation_name text,\n" +
						"    conversation_id   INTEGER not null\n" +
						"        constraint conversation_pk\n" +
						"            primary key autoincrement\n" +
						");\n" +
						"\n" +
						"create unique index conversation_conversation_id_uindex\n" +
						"    on conversation (conversation_id);\n";

		final String messageSQL =
				"create table message\n" +
						"(\n" +
						"    conversation_id INTEGER\n" +
						"        references conversation,\n" +
						"    sender_id       text,\n" +
						"    message_content text\n" +
						");\n" +
						"\n";

		try (Statement statement = connection.createStatement()) {
			statement.execute(userDataSQL);
			statement.execute(conversationSQL);
			statement.execute(messageSQL);
		}


	}

}
