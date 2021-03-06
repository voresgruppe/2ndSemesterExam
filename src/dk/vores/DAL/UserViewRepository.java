package dk.vores.DAL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.vores.DAL.db.DatabaseConnector;
import dk.vores.be.User;
import dk.vores.be.UserView;
import dk.vores.util.DataUtils;
import dk.vores.util.UserError;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class UserViewRepository {
    private UserError userError = UserError.getInstance();
    DatabaseConnector db;
    private DataUtils dataUtils = DataUtils.getInstance();

    public UserViewRepository() {
        this.db = DatabaseConnector.getInstance();
    }

    public UserView loadViewFromID(int id){
        try(Connection connection = db.getConnection()){
            if(connection == null){
                userError.databaseError("the connection to the database failed", "please check that your connection and your database settings and try again");
                return null;
            }
            String query = "Select*from [UserView] WHERE [id] = '" + id +"'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            UserView uv = null;
            while(resultSet.next()) {
                uv = new UserView(resultSet.getInt("id"), resultSet.getInt("userID"), resultSet.getInt("startx"), resultSet.getInt("starty"), resultSet.getInt("endx"), resultSet.getInt("endy"), dataUtils.typeFromString(resultSet.getString("type"), resultSet.getInt("id")), resultSet.getString("source"), resultSet.getInt("updateTime"));
            }
            return uv;
        } catch (SQLException e) {
            userError.databaseError("an error occurred while trying to load userview blocks", e.getMessage());
        }
        return null;
        }


    public ObservableList<UserView> loadViewsFromUserID(int id){
        try(Connection connection = db.getConnection()){
            if(connection == null){
                userError.databaseError("the connection to the database failed", "please check that your connection and your database settings and try again");
                return null;
            }
            ObservableList<UserView> userViews = FXCollections.observableArrayList();
            String query = "Select * from [UserView] where [userID] ='"+id+"'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                boolean hasUserID = (resultSet.getInt("userID") == id);
                if(hasUserID) {
                    UserView uv = new UserView(resultSet.getInt("id"), resultSet.getInt("userID"), resultSet.getInt("startx"), resultSet.getInt("starty"), resultSet.getInt("endx"), resultSet.getInt("endy"), dataUtils.typeFromString(resultSet.getString("type"), resultSet.getInt("id")), resultSet.getString("source"), resultSet.getInt("updateTime"));
                    userViews.add(uv);
                }
            }
            return userViews;
        } catch (SQLException e) {
            userError.databaseError("an error occurred while trying to load userview blocks", e.getMessage());
            return null;
        }
    }

    public void addViewToUser(int userID, int startX, int startY, int endX, int endY, String type, String source, int updateTime){
        try(Connection connection = db.getConnection()){
            if(connection == null){
                userError.databaseError("the connection to the database failed", "please check that your connection and your database settings and try again");
                return;
            }
            String sql = "INSERT INTO [UserView] ([userID], [startX], [startY], [endX], [endY], [type], [source], [updateTime]) \n" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,userID);
            preparedStatement.setInt(2,startX);
            preparedStatement.setInt(3,startY);
            preparedStatement.setInt(4,endX);
            preparedStatement.setInt(5,endY);
            preparedStatement.setString(6,type);
            preparedStatement.setString(7,source);
            preparedStatement.setInt(8, updateTime);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            userError.databaseError("an error occurred while trying to add a userview block", e.getMessage());
        }
    }

    public void clearViewFromUser(int userId){
        try(Connection connection = db.getConnection()){
            if(connection == null){
                userError.databaseError("the connection to the database failed", "please check that your connection and your database settings and try again");
                return;
            }
            String sql = "DELETE FROM [UserView] WHERE [userID] = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            userError.databaseError("an error occurred while trying to clear userviews from a user", e.getMessage());
        }
    }

    public void updateTypeFromID(int uvID, String newType){
        try(Connection connection = db.getConnection()){
            if(connection == null){
                userError.databaseError("the connection to the database failed", "please check that your connection and your database settings and try again");
                return;
            }
            String sql = "UPDATE [UserView] \n" +
                    "SET [type] = ? \n" +
                    "WHERE [id] = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,newType);
            preparedStatement.setInt(2,uvID);
            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            userError.databaseError("an error occurred while trying to load userview blocks", e.getMessage());
        }
    }
    public void updateUpdateTimeFromID(int uvID, int newTime){
        try(Connection connection = db.getConnection()){
            if(connection == null){
                userError.databaseError("the connection to the database failed", "please check that your connection and your database settings and try again");
                return;
            }
            String sql = "UPDATE [UserView] \n" +
                    "SET [updateTime] = ? \n" +
                    "WHERE [id] = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,newTime);
            preparedStatement.setInt(2,uvID);
            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            userError.databaseError("an error occurred while trying to update time on a userview block", e.getMessage());
        }
    }

    public void updateSourceFromID(int uvID, String source){
        try(Connection connection = db.getConnection()){
            if(connection == null){
                userError.databaseError("the connection to the database failed", "please check that your connection and your database settings and try again");
                return;
            }
            String sql = "UPDATE [UserView] \n" +
                    "SET [source] = ? \n" +
                    "WHERE [id] = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,source);
            preparedStatement.setInt(2,uvID);
            preparedStatement.executeUpdate();
        }catch (SQLException throwables) {
            showDBError(throwables);
        }
    }

    public void removeViewFromUser(int id){
        try(Connection connection = db.getConnection()){
            if(connection == null){
                userError.databaseError("the connection to the database failed", "please check that your connection and your database settings and try again");
                return;
            }
            String sql = "DELETE FROM [UserView] WHERE [id] = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1,id);
            ps.executeUpdate();
        }catch (SQLException e) {
            userError.databaseError("an error occurred while trying to remowe an userview block from a user", e.getMessage());
        }
    }

    private void showDBError(SQLException throwables){
        String header = "Something went wrong..";
        UserError.showError(header, throwables.getMessage());
    }

    public void updateViewPlacement(int uvID, int newStartX, int newStartY, int newEndX, int newEndY){
        try(Connection connection = db.getConnection()){
            if(connection == null){
                userError.databaseError("the connection to the database failed", "please check that your connection and your database settings and try again");
                return;
            }
            String sql = "UPDATE [UserView] \n" +
                    "    SET [startX] = ?, [startY] = ?, [endX] = ?, [endY] = ? \n" +
                    "WHERE [id] = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,newStartX);
            preparedStatement.setInt(2,newStartY);
            preparedStatement.setInt(3,newEndX);
            preparedStatement.setInt(4,newEndY);
            preparedStatement.setInt(5,uvID);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            showDBError(throwables);
        }
    }

}
