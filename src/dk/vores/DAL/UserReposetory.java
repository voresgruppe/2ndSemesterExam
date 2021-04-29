package dk.vores.DAL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.vores.DAL.db.DatabaseConnector;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserReposetory {

    DatabaseConnector db;
    private Connection connection;

    public UserReposetory(){
        this.db = DatabaseConnector.getInstance();

        try{
            connection = db.getConnection();
        }catch(SQLServerException e){
            e.printStackTrace();
        }
    }

    public int login(String username, String password){
        try {

            String query = "SELECT [id], [password] FROM [User] AS u WHERE username=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()){
                String pass = rs.getString("password");
                if(pass.equals(password))
                    return rs.getInt("id");
            }
            return -1;
        }catch(Exception e){
            e.printStackTrace();
        }
        return -1;
    }
}
