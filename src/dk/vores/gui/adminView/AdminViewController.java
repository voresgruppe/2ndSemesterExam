package dk.vores.gui.adminView;

import com.gembox.spreadsheet.SpreadsheetInfo;
import dk.vores.BLL.UserManager;
import dk.vores.BLL.UserViewManager;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminViewController implements Initializable {
    @FXML
    private Button btnAddView;
    @FXML
    private Button btn_showData;
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

    private boolean isDataShowing =false;

    private Rectangle selectedUserBlock = null;

    public User getLoggedAdmin() {
        return loggedAdmin;
    }

    public void setLoggedAdmin(User loggedAdmin) {
        this.loggedAdmin = loggedAdmin;
    }

    public void init(){
        initTableview();
        newUserView();

        SpreadsheetInfo.setLicense("FREE-LIMITED-KEY");
    }

    public void initTableview(){
        UserListener();

        tblUsers.setItems(uMan.getUsersWithoutAdmins());
        selectedUser = tblUsers.getItems().get(0);
        tcID.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().getIdProperty());
        tcUsername.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().getUsernameProperty());
        tcPassword.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().getPasswordProperty());
    }



    public void drawUserView_editMode() {
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
                userBlock.setFill(Paint.valueOf(viewUtils.matchDatatypeToColor(current.getType())));

                userBlock.setOnMouseClicked(event->{
                    String markedStyle = "-fx-stroke: blue; -fx-stroke-width: 5;";
                    if(selectedUserBlock!= null && selectedUserBlock.getStyle().contains(markedStyle)){
                        selectedUserBlock.setStyle(selectedUserBlock.getStyle().replace(markedStyle, ""));
                    }
                    selectedUserBlock = userBlock;
                    selectedUserBlock.setStyle(selectedUserBlock.getStyle()+ markedStyle);
                });



                userBlock.setId(String.valueOf(current.getId()));
                paneUserView.getChildren().add(userBlock);


            }

            paneUserView.setMinHeight(height);
            paneUserView.setMinWidth(width);
        }
    }

    public void drawUserView_dataMode() {
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

                AnchorPane userBlock = viewUtils.createAnchorPane(current.getId(), current.getStartX(), current.getStartY(), current.getEndX() - current.getStartX(), current.getEndY() - current.getStartY());


                userBlock.setStyle("-fx-background-color: #ffff; -fx-border-color: black; -fx-border-width: 2px 2px 2px 2px; ");
                DataType currentType = current.getType();
                userBlock.setStyle("-fx-background-color: " + viewUtils.matchDatatypeToColor(currentType) + ";");

                if(current.getSource().matches("test")) {
                    Label label = new Label();
                    label.setText(currentType.name() + "  Block");
                    label.setVisible(true);
                    label.setStyle("-fx-background-color: white");
                    label.setWrapText(true);
                    userBlock.getChildren().add(label);
                }
                else{
                    switch (currentType){
                        case PieChart:
                            PieChart pieChart = viewUtils.buildPieChart_CSV(current.getSource());
                            pieChart.setPrefHeight(userBlock.getPrefHeight());
                            pieChart.setPrefWidth(userBlock.getPrefWidth());
                            userBlock.getChildren().add(pieChart);
                            break;
                        case BarChart:
                            BarChart barChart = viewUtils.buildBarChart_CSV(current.getSource());
                            barChart.setPrefHeight(userBlock.getPrefHeight());
                            barChart.setPrefWidth(userBlock.getPrefWidth());
                            userBlock.getChildren().add(barChart);
                            break;
                        case HTML:
                            WebView webView = viewUtils.showWeb("https://" + current.getSource());
                            webView.setPrefHeight(userBlock.getPrefHeight());
                            webView.setPrefWidth(userBlock.getPrefWidth());
                            userBlock.getChildren().add(webView);
                            break;
                        case Table:
                            TableView tableView;
                            if(current.getSource().endsWith(".xlsx")){
                                tableView = viewUtils.showExcel(current.getSource());
                            }
                            else {
                                tableView = viewUtils.buildTableView_CSV(current.getSource());
                            }
                            tableView.setPrefHeight(userBlock.getPrefHeight());
                            tableView.setPrefWidth(userBlock.getPrefWidth());
                            userBlock.getChildren().add(tableView);
                            break;
                        case Undetermined:
                            break;
                        case PDF:
                            //fuck pdf
                            break;
                        default:
                            break;
                    }
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
        if(isDataShowing){ drawUserView_dataMode(); }
        else {
            drawUserView_editMode();
        }
    }

    private void openChangeView(User selectedUser){
        try {
            FXMLLoader loader = new FXMLLoader(ChangeViewController.class.getResource("view/addBlockView.fxml"));
            Parent mainLayout = loader.load();
            ChangeViewController cvc = loader.getController();
            cvc.setClickedUser(selectedUser);
            cvc.setAdminViewController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(mainLayout));
            stage.setTitle("Design the view for: " + selectedUser.getUsername());
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
    }

    public void addBlockToUser(ActionEvent actionEvent) {
        openChangeView(selectedUser);
    }

    public void removeBlock(ActionEvent actionEvent) {
        String header = "Are you sure you want remove this view from " + selectedUser.getUsername() + "?";
        Optional<ButtonType> result = UserError.showWarning(header,"Click OK to continue").showAndWait();
        if(result.get() == ButtonType.OK){
            try{
                int parseId = Integer.parseInt(selectedUserBlock.getId());
                uvMan.removeViewFromUser(parseId);
                paneUserView.getChildren().remove(selectedUserBlock);
                newUserView();
            } catch (NumberFormatException e) {
                UserError.showError("Something went wrong", "ID of window is not a number");
            }
        }
    }

    public void manageUsers(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(ChangeViewController.class.getResource("view/manageUsersView.fxml"));
            Parent mainLayout = loader.load();
            ManageUsersController muc = loader.getController();
            muc.setAdminViewController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(mainLayout));
            stage.setTitle("Manage users");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showData(ActionEvent actionEvent) {
        isDataShowing = !isDataShowing;
        newUserView();
        if(isDataShowing){
            btn_showData.setText("edit mode");
        }
        else{
            btn_showData.setText("show data");
        }
    }

}
