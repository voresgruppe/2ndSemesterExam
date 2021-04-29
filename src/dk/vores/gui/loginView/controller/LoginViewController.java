package dk.vores.gui.loginView.controller;

import com.sun.javafx.scene.control.InputField;
import dk.vores.BLL.UserManager;
import dk.vores.BLL.UserViewManager;
import dk.vores.DAL.UserReposetory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginViewController implements Initializable {

    @FXML private Button submitLogin;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final UserManager userManager;
    private final UserViewManager userViewManager;

    public void TryLogin(ActionEvent actionEvent) {
        System.out.println(usernameField.getText() + " " + passwordField.getText());
        int res = userManager.login(usernameField.getText(), passwordField.getText());
        if(res != -1){
            //TODO: get the correct view and display it to the user
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
    }
}
