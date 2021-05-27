package dk.vores.gui.userView;

import dk.vores.BLL.DataManager;
import dk.vores.BLL.UserViewManager;
import dk.vores.be.User;
import dk.vores.be.UserView;
import dk.vores.util.ViewUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserViewController implements Initializable {

    private UserViewManager uvm;
    private DataManager dm;
    private ViewUtils vu;

    private User loggedUser;

    private double userViewWidth = 0;
    private double userViewHeight = 0;
    private UserViewManager uvMan = new UserViewManager();
    private ViewUtils viewUtils = new ViewUtils();

    @FXML private AnchorPane paneUserView;

    private final int MILISECMULTIPLIER = 1000;

    
    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
        initViews();
    }

    private void initViews() {
        drawUserView_dataMode();
    }

    public void drawUserView_dataMode() {
        ObservableList<UserView> usersViews = uvMan.loadViewsFromUserID(loggedUser.getId());
        double height = paneUserView.getHeight();
        double width = paneUserView.getWidth();
        int updateTime = 600_000;
        for (UserView current : usersViews) {

            if (height < current.getEndY()) {
                height = current.getEndY();
            }
            if (width < current.getEndX()) {
                width = current.getEndX();
            }
            paneUserView.setMinHeight(height);
            paneUserView.setMinWidth(width);

            if(current.getUpdateTime() < (updateTime / MILISECMULTIPLIER)){
                updateTime = current.getUpdateTime() * MILISECMULTIPLIER;
            }
        }
        UpdateUserView updater = new UpdateUserView(paneUserView,loggedUser,updateTime);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(updater);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.uvm = new UserViewManager();
        this.dm = new DataManager();
        this.vu = new ViewUtils();
    }

    public void updateView(ActionEvent actionEvent) {
        drawUserView_dataMode();
    }
}
