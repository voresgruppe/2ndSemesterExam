package dk.vores.BLL;

import dk.vores.DAL.UserReposetory;

public class UserManager {

    private final UserReposetory userReposetory;

    public UserManager(){
        userReposetory = new UserReposetory();
    }

    public int login(String username, String password){
        return userReposetory.login(username,password);
    }
}
