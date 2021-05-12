package dk.vores.BLL;

import dk.vores.DAL.UserViewRepository;
import dk.vores.be.User;
import dk.vores.be.UserView;
import javafx.collections.ObservableList;

public class UserViewManager {
    private UserViewRepository uvRepo;
    public UserViewManager(){
        uvRepo = new UserViewRepository();
    }

    public UserView getViewFromID(int id){
        return uvRepo.loadViewFromID(id);
    }

    public void addViewToUser(User u, String type, String source){
        uvRepo.addViewToUser(u,type,source);
    }

    public void clearViewFromUser(User u){
        uvRepo.clearViewFromUser(u);
    }

    public void updateTypeFromID(int uvID, String newType){
        uvRepo.updateTypeFromID(uvID,newType);
    }

    public ObservableList<UserView> loadViewsFromUserID(int userID){
        return uvRepo.loadViewsFromUserID(userID);
    }

    public void removeViewFromUser(int id) {
        uvRepo.removeViewFromUser(id);
    }

    public void updatePositionFromID(int uvID, int newStartX, int newStartY, int newEndX, int newEndY){
        uvRepo.updateViewPlacement(uvID, newStartX, newStartY, newEndX, newEndY);

    }
}
