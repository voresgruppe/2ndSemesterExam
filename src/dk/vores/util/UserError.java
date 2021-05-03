package dk.vores.util;

import javafx.scene.control.Alert;

public class UserError {
    public static void showError(String header, String error) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(header);
        a.setTitle(header);
        a.setContentText(error);
        a.show();
    }
}
