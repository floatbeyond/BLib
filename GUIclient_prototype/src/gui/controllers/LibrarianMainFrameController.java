package gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Button;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import client.ClientUI;
import client.NotificationScheduler;
import common.Librarian;
import common.MessageUtils;
import common.Notification;
import client.SharedController;

public class LibrarianMainFrameController {
    
    @FXML private Button btnDataReports;
    @FXML private Button btnShowSubscribers;
    @FXML private Button btnBorrow;
    @FXML private Button btnReaderCard;
    @FXML private Button btnReturnBook;

    @FXML private Label messageLabel;

    @FXML private Button btnNotifications = null;
    @FXML private Button btnCloseNotifications = null;
    @FXML private SplitPane notificationSplitPane;
    @FXML private ListView<String> notificationListView;
    private ObservableList<String> notifications = FXCollections.observableArrayList();

    private Librarian librarian;

    @FXML
    public void initialize() {
        librarian = SharedController.getLibrarian();
        SharedController.setLibrarianMainFrameController(this);
        notificationSplitPane.setVisible(false);
        notificationListView.setItems(notifications);
        notificationListView.setItems(notifications);
        // NotificationScheduler.start("librarian", librarian.getLibrarian_id());
    }


    public void addNotifications(List<Notification> newNotifications) {
        for (int i = newNotifications.size() - 1; i >= 0; i--) {
            notifications.add(0, newNotifications.get(i).getMessage());
        }
    }

    @FXML
    private void showNotifications(ActionEvent event) {
        notificationSplitPane.setVisible(true);
    }

    @FXML
    private void closeNotifications(ActionEvent event) {
        notificationSplitPane.setVisible(false);
    }

    @FXML
    private void goShowSubscribers(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc,"librarian", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/SubscribersTableForm.fxml"));
                Parent root = fxmlLoader.load();

                SharedController.setSubscribersTableController(fxmlLoader.getController());
                Stage stage = new Stage();
                stage.setTitle("Show Subscribers");
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                    try {
                        if (ClientUI.chat != null) {
                            MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , null);
                            ClientUI.chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                ((Node) event.getSource()).getScene().getWindow().hide();
                stage.setResizable(false);
                stage.show();
            } else {
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goDataReports(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc,"librarian", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/DataReports.fxml"));
                Parent root = fxmlLoader.load();
                SharedController.setDataReportsController(fxmlLoader.getController());

                // Fetch and set the reports data after the controller is initialized
                MessageUtils.sendMessage(ClientUI.cc, "librarian", "fetchAllReports", null);

                Stage stage = new Stage();
                stage.setTitle("Data Reports");
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                    try {
                        if (ClientUI.chat != null) {
                            MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , null);
                            ClientUI.chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                ((Node) event.getSource()).getScene().getWindow().hide();
                stage.setResizable(false);
                stage.show();
            } else {
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBorrow(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc,"librarian", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/BorrowForm.fxml"));
                Parent root = fxmlLoader.load();

                SharedController.setBorrowFormController(fxmlLoader.getController());
                NotificationScheduler.stop();
                Stage stage = new Stage();
                stage.setTitle("Borrow");
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                    try {
                        if (ClientUI.chat != null) {
                            MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , null);
                            ClientUI.chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                ((Node) event.getSource()).getScene().getWindow().hide();
                stage.setResizable(false);
                stage.show();
            } else {
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goReaderCard(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc,"librarian", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/ReaderCard.fxml"));
                Parent root = fxmlLoader.load();

                // SharedController.setReaderCardController(fxmlLoader.getController());
                Stage stage = new Stage();
                stage.setTitle("Reads Card");
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                    try {
                        if (ClientUI.chat != null) {
                            MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , null);
                            ClientUI.chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                ((Node) event.getSource()).getScene().getWindow().hide();
                stage.setResizable(false);
                stage.show();
            } else { 
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goReturnBook(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc,"librarian", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/ReturnBookFrame.fxml"));
                Parent root = fxmlLoader.load();

                SharedController.setReturnBookController(fxmlLoader.getController());
                Stage stage = new Stage();
                stage.setTitle("Return Book");
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                    try {
                        if (ClientUI.chat != null) {
                            MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , null);
                            ClientUI.chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                ((Node) event.getSource()).getScene().getWindow().hide();
                stage.setResizable(false);
                stage.show();
            } else { 
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goAddSubscriber(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc,"librarian", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/AddSubscriber.fxml"));
                Parent root = fxmlLoader.load();

                SharedController.setAddSubscriberController(fxmlLoader.getController());
                Stage stage = new Stage();
                stage.setTitle("Return Book");
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                    try {
                        if (ClientUI.chat != null) {
                            MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , null);
                            ClientUI.chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                ((Node) event.getSource()).getScene().getWindow().hide();
                stage.setResizable(false);
                stage.show();
            } else { 
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goLogOut(ActionEvent event) {
        try {
            if (SharedController.getSubscribersTableController() != null) {
                SharedController.getSubscribersTableController().closeAllReaderCards();
            } else System.out.println("SubscribersTableController is null");
            NotificationScheduler.stop();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/LandingWindow.fxml"));
            Pane root = loader.load();
            
            Stage stage = new Stage();
            Scene scene = new Scene(root);			
            stage.setTitle("Landing Window");
            stage.setScene(scene);
            stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                try {
                    if (ClientUI.chat != null) {
                        MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , null);
                        ClientUI.chat.quit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });    
            ((Node)event.getSource()).getScene().getWindow().hide();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayMessage(String message) {
        messageLabel.setText(message);
    }
}
