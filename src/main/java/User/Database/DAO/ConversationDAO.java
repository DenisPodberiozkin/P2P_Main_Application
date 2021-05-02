package User.Database.DAO;

import Encryption.EncryptionController;
import User.NodeManager.Conversation;
import User.NodeManager.Message;

import javax.crypto.SecretKey;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class ConversationDAO {
	private final Connection connection;
	private final SecretKey secretKey;
	private final EncryptionController encryptionController = EncryptionController.getInstance();

	public ConversationDAO(Connection connection, SecretKey secretKey) {
		this.connection = connection;
		this.secretKey = secretKey;
	}

	public void addConversation(Conversation conversation) {
		final String participantId = conversation.getParticipantId();
		final String conversationName = conversation.getConversationName();
		final String encryptedParticipantId = encryptionController.encryptStringByAES(secretKey, participantId);
		final String encryptedConversationName = encryptionController.encryptStringByAES(secretKey, conversationName);

		final String sql = "INSERT INTO conversation (participant_id, conversation_name) VALUES (?,?)";

		try {
			connection.setAutoCommit(false);
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				preparedStatement.setString(1, encryptedParticipantId);
				preparedStatement.setString(2, encryptedConversationName);
				preparedStatement.executeUpdate();
				final long generatedId = preparedStatement.getGeneratedKeys().getLong(1);
				conversation.setId(generatedId);
				connection.commit();
			} catch (SQLException throwables) {
				throwables.printStackTrace();

				connection.rollback();

			} finally {

				connection.setAutoCommit(true);

			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

	}

	public void addMessageToConversation(Conversation conversation, Message message) {
		final long conversationId = conversation.getId();
		final String encryptedSenderId = encryptionController.encryptStringByAES(secretKey, message.getSenderId());
		final String encryptedMessageContent = encryptionController.encryptStringByAES(secretKey, message.getText());

		final String sql = "INSERT INTO message (conversation_id, sender_id, message_content) VALUES (?,?,?)";

		try {
			connection.setAutoCommit(false);
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setLong(1, conversationId);
				preparedStatement.setString(2, encryptedSenderId);
				preparedStatement.setString(3, encryptedMessageContent);
				preparedStatement.executeUpdate();
				connection.commit();
			} catch (SQLException throwables) {
				throwables.printStackTrace();

				connection.rollback();

			} finally {

				connection.setAutoCommit(true);

			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}


	}

	public List<Conversation> getAllConversations(String userId) {
		final String sql = "SELECT * FROM conversation";
		final List<Conversation> conversations = new LinkedList<>();

		try {
			connection.setAutoCommit(false);
			try (Statement statement = connection.createStatement()) {
				ResultSet resultSet = statement.executeQuery(sql);
				while (resultSet.next()) {
					final String encryptedParticipantId = resultSet.getString("participant_id");
					final String encryptedConversationName = resultSet.getString("conversation_name");
					final long conversationId = resultSet.getLong("conversation_id");
					final String participantId = encryptionController.decryptStringByAES(secretKey, encryptedParticipantId);
					final String conversationName = encryptionController.decryptStringByAES(secretKey, encryptedConversationName);
					final Conversation conversation = new Conversation(participantId, conversationName);
					conversation.setId(conversationId);
					final LinkedList<Message> messages = getAllMessages(conversationId, conversationName, userId);
					conversation.addAllMessages(messages);
					conversation.setConversationDAO(this);
					conversations.add(conversation);
				}
				connection.commit();
			} catch (SQLException throwables) {
				throwables.printStackTrace();

				connection.rollback();

			} finally {

				connection.setAutoCommit(true);

			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}


		return conversations;
	}

	private LinkedList<Message> getAllMessages(long conversationId, String conversationName, String userId) {
		final String sql = "SELECT * FROM message WHERE conversation_id = ?";
		final LinkedList<Message> messages = new LinkedList<>();

		try {
			connection.setAutoCommit(false);
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setLong(1, conversationId);
				ResultSet resultSet = preparedStatement.executeQuery();
				connection.commit();
				while (resultSet.next()) {
					final String encryptedSenderId = resultSet.getString("sender_id");
					final String encryptedMessageContent = resultSet.getString("message_content");
					final String senderId = encryptionController.decryptStringByAES(secretKey, encryptedSenderId);
					final String messageContent = encryptionController.decryptStringByAES(secretKey, encryptedMessageContent);

					final boolean isSentByUser = senderId.equals(userId);
					messages.add(new Message(messageContent, senderId, conversationName, isSentByUser));
				}
			} catch (SQLException throwables) {
				throwables.printStackTrace();

				connection.rollback();

			} finally {

				connection.setAutoCommit(true);

			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}


		return messages;
	}

	public void removeConversation(Conversation conversation) {
		final String sql = "DELETE FROM conversation WHERE conversation_id = ?";
		final long conversationId = conversation.getId();


		try {
			connection.setAutoCommit(false);
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				removeAllMessagesFromConversation(conversationId);
				preparedStatement.setLong(1, conversationId);
				preparedStatement.executeUpdate();
				connection.commit();
			} catch (SQLException throwables) {
				throwables.printStackTrace();

				connection.rollback();

			} finally {

				connection.setAutoCommit(true);

			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}


	}

	private void removeAllMessagesFromConversation(long conversationId) {
		final String sql = "DELETE FROM message WHERE conversation_id = ?";

		try {
			connection.setAutoCommit(false);
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setLong(1, conversationId);
				preparedStatement.executeUpdate();
				connection.commit();
			} catch (SQLException throwables) {
				throwables.printStackTrace();

				connection.rollback();

			} finally {

				connection.setAutoCommit(true);

			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}


	}

	public void updateConversationName(long conversationId, String newConversationName) {
		final String sql = "UPDATE conversation SET conversation_name = ? WHERE conversation_id = ?";
		final String encryptedNewConversationName = encryptionController.encryptStringByAES(secretKey, newConversationName);

		try {
			connection.setAutoCommit(false);
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setString(1, encryptedNewConversationName);
				preparedStatement.setLong(2, conversationId);
				preparedStatement.executeUpdate();
				connection.commit();
			} catch (SQLException throwables) {
				throwables.printStackTrace();

				connection.rollback();

			} finally {

				connection.setAutoCommit(true);

			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

	}
}
