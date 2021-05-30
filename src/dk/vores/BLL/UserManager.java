package dk.vores.BLL;

import dk.vores.DAL.UserRepository;
import dk.vores.be.User;
import javafx.collections.ObservableList;

public class UserManager {

    private final UserRepository userRepository;

    public UserManager(){
        userRepository = new UserRepository();
    }

    //public User getUserFromID(int id){ return userRepository.getUserFromID(id); }

    public User login(String username, String password){
        return userRepository.login(username,password);
    }

    public ObservableList<User> getAllUsers(){
        return userRepository.loadUsers();
    }

    public ObservableList<User> getUsersWithoutAdmins(){
        return userRepository.loadUsersWithoutAdmins();
    }

    public void addUser(User user){
        user.setId(userRepository.addUser(user));
    }

    public void deleteUser(User user){
        userRepository.delete(user);
    }

}
