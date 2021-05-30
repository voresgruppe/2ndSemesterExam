package dk.vores.util;

import dk.vores.BLL.UserViewManager;
import dk.vores.be.DataType;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class DataUtils {



    /**
     * This method converts strings to datatypes
     * @param str the string to convert
     * @param uvID the id of the userView
     * @return Datatype from the string
     */
    public DataType typeFromString(String str, int uvID){
        for(DataType current: DataType.values()) {
            if(str.toLowerCase().matches(current.name().toLowerCase())){
                return current;
            }
        }
        return datatypeError(str, uvID);

    }

    /**
     * this solves datatypeErrors by displaying an error message to the user and gives options to choose a valid datatype
     * @param str the string that caused the error
     * @param uvID the id of the userView
     * @return the chosen Datatype
     */
    public DataType datatypeError(String str, int uvID){
        Stage stage = new Stage();
        stage.setTitle("Datatype Error");

        TilePane tilePane = new TilePane();

        Label explanation = new Label("invalid Datatype found: " + str + "\n" + "please select correct datatype from the list.");

        ComboBox<DataType> dataTypeMenu = new ComboBox<>(FXCollections.observableArrayList(DataType.values()));
        EventHandler<ActionEvent> event = e -> {
            if (dataTypeMenu.getValue() != null) {
                UserViewManager uvMan = new UserViewManager();
                uvMan.updateTypeFromID(uvID, dataTypeMenu.getValue().name());
                stage.close();
            }
        };

        Button save = new Button("Confirm");
        save.setOnAction(event);

        tilePane.getChildren().add(explanation);
        explanation.autosize();
        tilePane.getChildren().add(dataTypeMenu);
        tilePane.getChildren().add(save);

        Scene scene = new Scene(tilePane, 250, 200);

        stage.setScene(scene);
        stage.showAndWait();
        return dataTypeMenu.getValue();
    }




}
