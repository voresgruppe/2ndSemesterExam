package dk.vores.gui.adminView;

import dk.vores.BLL.UserManager;
import dk.vores.BLL.UserViewManager;
import dk.vores.be.DataType;
import dk.vores.be.User;
import dk.vores.be.UserView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class AdminViewController implements Initializable {
    @FXML
    private AnchorPane mainPane;
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
        administratorsListener();

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
                Rectangle userBlock = createDraggableRectangle(current.getStartX(), current.getStartY(), current.getEndX() - current.getStartX(), current.getEndY() - current.getStartY());
                userBlock.setStroke(Color.BLACK);


                //midlertidig for at illustrere typer
                userBlock.setFill(Color.WHITE);
                if (current.getType().equals(DataType.BarChart)) {
                    userBlock.setFill(Color.RED);
                }
                paneUserView.getChildren().add(userBlock);


            }
            paneUserView.setMinHeight(height);
            paneUserView.setMinWidth(width);
        }
    }


        private Rectangle createDraggableRectangle ( double x, double y, double width, double height){
            final double handleRadius = 10;

            Rectangle rect = new Rectangle(x, y, width, height);

            // top left resize handle:
            Circle resizeHandleNW = new Circle(handleRadius, Color.GOLD);
            // bind to top left corner of Rectangle:
            resizeHandleNW.centerXProperty().bind(rect.xProperty());
            resizeHandleNW.centerYProperty().bind(rect.yProperty());

            // bottom right resize handle:
            Circle resizeHandleSE = new Circle(handleRadius, Color.GOLD);
            // bind to bottom right corner of Rectangle:
            resizeHandleSE.centerXProperty().bind(rect.xProperty().add(rect.widthProperty()));
            resizeHandleSE.centerYProperty().bind(rect.yProperty().add(rect.heightProperty()));

            // move handle:
            Circle moveHandle = new Circle(handleRadius, Color.GOLD);
            // bind to bottom center of Rectangle:
            moveHandle.centerXProperty().bind(rect.xProperty().add(rect.widthProperty().divide(2)));
            moveHandle.centerYProperty().bind(rect.yProperty().add(rect.heightProperty()));

            // force circles to live in same parent as rectangle:
            rect.parentProperty().addListener((obs, oldParent, newParent) -> {
                for (Circle c : Arrays.asList(resizeHandleNW, resizeHandleSE, moveHandle)) {
                    Pane currentParent = (Pane) c.getParent();
                    if (currentParent != null) {
                        currentParent.getChildren().remove(c);
                    }
                    ((Pane) newParent).getChildren().add(c);
                }
            });

            Wrapper<Point2D> mouseLocation = new Wrapper<>();

            setUpDragging(resizeHandleNW, mouseLocation);
            setUpDragging(resizeHandleSE, mouseLocation);
            setUpDragging(moveHandle, mouseLocation);

            resizeHandleNW.setOnMouseDragged(event -> {
                if (mouseLocation.value != null) {
                    double deltaX = event.getSceneX() - mouseLocation.value.getX();
                    double deltaY = event.getSceneY() - mouseLocation.value.getY();
                    double newX = rect.getX() + deltaX;
                    if (newX >= handleRadius
                            && newX <= rect.getX() + rect.getWidth() - handleRadius) {
                        rect.setX(newX);
                        rect.setWidth(rect.getWidth() - deltaX);
                    }
                    double newY = rect.getY() + deltaY;
                    if (newY >= handleRadius
                            && newY <= rect.getY() + rect.getHeight() - handleRadius) {
                        rect.setY(newY);
                        rect.setHeight(rect.getHeight() - deltaY);
                    }
                    mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
                }
            });

            resizeHandleSE.setOnMouseDragged(event -> {
                if (mouseLocation.value != null) {
                    double deltaX = event.getSceneX() - mouseLocation.value.getX();
                    double deltaY = event.getSceneY() - mouseLocation.value.getY();
                    double newMaxX = rect.getX() + rect.getWidth() + deltaX;
                    if (newMaxX >= rect.getX()
                            && newMaxX <= rect.getParent().getBoundsInLocal().getWidth() - handleRadius) {
                        rect.setWidth(rect.getWidth() + deltaX);
                    }
                    double newMaxY = rect.getY() + rect.getHeight() + deltaY;
                    if (newMaxY >= rect.getY()
                            && newMaxY <= rect.getParent().getBoundsInLocal().getHeight() - handleRadius) {
                        rect.setHeight(rect.getHeight() + deltaY);
                    }
                    mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
                }
            });

            moveHandle.setOnMouseDragged(event -> {
                if (mouseLocation.value != null) {
                    double deltaX = event.getSceneX() - mouseLocation.value.getX();
                    double deltaY = event.getSceneY() - mouseLocation.value.getY();
                    double newX = rect.getX() + deltaX;
                    double newMaxX = newX + rect.getWidth();
                    if (newX >= handleRadius
                            && newMaxX <= rect.getParent().getBoundsInLocal().getWidth() - handleRadius) {
                        rect.setX(newX);
                    }
                    double newY = rect.getY() + deltaY;
                    double newMaxY = newY + rect.getHeight();
                    if (newY >= handleRadius
                            && newMaxY <= rect.getParent().getBoundsInLocal().getHeight() - handleRadius) {
                        rect.setY(newY);
                    }
                    mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
                }

            });

            return rect;
        }


        private void setUpDragging(Circle circle, Wrapper<Point2D> mouseLocation) {

            circle.setOnDragDetected(event -> {
                circle.getParent().setCursor(Cursor.CLOSED_HAND);
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            });

            circle.setOnMouseReleased(event -> {
                circle.getParent().setCursor(Cursor.DEFAULT);
                mouseLocation.value = null ;
            });
        }






    static class Wrapper<T> { T value ; }

    private void administratorsListener() {
        tblUsers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectedUser = newValue);

        tblUsers.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
                openChangeView(selectedUser);
            }
        });

        if(selectedUser != null){
            drawUserView();
        }
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
