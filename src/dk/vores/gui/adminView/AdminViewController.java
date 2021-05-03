package dk.vores.gui.adminView;

import dk.vores.BLL.UserManager;
import dk.vores.be.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminViewController implements Initializable {
    @FXML
    private TableView<User> tblUsers;
    @FXML
    private TableColumn<User, String> tcID;
    @FXML
    private TableColumn<User, String> tcUsername;
    @FXML
    private TableColumn<User, String> tcPassword;
    private User loggedAdmin;
    private User selectedUser;
    private UserManager uMan = new UserManager();

    public User getLoggedAdmin() {
        return loggedAdmin;
    }

    public void setLoggedAdmin(User loggedAdmin) {
        this.loggedAdmin = loggedAdmin;
    }

    public void initTableview(){
        administratorsListener();

        tblUsers.setItems(uMan.getUsersWithoutAdmins());
        tcID.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().getIdProperty());
        tcUsername.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().getUsernameProperty());
        tcPassword.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().getPasswordProperty());
    }

    private void administratorsListener() {
        tblUsers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectedUser = newValue);

        tblUsers.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
                openChangeView(selectedUser);
            }
        });
    }

    private void openChangeView(User selectedUser){
        try {
            FXMLLoader loader = new FXMLLoader(ChangeViewController.class.getResource("view/changeView.fxml"));
            Parent mainLayout = loader.load();
            ChangeViewController cvc = loader.getController();
            cvc.setClickedUser(selectedUser);
            Stage stage = new Stage();
            stage.setScene(new Scene(mainLayout));
            stage.setTitle("Design the view for: " + selectedUser.getUsername());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTableview();
    }
}
