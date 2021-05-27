package dk.vores.gui.userView;

import dk.vores.BLL.UserViewManager;
import dk.vores.be.DataType;
import dk.vores.be.User;
import dk.vores.be.UserView;
import dk.vores.util.ViewUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class UpdateUserView extends Task<AnchorPane> {
    private ViewUtils viewUtils = new ViewUtils();
    private UserViewManager uvMan = new UserViewManager();

    private int updateTime;
    private AnchorPane mainPane;
    private User loggedUser;

    @Override
    protected AnchorPane call() throws Exception {
        while(true){
            mainPane.getChildren().removeAll();
            createPanes();
            Thread.sleep(updateTime);
        }
    }

    public UpdateUserView(AnchorPane mainPane, User loggedUser, int updateTime) {
        this.mainPane = mainPane;
        this.loggedUser = loggedUser;
        this.updateTime = updateTime;
    }

    public void setMainPane(AnchorPane mainPane) {
        this.mainPane = mainPane;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }

    private void createPanes(){
        ObservableList<UserView> usersViews = uvMan.loadViewsFromUserID(loggedUser.getId());
        if (!usersViews.isEmpty()) {
            for (UserView current : usersViews) {

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
                            Platform.runLater(()->{
                                WebView webView = viewUtils.showWeb("https://" + current.getSource());
                                webView.setPrefHeight(userBlock.getPrefHeight());
                                webView.setPrefWidth(userBlock.getPrefWidth());
                                userBlock.getChildren().add(webView);
                                addListener(userBlock,current.getSource());
                            });
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
                            Label label = new Label("press the button to open the pdf");
                            label.setLayoutX(15);
                            label.setLayoutY(15);

                            pdf.setLayoutX(((current.getEndX()-current.getStartX())-pdf.getWidth())/2.0);
                            pdf.setLayoutY((userBlock.getPrefHeight()/2.0)-(pdf.getHeight()/2.0));

                            userBlock.getChildren().add(label);
                            userBlock.getChildren().add(pdf);
                            break;
                        default:
                            break;
                    }
                }
                userBlock.setId(String.valueOf(current.getId()));
                addListener(userBlock, currentSource);

                Platform.runLater(()->{
                    mainPane.getChildren().add(userBlock);
                });
                updateValue(mainPane);
            }
        }
    }

    private void addListener(AnchorPane root, String source){
        ObservableList views = root.getChildren();
        for(Object view : views){
            if(view instanceof BarChart){
                BarChart barChart = (BarChart) view;
                barChart.setOnMouseClicked(mouseEvent -> {
                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                        if(mouseEvent.getClickCount() == 2){
                            BarChart chart;
                            if(source.endsWith("xml")){
                                chart = viewUtils.buildBarChart_XML(source);
                            }else chart = viewUtils.buildBarChart_CSV(source);

                            AnchorPane anch = new AnchorPane();
                            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
                            chart.setPrefWidth(screenBounds.getWidth());
                            chart.setPrefHeight(screenBounds.getHeight() - 75);
                            anch.getChildren().add(chart);

                            Scene secondScene = new Scene(anch);

                            Stage stage = new Stage();
                            stage.setTitle("Fullscreen bar chart");
                            stage.setScene(secondScene);
                            stage.setMaximized(true);
                            stage.show();
                        }
                    }
                });
            }else if(view instanceof PieChart){
                PieChart pieChart = (PieChart) view;
                pieChart.setOnMouseClicked(mouseEvent -> {
                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                        if(mouseEvent.getClickCount() == 2){
                            PieChart chart;
                            if(source.endsWith("xml")) chart = viewUtils.buildPieChart_XML(source);
                            else chart = viewUtils.buildPieChart_CSV(source);

                            AnchorPane anch = new AnchorPane();
                            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
                            chart.setPrefWidth(screenBounds.getWidth());
                            chart.setPrefHeight(screenBounds.getHeight() - 75);
                            anch.getChildren().add(chart);

                            Scene secondScene = new Scene(anch);

                            Stage stage = new Stage();
                            stage.setTitle("Fullscreen pie chart");
                            stage.setScene(secondScene);
                            stage.setMaximized(true);
                            stage.show();
                        }
                    }
                });
            }else if(view instanceof WebView){
                WebView webView = (WebView) view;
                webView.setOnMouseClicked(mouseEvent -> {
                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                        if(mouseEvent.getClickCount() == 2){
                            WebView web = viewUtils.showWeb("https://" + source);

                            AnchorPane anch = new AnchorPane();
                            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
                            web.setPrefWidth(screenBounds.getWidth());
                            web.setPrefHeight(screenBounds.getHeight() - 75);
                            anch.getChildren().add(web);

                            Scene secondScene = new Scene(anch);

                            Stage stage = new Stage();
                            stage.setTitle("Fullscreen web");
                            stage.setScene(secondScene);
                            stage.setMaximized(true);
                            stage.show();
                        }
                    }
                });
            }else if(view instanceof TableView){
                TableView tableView = (TableView) view;
                tableView.setOnMouseClicked(mouseEvent -> {
                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                        if(mouseEvent.getClickCount() == 2){
                            TableView tbl;
                            if(source.endsWith(".xlsx")) tbl = viewUtils.showExcel(source);
                            else tbl = viewUtils.buildTableView_CSV(source);

                            AnchorPane anch = new AnchorPane();
                            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
                            tbl.setPrefWidth(screenBounds.getWidth());
                            tbl.setPrefHeight(screenBounds.getHeight() - 75);
                            anch.getChildren().add(tbl);

                            Scene secondScene = new Scene(anch);

                            Stage stage = new Stage();
                            stage.setTitle("Fullscreen table");
                            stage.setScene(secondScene);
                            stage.setMaximized(true);
                            stage.show();
                        }
                    }
                });
            }
        }
    }

}
