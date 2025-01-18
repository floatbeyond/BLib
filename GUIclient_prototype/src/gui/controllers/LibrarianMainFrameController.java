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
import javafx.scene.control.Button;

import client.ClientUI;
import common.Librarian;
import common.MessageUtils;
import client.SharedController;

public class LibrarianMainFrameController {
    
    @FXML private Button btnDataReports;
    @FXML private Button btnShowSubscribers;
    @FXML private Button btnBorrow;
    @FXML private Button btnReaderCard;
    @FXML private Button btnReturnBook;

    @FXML private Label messageLabel;

    private Librarian librarian;

    @FXML
    public void initialize() {
        librarian = SharedController.getLibrarian();
    }

    @FXML
    private void goShowSubscribers(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc,"user", "connect" , null);
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
            MessageUtils.sendMessage(ClientUI.cc,"user", "connect" , null);
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
            MessageUtils.sendMessage(ClientUI.cc,"user", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/BorrowForm.fxml"));
                Parent root = fxmlLoader.load();

                SharedController.setBorrowFormController(fxmlLoader.getController());

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
            MessageUtils.sendMessage(ClientUI.cc,"user", "connect" , null);
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
            MessageUtils.sendMessage(ClientUI.cc,"user", "connect" , null);
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
            MessageUtils.sendMessage(ClientUI.cc,"user", "connect" , null);
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
