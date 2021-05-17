package dk.vores.util;

import dk.vores.BLL.UserViewManager;
import dk.vores.be.DataType;
import dk.vores.be.UserView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class DataUtils {



    public DataType typeFromString(String str, int uvID){
        for(DataType current: DataType.values()) {
            if(str.toLowerCase().matches(current.name().toLowerCase())){
                return current;
            }
        }
        datatypeError(str, uvID);
        return null;

    }

    /*
     users have to deal with Datatype errors
     */
    public void datatypeError(String str, int uvID){
        Stage stage = new Stage();
        stage.setTitle("Datatype Error");

        TilePane tilePane = new TilePane();

        Label explanation = new Label("invalid Datatype found: " + str + "\n" + "please select correct datatype from the list." + "\n" + "After Confirming please restart the program");

        ComboBox<DataType> dataTypeMenu = new ComboBox<>(FXCollections.observableArrayList(DataType.values()));
        EventHandler<ActionEvent> event = e -> {
            if (dataTypeMenu.getValue() != null) {
                UserViewManager uvMan = new UserViewManager();
                uvMan.updateTypeFromID(uvID, dataTypeMenu.getValue().name());
            }
        };

        Button save = new Button("Confirm");
        save.setOnAction(event);

        tilePane.getChildren().add(explanation);
        tilePane.getChildren().add(dataTypeMenu);
        tilePane.getChildren().add(save);

        Scene scene = new Scene(tilePane, 200, 200);

        stage.setScene(scene);
        stage.showAndWait();
    }


}
