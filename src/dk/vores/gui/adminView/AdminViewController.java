package dk.vores.gui.adminView;

import dk.vores.BLL.DataManager;
import dk.vores.BLL.UserManager;
import dk.vores.BLL.UserViewManager;
import dk.vores.be.DataExample;
import dk.vores.be.DataType;
import dk.vores.be.User;
import dk.vores.be.UserView;
import dk.vores.util.UserError;
import dk.vores.util.ViewUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminViewController implements Initializable {
    public Button btnAddView;
    @FXML
    private AnchorPane paneUserView;
    @FXML
    private TableView<User> tblUsers;
    @FXML
    private TableColumn<User, String> tcID;
    @FXML
    private TableColumn<User, String> tcUsername;
    @FXML
    private TableColumn<User, String> tcPassword;


    private User loggedAdmin;
    private User selectedUser;
    private UserManager uMan = new UserManager();
    private UserViewManager uvMan = new UserViewManager();
    private ViewUtils viewUtils = new ViewUtils();

    private AnchorPane selectedUserBlock = null;

    public User getLoggedAdmin() {
        return loggedAdmin;
    }

    public void setLoggedAdmin(User loggedAdmin) {
        this.loggedAdmin = loggedAdmin;
    }

    public void init(){
        initTableview();
        drawUserView();
    }

    public void initTableview(){
        UserListener();

        tblUsers.setItems(uMan.getUsersWithoutAdmins());
        selectedUser = tblUsers.getItems().get(0);
        tcID.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().getIdProperty());
        tcUsername.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().getUsernameProperty());
        tcPassword.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().getPasswordProperty());
    }
/*
draws userView as the user with the given id would currently see it
 */
    public void drawUserView() {
        ObservableList<UserView> usersViews = uvMan.loadViewsFromUserID(selectedUser.getId());
        if (!usersViews.isEmpty()) {
            double height = paneUserView.getHeight();
            double width = paneUserView.getWidth();

            for (UserView current : usersViews) {
                if (height < current.getEndY()) {
                    height = current.getEndY();
                }
                if (width < current.getEndX()) {
                    width = current.getEndX();
                }


                AnchorPane userBlock = viewUtils.createDraggableAnchorPane(current.getId(), current.getStartX(), current.getStartY(), current.getEndX() - current.getStartX(), current.getEndY() - current.getStartY());
                userBlock.setStyle("-fx-background-color: #f2c31d; -fx-border-color: black; -fx-border-width: 2px 2px 2px 2px; ");
                userBlock.setOnMouseClicked(event->{
                    String markedStyle = "-fx-border-color: magenta; -fx-border-width: 4px 4px 4px 4px;";
                    if(selectedUserBlock!= null && selectedUserBlock.getStyle().contains(markedStyle)){
                        selectedUserBlock.setStyle(selectedUserBlock.getStyle().replace("-fx-border-color: magenta; -fx-border-width: 4px 4px 4px 4px;", ""));
                    }
                    selectedUserBlock = userBlock;
                    selectedUserBlock.setStyle(selectedUserBlock.getStyle()+ "-fx-border-color: magenta; -fx-border-width: 4px 4px 4px 4px;");
                });


                if (current.getType().equals(DataType.BarChart)) {
                    if(current.getSource().matches("test")) {
                        userBlock.setStyle(userBlock.getStyle() + "-fx-background-color: red;");
                    }
                    else{
                        DataManager dMan = new DataManager();
                        List<DataExample> data = dMan.getAllData(current.getSource());

                        userBlock.getChildren().add(viewUtils.buildBarChart(data));
                    }
                }
                else if (current.getType().equals(DataType.PieChart)) {
                    userBlock.setStyle(userBlock.getStyle() + "-fx-background-color: blue;");
                }
                else if (current.getType().equals(DataType.HTML)) {
                    userBlock.setStyle(userBlock.getStyle() + "-fx-background-color: green;");
                }
                else if (current.getType().equals(DataType.Table)) {
                    userBlock.setStyle(userBlock.getStyle() + "-fx-background-color: pink;");
                }
                userBlock.setId(String.valueOf(current.getId()));
                paneUserView.getChildren().add(userBlock);


            }

            paneUserView.setMinHeight(height);
            paneUserView.setMinWidth(width);
        }
    }

    private void UserListener() {
        tblUsers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectedUser = newValue);

        tblUsers.setOnMouseClicked((MouseEvent event) -> {
            if(selectedUser != null){
                newUserView();
            }

        });
    }

    public void newUserView(){
        paneUserView.getChildren().clear();
        drawUserView();
    }

    private void openChangeView(User selectedUser){
        try {
            FXMLLoader loader = new FXMLLoader(ChangeViewController.class.getResource("view/changeView.fxml"));
            Parent mainLayout = loader.load();
            ChangeViewController cvc = loader.getController();
            cvc.setClickedUser(selectedUser);
            cvc.setAdminViewController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(mainLayout));
            stage.setTitle("Design the view for: " + selectedUser.getUsername());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
    }

    public void addViewToUser(ActionEvent actionEvent) {
        openChangeView(selectedUser);
    }

    public void remove(ActionEvent actionEvent) {
        String header = "Are you sure you want remove this view from " + selectedUser.getUsername() + "?";
        Optional<ButtonType> result = UserError.showWarning(header,"Click OK to continue").showAndWait();
        if(result.get() == ButtonType.OK){
            try{
                int parseId = Integer.parseInt(selectedUserBlock.getId());
                uvMan.removeViewFromUser(parseId);
                paneUserView.getChildren().remove(selectedUserBlock);
            } catch (NumberFormatException e) {
                UserError.showError("Somethig went wrong", "ID of window is not a number");
            }
        }
    }

    public void openCreateNewUser(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(ChangeViewController.class.getResource("view/createNewUserView.fxml"));
            Parent mainLayout = loader.load();
            createNewUserController cnuc = loader.getController();
            cnuc.setAdminViewController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(mainLayout));
            stage.setTitle("Create a new user");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(ActionEvent actionEvent) {
        uMan.deleteUser(selectedUser);
        initTableview();
    }
}
