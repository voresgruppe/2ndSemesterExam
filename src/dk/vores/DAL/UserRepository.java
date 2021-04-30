package dk.vores.DAL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.vores.DAL.db.DatabaseConnector;
import dk.vores.be.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class UserRepository {

    DatabaseConnector db;
    private Connection connection;

    public UserRepository(){
        this.db = DatabaseConnector.getInstance();

        try{
            connection = db.getConnection();
        }catch(SQLServerException e){
            e.printStackTrace();
        }
    }

    public User login1(String username, String password){
        try {
            String query = "SELECT [id], [password], [isAdmin] FROM [User] AS u WHERE username=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                String pass = rs.getString("password");
                if(pass.equals(password)) {
                    User returnUser = new User(rs.getInt("id"), username, pass, rs.getBoolean("isAdmin"));
                    return returnUser;
                }
            }
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public int login(String username, String password){
        try(Connection connection = db.getConnection()){
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

    public ObservableList<User> loadUsers(){
        try(Connection connection = db.getConnection()){
            ObservableList<User> users = FXCollections.observableArrayList();
            String query = "SELECT * FROM [User] ORDER BY [id]";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                boolean isAdmin = (resultSet.getInt("isAdmin") == 1);
                User u = new User(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),isAdmin);
                users.add(u);
            }
            return users;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public ObservableList<User> loadUsersWithoutAdmins(){
        try(Connection connection = db.getConnection()){
            ObservableList<User> users = FXCollections.observableArrayList();
            String query = "SELECT * FROM [User] ORDER BY [id]";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                boolean isAdmin = (resultSet.getInt("isAdmin") == 1);
                User u = new User(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),isAdmin);
                if(!u.isAdmin()) {
                    users.add(u);
                }
            }
            return users;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }
}
