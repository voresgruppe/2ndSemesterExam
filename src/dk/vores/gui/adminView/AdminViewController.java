package dk.vores.gui.adminView;

import dk.vores.BLL.UserManager;
import dk.vores.be.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTableview();
    }
}
