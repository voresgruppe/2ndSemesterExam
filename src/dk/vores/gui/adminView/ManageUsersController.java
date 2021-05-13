package dk.vores.gui.adminView;

import dk.vores.BLL.UserManager;
import dk.vores.be.User;
import dk.vores.util.UserError;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageUsersController implements Initializable {
    public Button btnCancel;
    public TableView<User> tableView;
    public TableColumn<User, String> tblID;
    public TableColumn<User, String> tblUsername;
    public TableColumn<User, String> tblPassword;
    public TableColumn<User, String> tblAdmin;
    private AdminViewController adminViewController;
    private UserManager uMan = new UserManager();
    private User selectedUser;

    public void setAdminViewController(AdminViewController adminViewController) {
        this.adminViewController = adminViewController;
    }

    public void deleteUser(ActionEvent actionEvent) {
        String header = "Are you sure you want delete " + selectedUser.getUsername() + "? This can not be undone";
        Optional<ButtonType> result = UserError.showWarning(header,"Click OK to continue").showAndWait();
        if(result.get() == ButtonType.OK){
            uMan.deleteUser(selectedUser);
            adminViewController.initTableview();
            initTable();
        }
    }

    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void initTable(){
        tableView.setItems(uMan.getAllUsers());
        selectedUser = tableView.getItems().get(0);
        tblID.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().getIdProperty());
        tblUsername.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().getUsernameProperty());
        tblPassword.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().getPasswordProperty());
        tblAdmin.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().getIsAdminProperty());
    }

    private void userListener() {
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectedUser = newValue);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable();
        userListener();
    }

    public void createNewUser(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(ChangeViewController.class.getResource("view/createNewUserView.fxml"));
            Parent mainLayout = loader.load();
            createNewUserController cnuc = loader.getController();
            cnuc.setAdminViewController(adminViewController);
            cnuc.setManageUsersController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(mainLayout));
            stage.setTitle("Create a new user");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
