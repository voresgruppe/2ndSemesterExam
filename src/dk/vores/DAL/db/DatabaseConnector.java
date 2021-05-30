package dk.vores.DAL.db;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.vores.be.User;
import dk.vores.util.UserError;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Logger;

public class DatabaseConnector {
    final private UserError userError = UserError.getInstance();
    private static DatabaseConnector Instance = null;

    private static final String PROP_FILE = "data/database.settings";
    SQLServerDataSource dataSource;

    public static DatabaseConnector getInstance(){
        if(Instance == null)
            Instance = new DatabaseConnector();

        return Instance;
    }

    private DatabaseConnector() {
        Properties databaseProperties = new Properties();
        File file = new File(PROP_FILE);
        if(!file.exists()){
            userError.fileNotFound("the 'database.settings' file is missing", "please place the file in the 'data' directory containing the correct database settings");
            userError.setConnectionError(true);
        }
        else if(!userError.isConnectionError()) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                databaseProperties.load(fileInputStream);
            } catch (IOException e) {
                userError.fileNotFound(e.getCause().toString(), e.getMessage());
            }
                String server = databaseProperties.getProperty("Server");
                String database = databaseProperties.getProperty("Database");
                String user = databaseProperties.getProperty("User");
                String password = databaseProperties.getProperty("Password");

                dataSource = new SQLServerDataSource();
                dataSource.setServerName(server);
                dataSource.setDatabaseName(database);
                dataSource.setUser(user);
                dataSource.setPassword(password);
            }

    }

    public Connection getConnection(){
        if(dataSource !=null) {
            try {
                return dataSource.getConnection();
            } catch (SQLServerException e) {
                userError.databaseError("a SQLSeverException occurred while trying to establish connection", "please check that your connection and your database settings and try again");
                return null;
            }
        }
        else {
            userError.fileNotFound("the dataSource is null", "please check your 'database.settings' file and try again");
            return null;
        }
    }
}
