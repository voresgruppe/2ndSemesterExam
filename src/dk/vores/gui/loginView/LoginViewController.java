package dk.vores.gui.loginView;

import dk.vores.BLL.UserManager;
import dk.vores.BLL.UserViewManager;
import dk.vores.be.User;
import dk.vores.gui.adminView.AdminViewController;
import dk.vores.gui.userView.UserViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginViewController implements Initializable {

    @FXML private Button submitLogin;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final UserManager userManager;
    private final UserViewManager userViewManager;



    public void TryLogin(ActionEvent actionEvent) {
        User user = userManager.login1(usernameField.getText(), passwordField.getText());
        if(user != null){
            if(user.isAdmin()){
                try {
                    FXMLLoader loader = new FXMLLoader(AdminViewController.class.getResource("view/adminView.fxml"));
                    Parent mainLayout = loader.load();
                    AdminViewController tvc = loader.getController();
                    tvc.setLoggedAdmin(user);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(mainLayout));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    FXMLLoader loader = new FXMLLoader(UserViewController.class.getResource("view/userView.fxml"));
                    Parent mainLayout = loader.load();
                    UserViewController tvc = loader.getController();
                    tvc.setLoggedUser(user);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(mainLayout));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
            System.out.println("no user found");
        }
    }

    public LoginViewController(){
        userManager = new UserManager();
        userViewManager = new UserViewManager();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTestLogin();
    }

    private void setTestLogin(){
        usernameField.setText("testAdmin");
        passwordField.setText("testAdmin");
    }
}
