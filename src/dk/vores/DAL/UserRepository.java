package dk.vores.DAL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.vores.DAL.db.DatabaseConnector;
import dk.vores.be.User;
import dk.vores.util.UserError;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class UserRepository {
    UserError userError = UserError.getInstance();
    DatabaseConnector db;

    public UserRepository(){
        this.db = DatabaseConnector.getInstance();
    }

    public User login(String username, String password){
        try (Connection connection = db.getConnection()) {
            if(connection !=null) {
                String query = "SELECT [id], [password], [isAdmin] FROM [User] AS u WHERE username=?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    String pass = rs.getString("password");
                    if (pass.equals(password)) {
                        User returnUser = new User(rs.getInt("id"), username, pass, rs.getBoolean("isAdmin"));
                        return returnUser;
                    }
                }
            }
            else {
                userError.databaseError("the connection to the database failed", "please check that your connection and your database settings and try again");
            }
            return null;
        }catch(Exception e){
            userError.databaseError("an error occurred while trying to login", e.getMessage());
            return null;
        }
    }

    public ObservableList<User> loadUsers(){

        try(Connection connection = db.getConnection()){
            if(connection !=null) {
                ObservableList<User> users = FXCollections.observableArrayList();
                String query = "SELECT * FROM [User] ORDER BY [id]";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    boolean isAdmin = (resultSet.getInt("isAdmin") == 1);
                    User u = new User(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("password"), isAdmin);
                    users.add(u);
                }
               return users;
            }
            else { userError.databaseError("the connection to the database failed", "please check that your connection and your database settings and try again"); }
            return null;
        } catch (SQLException e) {
            userError.databaseError("an error occurred while trying to load the users", e.getMessage());
            return null;
        }
    }

    public ObservableList<User> loadUsersWithoutAdmins(){
        try(Connection connection = db.getConnection()){
            if(connection !=null) {
                ObservableList<User> users = FXCollections.observableArrayList();
                String query = "SELECT * FROM [User] ORDER BY [id]";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    boolean isAdmin = (resultSet.getInt("isAdmin") == 1);
                    User u = new User(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("password"), isAdmin);
                    if (!u.isAdmin()) {
                        users.add(u);
                    }
                }
                return users;
            }
            else { userError.databaseError("the connection to the database failed", "please check that your connection and your database settings and try again"); }
            return null;
        } catch (SQLException e) {
            userError.databaseError("an error occurred while trying to to load users without admins", e.getMessage());
            return null;
        }
    }

    public int addUser(User user){
        int returnId = -1;
        try(Connection connection = db.getConnection()){
            if(connection !=null) {
                String query = "INSERT INTO [User] ([username], [password], [isAdmin]) VALUES (?, ?, ?);";
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setBoolean(3, user.isAdmin());
                preparedStatement.executeUpdate();
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    returnId = generatedKeys.getInt(1);
                }
            }
            else { userError.databaseError("the connection to the database failed", "please check that your connection and your database settings and try again"); }

        } catch (SQLException e) {
            userError.databaseError("an error occurred while trying to add the user", e.getMessage());        }
        return returnId;
    }

    public void delete(User user) {
        removeViews(user);
        try (Connection connection = db.getConnection()){
            if(connection !=null) {
                int id = user.getId();
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM [User] WHERE [id] = ?");
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            }
            else { userError.databaseError("the connection to the database failed", "please check that your connection and your database settings and try again"); }

        } catch (SQLException e) {
            userError.databaseError("an error occurred while trying to delete a user", e.getMessage());}
    }

    private void removeViews(User user){
        try (Connection connection = db.getConnection()){
            if(connection !=null) {
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM [UserView] WHERE [userID] = ?");
                preparedStatement.setInt(1, user.getId());
                preparedStatement.executeUpdate();
            }
             else { userError.databaseError("the connection to the database failed", "please check that your connection and your database settings and try again"); }

        } catch (SQLException e) {
            userError.databaseError("an error occurred while trying to remove userview blocks", e.getMessage());}
    }



}
