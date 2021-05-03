package dk.vores.gui.adminView;

import dk.vores.be.User;
import dk.vores.util.UserError;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ChangeViewController {

    private User currentUser;

    @FXML
    private TextField endY;
    @FXML
    private TextField startY;
    @FXML
    private TextField endX;
    @FXML
    private TextField startX;
    @FXML
    private Button btnClose;
    @FXML
    private Label lblWhoToChange;
    @FXML
    private TextField txtfieldSourcePath;
    @FXML
    private ChoiceBox choiceType;

    public void setClickedUser(User currentUser){
        this.currentUser = currentUser;
        lblWhoToChange.setText("Change the view for: " + currentUser.getUsername());
    }

    public void chooseSource(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) lblWhoToChange.getScene().getWindow();
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose source...");
            File file = fc.showOpenDialog(stage);
            String filePath = file.toString().replaceAll("\\\\","/");
            txtfieldSourcePath.setText(filePath);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void save(ActionEvent actionEvent) {
        if(!showErrorstoUser()){
            //gem i databasen
        }
    }

    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    public void removeLayout(ActionEvent actionEvent) {
    }

    private boolean showErrorstoUser(){
        String header = "Something went wrong...";
        try {
            int startXnum = Integer.parseInt(startX.getText());
            int endXnum = Integer.parseInt(endX.getText());
            int startYnum = Integer.parseInt(startY.getText());
            int endYnum = Integer.parseInt(endY.getText());

            if(startXnum > 100 || startXnum < 0){
                UserError.showError(header,"Start X has to be more than 0 and less than 100");
                return true;
            }else if(endXnum > 100 || endXnum < 0){
                UserError.showError(header,"End X has to be more than 0 and less than 100");
                return true;
            }else if(startYnum > 100 || startYnum < 0){
                UserError.showError(header,"Start Y has to be more than 0 and less than 100");
                return true;
            }else if(endYnum > 100 || endYnum < 0){
                UserError.showError(header,"End Y has to be more than 0 and less than 100");
                return true;
            }else if(choiceType.getItems().isEmpty()){
                UserError.showError(header, "Please provide a type");
                return true;
            }else if(txtfieldSourcePath.getText().isBlank()){
                UserError.showError(header, "Please provide a source");
                return true;
            }
        } catch (NumberFormatException e) {
            UserError.showError(header,"Please provide numbers in Start/End X/Y");
            return true;
        }
        return false;
    }

}
