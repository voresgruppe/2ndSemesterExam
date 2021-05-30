package dk.vores.gui.loginView;

import dk.vores.BLL.UserManager;
import dk.vores.be.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginViewControllerTest {

    @Test
    void tryLogin() {
        //Arrange
        LoginViewController loginViewController = new LoginViewController();
        UserManager uMan = new UserManager();

        //Act
        User actual = loginViewController.tryLogin("test", "test", true);
        int expected =  1;

        //Assert
        Assertions.assertEquals(actual.getId(),expected);
    }
}