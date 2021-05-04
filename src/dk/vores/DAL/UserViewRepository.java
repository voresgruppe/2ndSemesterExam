package dk.vores.DAL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.vores.DAL.db.DatabaseConnector;
import dk.vores.be.User;
import dk.vores.be.UserView;
import dk.vores.util.UserError;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class UserViewRepository {

    DatabaseConnector db;

    public UserViewRepository() {
        this.db = DatabaseConnector.getInstance();
    }

    public ObservableList<UserView> loadViewsFromUserID(int id){
        try(Connection connection = db.getConnection()){
            ObservableList<UserView> userViews = FXCollections.observableArrayList();
            String query = "Select * from [UserView] where [userID] ='"+id+"'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                boolean hasUserID = (resultSet.getInt("userID") == id);
                if(hasUserID) {
                    UserView uv = new UserView(resultSet.getInt("id"), resultSet.getInt("userID"), resultSet.getInt("startx"), resultSet.getInt("starty"), resultSet.getInt("endx"), resultSet.getInt("endy"), resultSet.getString("type"), resultSet.getString("source"));
                    userViews.add(uv);
                }
            }
            return userViews;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }


    public void addViewToUser(User u, int startX, int startY, int endX, int endY, String type, String source){
        try(Connection connection = db.getConnection()){
            String sql = "INSERT INTO [UserView] ([userID], [startX], [startY], [endX], [endY], [type], [source]) \n" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,u.getId());
            preparedStatement.setInt(2,startX);
            preparedStatement.setInt(3,startY);
            preparedStatement.setInt(4,endX);
            preparedStatement.setInt(5,endY);
            preparedStatement.setString(6,type);
            preparedStatement.setString(7,source);
            preparedStatement.executeQuery();
        } catch (SQLException throwables) {
            showDBError(throwables);
        }
    }

    public void clearViewFromUser(User u){
        try(Connection connection = db.getConnection()){
            String sql = "DELETE FROM [UserView] WHERE [userID] = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,u.getId());
            preparedStatement.executeQuery();
        } catch (SQLException throwables) {
            showDBError(throwables);
        }

    }

    public void updateTypeSourceFromUser(User u, String oldType, String oldSource, String newType, String newSource){
        try(Connection connection = db.getConnection()){
            String sql = "UPDATE [UserView] \n" +
                    "SET [type] = ?, [source] = ? \n" +
                    "WHERE [userID] = ? AND [type] = ? AND [source] = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,newType);
            preparedStatement.setString(2,newSource);
            preparedStatement.setInt(3,u.getId());
            preparedStatement.setString(4,oldType);
            preparedStatement.setString(5,oldSource);
            preparedStatement.executeQuery();
        } catch (SQLException throwables) {
            showDBError(throwables);
        }
    }

    private void showDBError(SQLException throwables){
        String header = "Something went wrong..";
        UserError.showError(header, throwables.getMessage());
    }

}
