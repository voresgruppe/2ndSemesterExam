package dk.vores.util;

import javafx.scene.control.Alert;

public class UserError {
    private boolean connectionError = false;

    private static UserError single_instance = null;
    private UserError(){}

    public static UserError getInstance(){
        if(single_instance == null){
            single_instance = new UserError();
        }
        return single_instance;
    }

    public Alert fileNotFound(String title, String content){
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText("There was an error regarding a file");
        a.setTitle(title);
        a.setContentText(content);
        a.show();
        return a;
    }

    public Alert databaseError(String title, String content){
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText("There was an error regarding the database");
        a.setTitle(title);
        a.setContentText(content);
        a.show();
        return a;
    }

    public static void showError(String header, String error) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(header);
        a.setTitle(header);
        a.setContentText(error);
        a.show();
    }

    public static Alert showWarning(String header, String text){
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Please confirm...");
        a.setHeaderText(header);
        a.setContentText(text);
        return a;
    }

    public boolean isConnectionError() {
        return connectionError;
    }

    public void setConnectionError(boolean connectionError) {
        this.connectionError = connectionError;
    }
}
