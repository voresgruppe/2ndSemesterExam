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

    public static Alert showWarning(String header, String text){
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation");
        a.setHeaderText(header);
        a.setContentText(text);
        return a;
    }
}
