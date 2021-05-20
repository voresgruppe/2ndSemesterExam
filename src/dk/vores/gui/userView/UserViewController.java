package dk.vores.gui.userView;

import dk.vores.BLL.DataManager;
import dk.vores.BLL.UserViewManager;
import dk.vores.be.DataType;
import dk.vores.be.User;
import dk.vores.be.UserView;
import dk.vores.util.ViewUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class UserViewController implements Initializable {

    private ObservableList<UserView> views;

    private UserViewManager uvm;
    private DataManager dm;
    private ViewUtils vu;

    private User loggedUser;

    private double userViewWidth = 0;
    private double userViewHeight = 0;
    private UserViewManager uvMan = new UserViewManager();
    private ViewUtils viewUtils = new ViewUtils();

    @FXML private AnchorPane paneUserView;

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
        initViews();
    }

    private void initViews() {
        loadViews();
        /*
        for (UserView uv : views) {
            addView(uv);
        }

         */

        drawUserView_dataMode();
    }

    public void drawUserView_dataMode() {
        ObservableList<UserView> usersViews = uvMan.loadViewsFromUserID(loggedUser.getId());
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


                //userBlock.setStyle("-fx-background-color: #ffff; -fx-border-color: black; -fx-border-width: 2px 2px 2px 2px; ");
                userBlock.setStyle("-fx-background-color: #ffff;");

                DataType currentType = current.getType();
                //userBlock.setStyle("-fx-background-color: " + viewUtils.matchDatatypeToColor(currentType) + ";");
                String currentSource = current.getSource();


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
                            if(currentSource.endsWith(".csv")){
                                BarChart barChart = viewUtils.buildBarChart_CSV(source2);
                                barChart.setPrefHeight(userBlock.getPrefHeight());
                                barChart.setPrefWidth(userBlock.getPrefWidth());
                                userBlock.getChildren().add(barChart);
                            }else if(currentSource.endsWith(".xml")){
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

                            //TODO få knappen ind i midten
                            pdf.setLayoutX((userBlock.getPrefWidth()/2)-(pdf.getWidth()/2));
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

    /**
     * Adds a new UserView to the root-pane
     * @param uv the current userview to add to the root
     * @see UserView
     */
    private void addView(UserView uv) {

        //Create variables to store view information
        //Makes it easier to insert in code, instead of doing calculations on spot
        double sizeX = uv.getEndX() - uv.getStartX();
        double sizeY = uv.getEndY() - uv.getStartY();
        double posX = uv.getStartX();
        double posY = uv.getStartY();

        //Used to change to window-size depending on used space
        //Added buffers to both width and height by 5 and 15 respectively
        userViewWidth = (userViewWidth > uv.getEndX()) ? userViewWidth : uv.getEndX() + 5;
        userViewHeight = (userViewHeight > uv.getEndY()) ? userViewHeight : uv.getEndY() + 15;

        switch (uv.getType()) {
            case PDF:
                //TODO: Find out how to use PDF
                break;
            case HTML:
                //Create the webview
                WebView webView = new WebView();
                WebEngine we = webView.getEngine();

                //Load data into the webview and set size/pos
                we.load(getClass().getResource("/mockData/webpage.html").toString());
                webView.setPrefSize(sizeX, sizeY);
                webView.setTranslateX(posX);
                webView.setTranslateY(posY);

                //Add to root
                paneUserView.getChildren().add(webView);
                break;
            case Table:
                break;
            case BarChart:
                BarChart barChart = vu.buildBarChart_CSV(uv.getSource());
                barChart.setPrefHeight(sizeX);
                barChart.setPrefWidth(sizeY);
                barChart.setTranslateX(posX);
                barChart.setTranslateY(posY);
                paneUserView.getChildren().add(barChart);
                break;
            case PieChart:
                PieChart pieChart = vu.buildPieChart_CSV(uv.getSource());
                pieChart.setPrefHeight(sizeX);
                pieChart.setPrefWidth(sizeY);
                pieChart.setTranslateX(posX);
                pieChart.setTranslateY(posY);
                paneUserView.getChildren().add(pieChart);
                break;
            case Undetermined:
            default:
                //Create an empty pane
                Pane p = new Pane();

                //Set size and position. To make empty pane visible, make it completely white
                p.setPrefSize(sizeX, sizeY);
                p.setTranslateX(posX);
                p.setTranslateY(posY);
                p.setStyle("-fx-background-color: #ffffff;");

                //Add pane to root
                paneUserView.getChildren().add(p);
                break;
        }

        paneUserView.setPrefSize(userViewWidth, userViewHeight);
    }

    /**
     * Loads all the userviews into a variable
     */
    private void loadViews() {
        views = uvm.loadViewsFromUserID(loggedUser.getId());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.uvm = new UserViewManager();
        this.dm = new DataManager();
        this.vu = new ViewUtils();
    }
}
