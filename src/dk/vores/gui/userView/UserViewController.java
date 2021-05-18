package dk.vores.gui.userView;

import dk.vores.BLL.DataManager;
import dk.vores.BLL.UserViewManager;
import dk.vores.be.DataExample;
import dk.vores.be.User;
import dk.vores.be.UserView;
import dk.vores.util.ViewUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.nio.channels.NotYetBoundException;
import java.util.List;
import java.util.ResourceBundle;

public class UserViewController implements Initializable {

    private ObservableList<UserView> views;

    private UserViewManager uvm;
    private DataManager dm;
    private ViewUtils vu;

    private User loggedUser;

    private double userViewWidth = 0;
    private double userViewHeight = 0;

    @FXML private AnchorPane root;

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
        initViews();
    }

    private void initViews() {
        loadViews();

        for (UserView uv : views) {
            addView(uv);
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
                root.getChildren().add(webView);
                break;
            case Table:
                break;
            case BarChart:
                BarChart barChart = vu.buildBarChart_CSV(uv.getSource());
                barChart.setPrefHeight(sizeX);
                barChart.setPrefWidth(sizeY);
                barChart.setTranslateX(posX);
                barChart.setTranslateY(posY);
                root.getChildren().add(barChart);
                break;
            case PieChart:
                PieChart pieChart = vu.buildPieChart_CSV(uv.getSource());
                pieChart.setPrefHeight(sizeX);
                pieChart.setPrefWidth(sizeY);
                pieChart.setTranslateX(posX);
                pieChart.setTranslateY(posY);
                root.getChildren().add(pieChart);
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
                root.getChildren().add(p);
                break;
        }

        root.setPrefSize(userViewWidth, userViewHeight);
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
