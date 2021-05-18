package dk.vores.util;

import com.gembox.spreadsheet.*;
import dk.vores.BLL.DataManager;
import dk.vores.BLL.UserViewManager;
import dk.vores.be.DataExample;
import dk.vores.be.DataType;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ViewUtils {
    UserViewManager uvMan = new UserViewManager();

    public Rectangle createDraggableRectangle (int uvID, double x, double y, double width, double height){
        final double handleRadius = 7;

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
            if (newParent != null) {
                for (Circle c : Arrays.asList(resizeHandleNW, resizeHandleSE, moveHandle)) {
                    Pane currentParent = (Pane)c.getParent();
                    if (currentParent != null) {
                        currentParent.getChildren().remove(c);
                    }
                    ((Pane)newParent).getChildren().add(c);
                }
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
        resizeHandleNW.setOnMouseReleased(event -> {
                uvMan.updatePositionFromID(uvID, (int) rect.getX(), (int) rect.getY(), (int) (rect.getX()+ rect.getWidth()), (int) (rect.getY()+rect.getHeight()));
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
        resizeHandleSE.setOnMouseReleased(event -> {
            uvMan.updatePositionFromID(uvID, (int) rect.getX(), (int) rect.getY(), (int) (rect.getX()+ rect.getWidth()), (int) (rect.getY()+rect.getHeight()));
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

        moveHandle.setOnMouseReleased(event -> {
                uvMan.updatePositionFromID(uvID, (int) rect.getX(), (int) rect.getY(), (int) (rect.getX()+ rect.getWidth()), (int) (rect.getY()+rect.getHeight()));
        });

        return rect;
    }

    public AnchorPane createAnchorPane(int uvID, double x, double y, double width, double height){
        AnchorPane pane = new AnchorPane();
        pane.setLayoutX(x);
        pane.setLayoutY(y);
        pane.setPrefWidth(width);
        pane.setPrefHeight(height);
        return pane;
    }


    public AnchorPane createDraggableAnchorPane (int uvID, double x, double y, double width, double height){
        final double handleRadius = 7;

        AnchorPane pane = new AnchorPane();
        pane.setLayoutX(x);
        pane.setLayoutY(y);
        pane.setPrefWidth(width);
        pane.setPrefHeight(height);

        // top left resize handle:
        Circle resizeHandleNW = new Circle(handleRadius, Color.GOLD);

        //TODO handleNW doesnt work
        resizeHandleNW.setVisible(false);

        // bind to top left corner of Rectangle:
        resizeHandleNW.centerXProperty().bind(pane.layoutXProperty());
        resizeHandleNW.centerYProperty().bind(pane.layoutYProperty());

        // bottom right resize handle:
        Circle resizeHandleSE = new Circle(handleRadius, Color.GOLD);
        // bind to bottom right corner of Rectangle:
        resizeHandleSE.centerXProperty().bind(pane.layoutXProperty().add(pane.widthProperty()));
        resizeHandleSE.centerYProperty().bind(pane.layoutYProperty().add(pane.heightProperty()));

        // move handle:
        Circle moveHandle = new Circle(handleRadius, Color.GOLD);
        // bind to bottom center of Rectangle:
        moveHandle.centerXProperty().bind(pane.layoutXProperty().add(pane.widthProperty().divide(2)));
        moveHandle.centerYProperty().bind(pane.layoutYProperty().add(pane.heightProperty()));

        // force circles to live in same parent as rectangle:
        pane.parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent != null) {
                for (Circle c : Arrays.asList(resizeHandleNW, resizeHandleSE, moveHandle)) {
                    Pane currentParent = (Pane)c.getParent();
                    if (currentParent != null) {
                        currentParent.getChildren().remove(c);
                    }
                    ((Pane)newParent).getChildren().add(c);
                }
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
                double newX = pane.getLayoutX() + deltaX;
                if (newX >= handleRadius
                        && newX <= pane.getLayoutX() + pane.getWidth() - handleRadius) {
                    pane.setLayoutX(newX);
                    pane.setPrefWidth(pane.getWidth() - deltaX);
                }
                double newY = pane.getLayoutY() + deltaY;
                if (newY >= handleRadius
                        && newY <= pane.getLayoutY()+ pane.getHeight() - handleRadius) {
                    pane.setLayoutY(newY);
                    pane.setPrefHeight(pane.getHeight() - deltaY);

                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });
        resizeHandleNW.setOnMouseReleased(event -> {
            updateBlockPosition(uvID, pane);
        });

        resizeHandleSE.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();
                double newMaxX = pane.getLayoutX() + pane.getWidth() + deltaX;
                if (newMaxX >= pane.getLayoutX()
                        && newMaxX <= pane.getParent().getBoundsInLocal().getWidth() - handleRadius) {
                    pane.setPrefWidth(pane.getWidth() + deltaX);
                }
                double newMaxY = pane.getLayoutY() + pane.getHeight() + deltaY;
                if (newMaxY >= pane.getLayoutY()
                        && newMaxY <= pane.getParent().getBoundsInLocal().getHeight() - handleRadius) {
                    pane.setPrefHeight(pane.getHeight() + deltaY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });
        resizeHandleSE.setOnMouseReleased(event -> {
            updateBlockPosition(uvID, pane);
        });


        moveHandle.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();
                double newX = pane.getLayoutX() + deltaX;
                double newMaxX = newX + pane.getWidth();
                if (newX >= handleRadius
                        && newMaxX <= pane.getParent().getBoundsInLocal().getWidth() - handleRadius) {
                    pane.setLayoutX(newX);
                }
                double newY = pane.getLayoutY() + deltaY;
                double newMaxY = newY + pane.getHeight();
                if (newY >= handleRadius
                        && newMaxY <= pane.getParent().getBoundsInLocal().getHeight() - handleRadius) {
                    pane.setLayoutY(newY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }

        });

        moveHandle.setOnMouseReleased(event -> {
            updateBlockPosition(uvID, pane);
        });

        return pane;
    }

    private void updateBlockPosition(int uvID, AnchorPane pane){
        uvMan.updatePositionFromID(uvID, (int) pane.getLayoutX(), (int) pane.getLayoutY(), (int) (pane.getLayoutX()+ pane.getWidth()), (int) (pane.getLayoutY()+pane.getHeight()));

        //todo skal måske fjernes
        for(Node child: pane.getChildren()){
            child.autosize();
        }
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



    public BarChart buildBarChart_DataExample(String source) {
        DataManager dMan = new DataManager();
        List<DataExample> dataExamples = dMan.getAllData(source);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Product");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("quantity Sold");

        BarChart barChart = new BarChart(xAxis, yAxis);

        XYChart.Series data = new XYChart.Series();
        data.setName("Products Sold");

        //provide data
        for(DataExample dataExample: dataExamples){
            data.getData().add(new XYChart.Data(dataExample.getDate().toString(),dataExample.getUnitsSold()));
        }

        barChart.getData().add(data);
        barChart.setLegendVisible(false);

        return barChart;
    }


    public BarChart buildBarChart_CSV(String source) {

        File file = new File(source);
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        XYChart.Series<String, Integer> data = new XYChart.Series<>();


        try (LineNumberReader rdr = new LineNumberReader(new FileReader(file))) {
            for (String line; (line = rdr.readLine()) != null;) {
                if (rdr.getLineNumber() == 1) {
                    String[] titles = line.split(",");
                    xAxis.setLabel(titles[0]);
                    yAxis.setLabel(titles[1]);
                }
                else{
                    String[] lineData = line.split(",");
                    data.getData().add(new XYChart.Data<>(lineData[0], Integer.parseInt(lineData[1])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        BarChart barChart = new BarChart(xAxis, yAxis);

        barChart.getData().add(data);
        barChart.setLegendVisible(false);

        return barChart;
    }

    public PieChart buildPieChart_CSV(String source) {
        File file = new File(source);


        PieChart pieChart = new PieChart();

        try (LineNumberReader rdr = new LineNumberReader(new FileReader(file))) {
            for (String line; (line = rdr.readLine()) != null;) {
                if (rdr.getLineNumber() == 1) {
                    String[] titles = line.split(",");
                    pieChart.setTitle(titles[1]);
                }
                else{
                    String[] lineData = line.split(",");
                    pieChart.getData().add(new PieChart.Data(lineData[0],Double.parseDouble(lineData[1])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        pieChart.setClockwise(true);
        pieChart.setLabelLineLength(50);
        pieChart.setLabelsVisible(true);
        pieChart.setStartAngle(180);

        return pieChart;
    }
    public TableView buildTableView_CSV(String source) {
        TableView<List<String>> table = new TableView<>();

        File file = new File(source);
        String[] titles = new String[1];
        try (LineNumberReader rdr = new LineNumberReader(new FileReader(file))) {
            for (String line; (line = rdr.readLine()) != null && rdr.getLineNumber() >=1;) {
                if (rdr.getLineNumber() == 1) {
                    titles = line.split(",");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String[] finalTitles = titles;
            Files.lines(Path.of(source)).map(line -> line.split(",")).forEach(values -> {
                // Add extra columns if necessary:
                for (int i = table.getColumns().size(); i < values.length; i++) {
                    TableColumn<List<String>, String> col = new TableColumn<>(finalTitles[i]);
                    col.setMinWidth(80);
                    final int colIndex = i ;
                    col.setCellValueFactory(data -> {
                        List<String> rowValues = data.getValue();
                        String cellValue ;
                        if (colIndex < rowValues.size()) {
                            cellValue = rowValues.get(colIndex);
                        } else {
                            cellValue = "" ;
                        }
                        return new ReadOnlyStringWrapper(cellValue);
                    });
                    table.getColumns().add(col);
                }

                // add row:
                table.getItems().add(Arrays.asList(values));
            });

            table.getItems().remove(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table ;
    }




    public String matchDatatypeToColor(DataType dataType){
        return switch (dataType) {
            case Undetermined -> "white";
            case HTML -> "green";
            case Table -> "pink";
            case BarChart -> "red";
            case PieChart -> "yellow";
            case PDF -> "cyan";
        };
    }

    public WebView showWeb(String url) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        engine.load(url);
        return webView;
    }

    public TableView showExcel(String source){

        TableView table = new TableView();

        File file = new File(source);
        try {
            ExcelFile workbook  = ExcelFile.load(file.getAbsolutePath());
            ExcelWorksheet worksheet = workbook.getWorksheet(0);
            String[][] sourceData = new String[100][26];
            for (int row = 0; row < sourceData.length; row++) {
                for (int column = 0; column < sourceData[row].length; column++) {
                    ExcelCell cell = worksheet.getCell(row, column);
                    if (cell.getValueType() != CellValueType.NULL)
                        sourceData[row][column] = cell.getValue().toString();
                }
            }
            table.getColumns().clear();

            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            for (String[] row : sourceData)
                data.add(FXCollections.observableArrayList(row));
            table.setItems(data);

            for (int i = 0; i < sourceData[0].length; i++) {
                final int currentColumn = i;
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(ExcelColumnCollection.columnIndexToName(currentColumn));
                column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(currentColumn)));
                column.setEditable(true);
                column.setCellFactory(TextFieldTableCell.forTableColumn());
                column.setOnEditCommit(
                        (TableColumn.CellEditEvent<ObservableList<String>, String> t) -> {
                            t.getTableView().getItems().get(t.getTablePosition().getRow()).set(t.getTablePosition().getColumn(), t.getNewValue());
                        });
                table.getColumns().add(column);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }




}
