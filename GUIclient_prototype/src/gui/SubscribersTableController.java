package gui;

import java.net.URL;
import java.util.ResourceBundle;

import client.ClientUI;
import common.Subscriber;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.util.Duration;

@SuppressWarnings("rawtypes")


public class SubscribersTableController implements Initializable {

    @FXML private TableView<Subscriber> subscriberTable;
    @FXML private TableColumn<Subscriber, Integer> colID;
    @FXML private TableColumn<Subscriber, String> colName;
    @FXML private TableColumn<Subscriber, Integer> colHistory;
    @FXML private TableColumn<Subscriber, String> colPhone;
    @FXML private TableColumn<Subscriber, String> colEmail;

	@FXML private Label copiedLabel;

    @FXML private Button btnBack = null;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Initializing controller...");
		
		// Verify FXML injection
		assert subscriberTable != null : "fx:id=\"subscriberTable\" was not injected";
		assert colID != null : "fx:id=\"colID\" was not injected";
		assert colName != null : "fx:id=\"colName\" was not injected";
		assert colHistory != null : "fx:id=\"colHistory\" was not injected";
		assert colPhone != null : "fx:id=\"colPhone\" was not injected";
		assert colEmail != null : "fx:id=\"colEmail\" was not injected";
        assert copiedLabel != null : "fx:id=\"copiedLabel\" was not injected";

		// Initialize columns
		setupColumns();

		// Enable cell selection
		subscriberTable.getSelectionModel().setCellSelectionEnabled(true);
		subscriberTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		subscriberTable.setEditable(false);
	
		// Copy functionality - Ctrl + C
		subscriberTable.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.C && event.isControlDown()) {
				copySelectionToClipboard();
			}
		});

		// Copy functionality - Mouse press
		subscriberTable.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				copySelectionToClipboard();
			}
    	});
		
		// Load initial data
		Platform.runLater(() -> {
			System.out.println("Loading initial data...");
			ClientUI.cc.accept("show");
		});
	}

	private void setupColumns() {
		if (colID == null || subscriberTable == null) {
            System.err.println("ERROR: Table components not properly injected");
            return;
        }

        // Set value factories
        colID.setCellValueFactory(new PropertyValueFactory<>("sub_id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("sub_name"));
        colHistory.setCellValueFactory(new PropertyValueFactory<>("detailed_sub_history"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("sub_phone_num"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("sub_email"));

        // Set up non-editable cell factories
        colID.setCellFactory(column -> createNonEditableCell());
        colName.setCellFactory(column -> createNonEditableCell());
        colHistory.setCellFactory(column -> createNonEditableCell());
        colPhone.setCellFactory(column -> createNonEditableCell());
        colEmail.setCellFactory(column -> createNonEditableCell());
	}

	private <T> TableCell<Subscriber, T> createNonEditableCell() {
        return new TableCell<Subscriber, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setStyle("-fx-selection-bar: -fx-accent; -fx-selection-bar-non-focused: -fx-accent;");
            }
        };
    }

	private void copySelectionToClipboard() {
        StringBuilder clipboardString = new StringBuilder();
        ObservableList<TablePosition> positionList = subscriberTable.getSelectionModel().getSelectedCells();

        int prevRow = -1;
        for (TablePosition position : positionList) {
            if (prevRow != -1 && prevRow != position.getRow()) {
                clipboardString.append('\n');
            }
            Object cell = subscriberTable.getColumns().get(position.getColumn())
                        .getCellData(position.getRow());
            clipboardString.append(cell == null ? "" : cell.toString()).append('\t');
            prevRow = position.getRow();
        }

        final ClipboardContent content = new ClipboardContent();
        content.putString(clipboardString.toString());
        Clipboard.getSystemClipboard().setContent(content);

		showCopiedMessage();
    }

	private void showCopiedMessage() {
        copiedLabel.setVisible(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> copiedLabel.setVisible(false));
        pause.play();
	}

	public void parseSubscriberList(ObservableList<Subscriber> subscribers) {
		if (subscribers == null) return;
		subscriberTable.setItems(subscribers);
		System.out.println("Loaded " + subscribers.size() + " subscribers");
	}

    public void goBackBtn(ActionEvent event) throws Exception {
        System.out.println("goBackBtn clicked");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainFrame.fxml"));
		Pane root = loader.load();
		
		Stage primaryStage = new Stage();
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/MainFrame.css").toExternalForm());
		primaryStage.setTitle("Library Tool");
		primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
            try {
                if (ClientUI.chat != null) {
                    ClientUI.cc.accept("disconnect");
                    ClientUI.chat.quit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
		((Node)event.getSource()).getScene().getWindow().hide();
		primaryStage.setResizable(false);
		primaryStage.show();
    }
}
