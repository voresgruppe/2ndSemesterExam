package dk.vores.DAL.db;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

public class DatabaseConnector {

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
        try {
            databaseProperties.load(new FileInputStream(new File(PROP_FILE)));
        } catch (IOException e) {
            e.printStackTrace();
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

    public Connection getConnection() throws SQLServerException {
        return dataSource.getConnection();
    }
}
