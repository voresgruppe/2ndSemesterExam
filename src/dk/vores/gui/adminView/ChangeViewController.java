package dk.vores.gui.adminView;

import dk.vores.BLL.UserViewManager;
import dk.vores.be.DataType;
import dk.vores.be.User;
import dk.vores.util.UserError;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class ChangeViewController{


    public ComboBox comboUpdateTime;
    private User currentUser;
    private final UserViewManager uvMan = new UserViewManager();
    private AdminViewController adminViewController;

    @FXML
    private Button btnClose;
    @FXML
    private Label lblWhoToChange;
    @FXML
    private TextField txtfieldSourcePath;
    @FXML
    private ChoiceBox<DataType> choiceType;
    @FXML
    private TextField txtUpdateTime;

    private AnchorPane anchorPane;



    public void setClickedUser(User currentUser, AnchorPane pane){
        this.anchorPane = pane;
        this.currentUser = currentUser;
        lblWhoToChange.setText("Add a view for: " + currentUser.getUsername());
        choiceType.setItems(FXCollections.observableArrayList(DataType.values()));
        choiceType.getSelectionModel().selectFirst();
        txtfieldSourcePath.setText("test");
        comboUpdateTime.setItems(FXCollections.observableArrayList(150, 300, 600));
        comboUpdateTime.getSelectionModel().select(1);
    }

    public void chooseSource(ActionEvent actionEvent) {
        saveFilePathInTxtfield(txtfieldSourcePath);
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

    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    public void removeLayout(ActionEvent actionEvent) {
        String header = "Are you sure you want to clear " + currentUser.getUsername() + "'s current view?";
        Optional<ButtonType> result = UserError.showWarning(header,"Click OK to continue").showAndWait();
        if(result.get() == ButtonType.OK){
            anchorPane.getChildren().clear();
            uvMan.clearViewFromUser(currentUser);
        }
    }

    private boolean showErrorstoUser(){
        String header = "Something went wrong...";
        if(choiceType.getItems().isEmpty()){
            UserError.showError(header, "Please provide a type");
            return true;
        }else if(txtfieldSourcePath.getText().isBlank()){
            UserError.showError(header, "Please provide a source");
            return true;
        }else return false;
    }

    public void addView(ActionEvent actionEvent) {
        if(!showErrorstoUser()){
            String type = choiceType.getSelectionModel().getSelectedItem().toString();
            String source = "test";

            if(txtfieldSourcePath.getText()!= null &&!txtfieldSourcePath.getText().isEmpty()){
                source = txtfieldSourcePath.getText();
            }

            int updateTime = 300;
            if(comboUpdateTime.getValue()!= null &&!comboUpdateTime.getValue().toString().isEmpty()){
                updateTime = Integer.parseInt(comboUpdateTime.getValue().toString());
            }


            uvMan.addViewToUser(currentUser, 50, 50, 300, 300, type, source, updateTime);
            adminViewController.newUserView();
            Stage stage = (Stage) btnClose.getScene().getWindow();
            stage.close();
        }
    }

    public void setAdminViewController(AdminViewController adminViewController) {
        this.adminViewController = adminViewController;
    }
}
