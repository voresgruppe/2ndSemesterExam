package dk.vores.BLL;

import dk.vores.DAL.UserViewRepository;
import dk.vores.be.User;

public class UserViewManager {
    private UserViewRepository uvRepo;
    public UserViewManager(){
        uvRepo = new UserViewRepository();
    }

    public void addViewToUser(User u, int startX, int startY, int endX, int endY, String type, String source){
        uvRepo.addViewToUser(u,startX,startY,endX,endY,type,source);
    }

    public void clearViewFromUser(User u){
        uvRepo.clearViewFromUser(u);
    }

    public void updateTypeSourceFromUser(User u, String oldType, String oldSource, String newType, String newSource){
        uvRepo.updateTypeSourceFromUser(u,oldType,oldSource,newType,newSource);
    }
}
