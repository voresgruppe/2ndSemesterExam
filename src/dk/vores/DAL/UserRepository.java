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
/*
    public User getUserFromID(int id){
        try {
            String query = "SELECT [username], [password], [isAdmin] FROM [User] AS u WHERE [id]=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                User returnUser = new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getBoolean("isAdmin"));
                return returnUser;
            }
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

 */

    public User login(String username, String password){
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

    public int addUser(User user){
        int returnId = -1;
        try(Connection connection = db.getConnection()){
            String query = "INSERT INTO [User] ([username], [password], [isAdmin]) VALUES (?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setBoolean(3, user.isAdmin());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                returnId = generatedKeys.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return returnId;
    }

    public void delete(User user) {
        removeViews(user);
        try (Connection connect = db.getConnection()){
            int id = user.getId();
            PreparedStatement preparedStatement = connect.prepareStatement("DELETE FROM [User] WHERE [id] = ?");
            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void removeViews(User user){
        try (Connection connection = db.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM [UserView] WHERE [userID] = ?");
            preparedStatement.setInt(1,user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



}
