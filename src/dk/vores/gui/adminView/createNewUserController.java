package dk.vores.gui.adminView;

import dk.vores.BLL.UserManager;
import dk.vores.be.User;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class createNewUserController {

    public TextField txtfieldUsername;
    public CheckBox checkMakeAdmin;
    public TextField txtfieldPassword;
    public Button btnCancel;
    private AdminViewController adminViewController;
    private ManageUsersController manageUsersController;
    private UserManager uMan = new UserManager();

    public void setManageUsersController(ManageUsersController manageUsersController) {
        this.manageUsersController = manageUsersController;
    }

    public void setAdminViewController(AdminViewController adminViewController) {
        this.adminViewController = adminViewController;
    }

    public void addUser(ActionEvent actionEvent) {
        User newUser = new User(txtfieldUsername.getText(),txtfieldPassword.getText(),checkMakeAdmin.isSelected());
        uMan.addUser(newUser);
        adminViewController.initTableview();
        manageUsersController.initTable();
        closeWindow();
    }

    public void cancel(ActionEvent actionEvent) {
        closeWindow();
    }

    private void closeWindow(){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
