package dk.vores.gui.userView;

import dk.vores.BLL.UserViewManager;
import dk.vores.be.User;
import dk.vores.be.UserView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class UserViewController implements Initializable {

    private ObservableList<UserView> views;

    private UserViewManager uvm;

    @FXML private AnchorPane root;

    private User loggedUser;

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
            System.out.println(uv.getType().toString());
            setView(uv);
        }
    }

    private void setView(UserView uv) {

        double sizeX = uv.getEndX() - uv.getStartX();
        double sizeY = uv.getEndY() - uv.getStartY();
        double posX = uv.getStartX();
        double posY = uv.getStartY();

        Node element = null;

        switch (uv.getType()) {
            case PDF:
                break;
            case HTML:
                WebView webView = new WebView();
                WebEngine we = webView.getEngine();
                we.load(getClass().getResource("/mockData/webpage.html").toString());
                webView.setPrefSize(sizeX, sizeY);
                webView.setTranslateX(posX);
                webView.setTranslateY(posY);
                root.getChildren().add(webView);
                break;
            case Table:
                break;
            case BarChart:
                break;
            case PieChart:
                break;
            case Undetermined:
            default:
                Pane p = new Pane();
                p.setPrefSize(sizeX, sizeY);
                p.setTranslateX(posX);
                p.setTranslateY(posY);
                root.getChildren().add(p);
                break;
        }
    }

    private void loadViews() {
        views = uvm.loadViewsFromUserID(loggedUser.getId());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.uvm = new UserViewManager();
    }
}
