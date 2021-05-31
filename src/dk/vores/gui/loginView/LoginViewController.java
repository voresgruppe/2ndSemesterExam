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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginViewController implements Initializable {

    @FXML private Text lblError;
    @FXML private AnchorPane mainPane;
    @FXML private AnchorPane logoPane;
    @FXML private Button submitLogin;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final UserManager userManager = new UserManager();
    private final UserViewManager userViewManager = new UserViewManager();

    public User tryLogin(String username, String password, boolean isTest) {
        User user = userManager.login(username, password);
        if(isTest){
            return user;
        }
        if(user == null) {
            lblError.setText("User doesnt exist");
        }
        if(user != null){
            lblError.setText("");
            if(user.isAdmin()){
                try {
                    FXMLLoader loader = new FXMLLoader(AdminViewController.class.getResource("view/adminView.fxml"));
                    Parent mainLayout = loader.load();
                    AdminViewController tvc = loader.getController();
                    tvc.setLoggedAdmin(user);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(mainLayout));
                    stage.setTitle(user.getUsername());
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
                    stage.setResizable(false);
                    stage.setTitle(user.getUsername());
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return user;
        }else{
            System.out.println("no user found");
            return null;
        }
    }

    @FXML
    private void handleLogin (ActionEvent actionEvent){
        tryLogin(usernameField.getText(), passwordField.getText(), false);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logoPane.getStyleClass().add("LogoPane");
    }
}
