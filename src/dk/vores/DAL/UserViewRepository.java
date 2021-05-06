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

    DatabaseConnector db;
    private DataUtils dataUtils = new DataUtils();

    public UserViewRepository() {
        this.db = DatabaseConnector.getInstance();
    }

    public UserView loadViewFromID(int id){
        try(Connection connection = db.getConnection()){
            String query = "Select*from [UserView] WHERE [id] = '" + id +"'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            UserView uv = null;
            while(resultSet.next()) {
                uv = new UserView(resultSet.getInt("id"), resultSet.getInt("userID"), resultSet.getInt("startx"), resultSet.getInt("starty"), resultSet.getInt("endx"), resultSet.getInt("endy"), dataUtils.typeFromString(resultSet.getString("type"), resultSet.getInt("id")), resultSet.getString("source"));
            }
            if(uv == null){
                //TODO error
            }
            return uv;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
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
                    UserView uv = new UserView(resultSet.getInt("id"), resultSet.getInt("userID"), resultSet.getInt("startx"), resultSet.getInt("starty"), resultSet.getInt("endx"), resultSet.getInt("endy"), dataUtils.typeFromString(resultSet.getString("type"), resultSet.getInt("id")), resultSet.getString("source"));
                    userViews.add(uv);
                }
            }
            return userViews;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public void addViewToUser(User u, String type, String source){
        try(Connection connection = db.getConnection()){
            String sql = "INSERT INTO [UserView] ([userID], [startX], [startY], [endX], [endY], [type], [source]) \n" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,u.getId());
            preparedStatement.setInt(2,211);
            preparedStatement.setInt(3,195);
            preparedStatement.setInt(4,633);
            preparedStatement.setInt(5,585);
            preparedStatement.setString(6,type);
            preparedStatement.setString(7,source);
            preparedStatement.executeUpdate();
        } catch (SQLServerException throwables) {
            System.out.println("server");
            showDBError(throwables);
        } catch (SQLException throwables) {
            System.out.println("sql");
            showDBError(throwables);
        }
    }

    public void clearViewFromUser(User u){
        try(Connection connection = db.getConnection()){
            String sql = "DELETE FROM [UserView] WHERE [userID] = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,u.getId());
            preparedStatement.executeQuery();
        } catch (SQLServerException throwables) {
            showDBError(throwables);
        } catch (SQLException throwables) {
            showDBError(throwables);
        }

    }

    public void updateTypeFromID(int uvID, String newType){
        try(Connection connection = db.getConnection()){
            String sql = "UPDATE [UserView] \n" +
                    "SET [type] = ? \n" +
                    "WHERE [id] = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,newType);
            preparedStatement.setInt(2,uvID);
            preparedStatement.executeQuery();
        }catch (SQLException throwables) {
            showDBError(throwables);
        }
    }

    public void removeViewFromUser(User u, String type, String source){
        try(Connection connection = db.getConnection()){
            String sql = "DELETE FROM [UserView] WHERE [type] = ? AND [source] = ? AND [userID] = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,type);
            ps.setString(2,source);
            ps.setInt(3,u.getId());
            ps.executeQuery();
        }catch (SQLException throwables) {
            showDBError(throwables);
        }
    }

    private void showDBError(SQLException throwables){
        String header = "Something went wrong..";
        UserError.showError(header, throwables.getMessage());
    }

    public void updateViewPlacement(int uvID, int newStartX, int newStartY, int newEndX, int newEndY){
        try(Connection connection = db.getConnection()){
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
