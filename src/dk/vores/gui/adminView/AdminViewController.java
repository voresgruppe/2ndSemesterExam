package dk.vores.gui.adminView;

import com.gembox.spreadsheet.SpreadsheetInfo;
import dk.vores.BLL.UserManager;
import dk.vores.BLL.UserViewManager;
import dk.vores.be.DataType;
import dk.vores.be.User;
import dk.vores.be.UserView;
import dk.vores.util.UserError;
import dk.vores.util.ViewUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.TextField;
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
    private Label lblEditError;
    @FXML
    private Button btnSaveEdit;
    @FXML
    private AnchorPane paneEdit;
    @FXML
    private TextField txtEditSource;
    @FXML
    private ComboBox<DataType> comboEditType;
    @FXML
    private ComboBox<Integer> comboEditUpdateTime;
    @FXML
    private Button btnAddView;
    @FXML
    private Button btn_showData;
    @FXML
    private AnchorPane paneUserView;
    @FXML
    private TableView<User> tblUsers;
    @FXML
    private TableColumn<User, String> tcUsername;

    private User loggedAdmin;
    private User selectedUser;
    private UserManager uMan = new UserManager();
    private UserViewManager uvMan = new UserViewManager();
    private ViewUtils viewUtils = ViewUtils.getInstance();

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
        tcUsername.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().getUsernameProperty());
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
                    initEditBlock(Integer.parseInt(userBlock.getId()));
                    selectedUserBlock.setStyle(selectedUserBlock.getStyle()+ markedStyle);
                });



                userBlock.setId(String.valueOf(current.getId()));
                paneUserView.getChildren().add(userBlock);


            }

            paneUserView.setMinHeight(height);
            paneUserView.setMinWidth(width);
        }
    }

    public void initEditBlock(int id){
        UserView uv = uvMan.getViewFromID(id);

        lblEditError.setVisible(false);
        lblEditError.setTextFill(Paint.valueOf("red"));
        txtEditSource.setText(uv.getSource());
        comboEditType.setItems(FXCollections.observableArrayList(DataType.values()));
        comboEditType.setValue(uv.getType());
        comboEditUpdateTime.setValue(uv.getUpdateTime());


        comboEditUpdateTime.setItems(FXCollections.observableArrayList(150, 300, 600));

        EventHandler<ActionEvent> event = e -> {
            Boolean errorMade = false;
            if(txtEditSource.getText()!= null){
                String source = txtEditSource.getText();
                if(source.isEmpty()){
                    uvMan.updateSourceFromID(id, "test");
                }
                else if(comboEditType.getValue() == DataType.Table && (source.endsWith(".csv") || source.endsWith(".xlsx"))){
                    uvMan.updateSourceFromID(id,source);
                }
                else if((comboEditType.getValue() == DataType.BarChart || comboEditType.getValue() == DataType.PieChart) && source.endsWith(".xml")){
                    uvMan.updateSourceFromID(id,source);
                }
                else if(source.endsWith(".csv")){
                    uvMan.updateSourceFromID(id,source);
                }
                else {
                    lblEditError.setText("Invalid Source-filetype");
                    errorMade = true;
                }
            }
            if (comboEditType.getValue() != null) {
                uvMan.updateTypeFromID(id, comboEditType.getValue().name());
            }
            if(comboEditUpdateTime.getValue() != null){
                uvMan.updateUpdateTimeFromID(id, comboEditUpdateTime.getValue());
            }
            if(!errorMade) {
                newUserView();
            }
        };
        btnSaveEdit.setOnAction(event);
        paneEdit.setVisible(true);
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
                            String source = current.getSource();
                            if (source.endsWith(".csv")) {
                                PieChart pieChart = viewUtils.buildPieChart_CSV(current.getSource());
                                pieChart.setPrefHeight(userBlock.getPrefHeight());
                                pieChart.setPrefWidth(userBlock.getPrefWidth());
                                userBlock.getChildren().add(pieChart);
                            }else if (source.endsWith(".xml")){
                                PieChart pieChart = viewUtils.buildPieChart_XML(source);
                                pieChart.setPrefHeight(userBlock.getPrefHeight());
                                pieChart.setPrefWidth(userBlock.getPrefWidth());
                                userBlock.getChildren().add(pieChart);
                            }
                            break;
                        case BarChart:
                            String source2 = current.getSource();
                            if(getLastThree(source2).matches("csv")){
                                BarChart barChart = viewUtils.buildBarChart_CSV(source2);
                                barChart.setPrefHeight(userBlock.getPrefHeight());
                                barChart.setPrefWidth(userBlock.getPrefWidth());
                                userBlock.getChildren().add(barChart);
                            }else if(getLastThree(source2).matches("xml")){
                                BarChart barChart = viewUtils.buildBarChart_XML(source2);
                                barChart.setPrefHeight(userBlock.getPrefHeight());
                                barChart.setPrefWidth(userBlock.getPrefWidth());
                                userBlock.getChildren().add(barChart);
                            }
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
                            Button pdf = viewUtils.openPdf(current.getSource());


                            pdf.setLayoutX(((current.getEndX()-current.getStartX())-pdf.getWidth())/2.0);
                            pdf.setLayoutY((userBlock.getPrefHeight()/2)-(pdf.getHeight()/2));
                            
                            userBlock.getChildren().add(pdf);
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

    private String getLastThree(String myString) {
        if(myString.length() > 3)
            return myString.substring(myString.length()-3);
        else
            return myString;
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
        paneEdit.setVisible(false);
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
            cvc.setClickedUser(selectedUser, paneUserView);
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
        if(selectedUserBlock != null){
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
