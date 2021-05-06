package dk.vores.gui.adminView;

import dk.vores.BLL.UserManager;
import dk.vores.BLL.UserViewManager;
import dk.vores.be.DataType;
import dk.vores.be.User;
import dk.vores.be.UserView;
import dk.vores.util.ViewUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AdminViewController implements Initializable {
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
                Rectangle userBlock = viewUtils.createDraggableRectangle(current.getId(), current.getStartX(), current.getStartY(), current.getEndX() - current.getStartX(), current.getEndY() - current.getStartY());

                userBlock.setStroke(Color.BLACK);

                //midlertidig for at illustrere typer
                userBlock.setFill(Color.WHITE);
                if (current.getType().equals(DataType.BarChart)) {
                    userBlock.setFill(Color.RED);
                }
                paneUserView.getChildren().add(userBlock);


            }
            paneUserView.setMinHeight(height+10);
            paneUserView.setMinWidth(width+10);
        }
    }

    private void UserListener() {
        tblUsers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectedUser = newValue);

        tblUsers.setOnMouseClicked((MouseEvent event) -> {
            if(selectedUser != null){
                newUserView();
            }
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
                openChangeView(selectedUser);
            }
        });
    }

    private void newUserView(){
        paneUserView.getChildren().clear();
        drawUserView();
    }

    private void openChangeView(User selectedUser){
        try {
            FXMLLoader loader = new FXMLLoader(ChangeViewController.class.getResource("view/changeView.fxml"));
            Parent mainLayout = loader.load();
            ChangeViewController cvc = loader.getController();
            cvc.setClickedUser(selectedUser);
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
}
