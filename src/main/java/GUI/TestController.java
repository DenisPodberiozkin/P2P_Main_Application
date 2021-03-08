/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package GUI;

import User.CommunicationUnit.Client.OutboundConnection;
import User.NodeManager.Node;
import User.NodeManager.User;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Queue;
import java.util.ResourceBundle;

public class TestController implements Initializable {

    private final ObservableList<OutboundConnection> outboundConnectionsTableData = FXCollections.observableArrayList();
    private final ObservableList<Node> fingerTableData = FXCollections.observableArrayList();
    private final ObservableList<Node> successorsTableData = FXCollections.observableArrayList();

    @FXML
    private TextField receiverIdField;
    @FXML
    private TextField messageField;
    @FXML
    private TableColumn<Node, String> successorPort;
    @FXML
    private TableColumn<Node, String> successorIP;
    @FXML
    private TableColumn<Node, String> successorID;
    @FXML
    private TableView<Node> successorsTable;
    @FXML
    private TableView<Node> fingerTable;
    @FXML
    private TableColumn<Node, Integer> fingerPort;
    @FXML
    private TableColumn<Node, String> fingerIP;
    @FXML
    private TableColumn<Node, String> fingerID;
    @FXML
    private Label portLabel;
    @FXML
    private Label nodeNameLabel;
    @FXML
    private Label successorLabel;
    @FXML
    private Label predecessorLabel;
    @FXML
    private TableView<OutboundConnection> outboundTable;
    @FXML
    private TableColumn<OutboundConnection, String> outID;
    @FXML
    private TableColumn<OutboundConnection, String> outIP;
    @FXML
    private TableColumn<OutboundConnection, String> outPort;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerFactory.setTestController(this);

        outID.setCellValueFactory(cellData -> {
            final Node assignedNode = cellData.getValue().getAssignedNode();
            if (assignedNode != null) {
                return new SimpleStringProperty(assignedNode.getId());
            }

            return new SimpleStringProperty("NONE");
        });

        outIP.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIp()));
        outPort.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getPort())));
        outboundTable.setItems(outboundConnectionsTableData);

        fingerID.setCellValueFactory(new PropertyValueFactory<>("Id"));
        fingerIP.setCellValueFactory(new PropertyValueFactory<>("Ip"));
        fingerPort.setCellValueFactory(new PropertyValueFactory<>("Port"));
        fingerTable.setItems(fingerTableData);

        successorID.setCellValueFactory(new PropertyValueFactory<>("Id"));
        successorIP.setCellValueFactory(new PropertyValueFactory<>("Ip"));
        successorPort.setCellValueFactory(new PropertyValueFactory<>("Port"));
        successorsTable.setItems(successorsTableData);


        setNodeNameLabelText("");
        setSuccessorLabelText("");
        setPredecessorLabelText("");
        setPortLabelText(0);


    }

    public void setNodeNameLabelText(String text) {
        Platform.runLater(() -> nodeNameLabel.setText("Node ID " + text));
    }

    public void setSuccessorLabelText(String text) {
        Platform.runLater(() -> successorLabel.setText("Successor ID " + text));

    }

    public void setPredecessorLabelText(String text) {
        Platform.runLater(() -> predecessorLabel.setText("Predecessor ID " + text));
    }

    public void setPortLabelText(int port) {
        Platform.runLater(() -> portLabel.setText("Port " + port));
    }

    public void addOutboundConnection(OutboundConnection connection) {
        outboundConnectionsTableData.add(connection);
    }

    public void removeOutboundConnection(OutboundConnection connection) {
        outboundConnectionsTableData.remove(connection);
    }

    public void addNodeToFingerTable(Node node) {
        fingerTableData.add(node);
    }

    public void removeNodeFromFIngerTable(Node node) {
        fingerTableData.remove(node);
    }

    public void updateSuccessorsData(Queue<Node> successors) {
        successorsTableData.clear();
        successorsTableData.addAll(successors);
    }

    @FXML
    private void sendButtonAction() {
        String receiverId = receiverIdField.getText();
        String text = messageField.getText();
        User.getInstance().sendMessage(receiverId, text);
    }
}
