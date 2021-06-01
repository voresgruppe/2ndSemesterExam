package dk.vores;

import dk.vores.be.User;
import dk.vores.util.UserError;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private final UserError error = UserError.getInstance();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        printTestLogin();
        Parent root = FXMLLoader.load(getClass().getResource("gui/loginView/view/loginView.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Login");
        if(!error.isConnectionError()) {
            stage.show();
        }
    }

    private void printTestLogin(){
        System.out.println("test Admin: username = testAdmin password= testAdmin");
        System.out.println("test user: username = test password= test");
    }
}
