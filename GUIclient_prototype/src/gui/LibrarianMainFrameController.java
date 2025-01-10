package gui;

<<<<<<< HEAD
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import client.ClientUI;

public class LibrarianMainFrameController {

    @FXML
    private void goShowSubscribers(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/SubscribersTableForm.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Show Subscribers");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/SubscribersTableForm.css").toExternalForm());
            stage.setScene(scene);
            stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                try {
                    if (ClientUI.chat != null) {
                        ClientUI.cc.accept("disconnect");
                        ClientUI.chat.quit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            ((Node) event.getSource()).getScene().getWindow().hide();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goDataReports(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/DataReports.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Data Reports");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/DataReports.css").toExternalForm());
            stage.setScene(scene);
            stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                try {
                    if (ClientUI.chat != null) {
                        ClientUI.cc.accept("disconnect");
                        ClientUI.chat.quit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            ((Node) event.getSource()).getScene().getWindow().hide();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBorrow(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/BorrowForm.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Borrow");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/BorrowForm.css").toExternalForm());
            stage.setScene(scene);
            stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                try {
                    if (ClientUI.chat != null) {
                        ClientUI.cc.accept("disconnect");
                        ClientUI.chat.quit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            ((Node) event.getSource()).getScene().getWindow().hide();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goReadsCard(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/ReadsCard.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Reads Card");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/ReadsCard.css").toExternalForm());
            stage.setScene(scene);
            stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                try {
                    if (ClientUI.chat != null) {
                        ClientUI.cc.accept("disconnect");
                        ClientUI.chat.quit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            ((Node) event.getSource()).getScene().getWindow().hide();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goReturnBook(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/ReturnBookFrame.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Return Book");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/ReturnBookFrame.css").toExternalForm());
            stage.setScene(scene);
            stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                try {
                    if (ClientUI.chat != null) {
                        ClientUI.cc.accept("disconnect");
                        ClientUI.chat.quit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            ((Node) event.getSource()).getScene().getWindow().hide();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goLogOut(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/LandingWindow.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Log Out");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/LandingWindow.css").toExternalForm());
            stage.setScene(scene);
            stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                try {
                    if (ClientUI.chat != null) {
                        ClientUI.cc.accept("disconnect");
                        ClientUI.chat.quit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            ((Node) event.getSource()).getScene().getWindow().hide();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
=======
public class LibrarianMainFrameController {
    
>>>>>>> 3ee753d (Finished search and login page)
}
