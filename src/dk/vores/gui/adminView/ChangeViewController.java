package dk.vores.gui.adminView;

import dk.vores.BLL.UserViewManager;
import dk.vores.be.User;
import dk.vores.util.UserError;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class ChangeViewController {

    private User currentUser;
    private final UserViewManager uvMan = new UserViewManager();

    @FXML
    private ChoiceBox choiceType1;
    @FXML
    private TextField txtfieldSourcePath1;
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
        lblWhoToChange.setText("Add a view for: " + currentUser.getUsername());
        String[] items = {"HTML", "CSV", "XML", "Pie chart", "Bar chart"};
        choiceType.setItems(FXCollections.observableArrayList(items));
        choiceType1.setItems(FXCollections.observableArrayList(items));
    }

    public void chooseSource(ActionEvent actionEvent) {
        saveFilePathInTxtfield(txtfieldSourcePath);
    }

    public void chooseSource1(ActionEvent actionEvent) {
        saveFilePathInTxtfield(txtfieldSourcePath1);
    }

    private void saveFilePathInTxtfield(TextField field){
        try{
            Stage stage = (Stage) lblWhoToChange.getScene().getWindow();
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose source...");
            File file = fc.showOpenDialog(stage);
            String filePath = file.toString().replaceAll("\\\\","/");
            field.setText(filePath);
        }catch(Exception e) {
            UserError.showError("Something went wrong...", e.getMessage());
        }

    }

    public void save(ActionEvent actionEvent) {
        if(!showErrorstoUser()){
            int startXvalue = Integer.parseInt(startX.getText());
            int startYvalue = Integer.parseInt(startY.getText());
            int endXvalue = Integer.parseInt(endX.getText());
            int endYvalue = Integer.parseInt(endY.getText());
            String type = choiceType.getSelectionModel().getSelectedItem().toString();
            String source = txtfieldSourcePath.getText();

            uvMan.addViewToUser(currentUser,startXvalue,startYvalue,endXvalue,endYvalue,type,source);
        }
    }

    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    public void removeLayout(ActionEvent actionEvent) {
        String header = "Are you sure you want to clear " + currentUser.getUsername() + "'s current view?";
        Optional<ButtonType> result = UserError.showWarning(header,"Click OK to continue").showAndWait();
        if(result.get() == ButtonType.OK){
            uvMan.clearViewFromUser(currentUser);
        }
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
            }else return isTypeOrSourceBlankForAdd();
        } catch (NumberFormatException e) {
            UserError.showError(header,"Please provide numbers in Start/End X/Y");
            return true;
        }
    }

    private boolean isTypeOrSourceBlankForAdd(){
        String header = "Something went wrong...";
        if(choiceType.getItems().isEmpty()){
            UserError.showError(header, "Please provide a type");
            return true;
        }else if(txtfieldSourcePath.getText().isBlank()){
            UserError.showError(header, "Please provide a source");
            return true;
        }else return false;
    }

    private boolean isTypeOrSourceBlankForUpdate(){
        String header = "Something went wrong...";
        if(choiceType1.getItems().isEmpty()){
            UserError.showError(header, "Please provide a type");
            return true;
        }else if(txtfieldSourcePath1.getText().isBlank()){
            UserError.showError(header, "Please provide a source");
            return true;
        }else return false;
    }


    /*
     jeg hgar ødelagt den  - Cecilie
     */
    public void updateTypeSource(ActionEvent actionEvent) {
        if(!isTypeOrSourceBlankForAdd() && !isTypeOrSourceBlankForUpdate()){
            String newType = choiceType1.getSelectionModel().getSelectedItem().toString();
            String newSource = txtfieldSourcePath1.getText();

            //uvMan.updateTypeSourceFromUser(newType,newSource);
        }
    }


}
