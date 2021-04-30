package dk.vores.BLL;

import dk.vores.DAL.UserRepository;
import dk.vores.be.User;
import javafx.collections.ObservableList;

public class UserManager {

    private final UserRepository userRepository;

    public UserManager(){
        userRepository = new UserRepository();
    }

    public int login(String username, String password){
        return userRepository.login(username,password);
    }

    public User login1(String username, String password){
        return userRepository.login1(username,password);
    }

    public ObservableList<User> getAllUsers(){
        return userRepository.loadUsers();
    }


}
