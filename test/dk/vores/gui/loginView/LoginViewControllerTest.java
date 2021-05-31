package dk.vores.gui.loginView;

import dk.vores.BLL.UserManager;
import dk.vores.be.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginViewControllerTest {

    @Test
    void tryLogin1() {
        //Arrange
        LoginViewController loginViewController = new LoginViewController();

        //Act
        User actual = loginViewController.tryLogin("test", "test", true);
        int expected =  1;

        //Assert
        Assertions.assertEquals(actual.getId(),expected);
    }

    @Test
    void tryLogin2() {
        //Arrange
        LoginViewController loginViewController = new LoginViewController();

        //Act
        User actual = loginViewController.tryLogin("test2", "test2", true);
        String expected =  "test2";

        //Assert
        Assertions.assertEquals(actual.getUsername(),expected);
    }

    @Test
    void tryLogin3() {
        //Arrange
        LoginViewController loginViewController = new LoginViewController();

        //Act
        User actual = loginViewController.tryLogin("antitest", "antitest", true);

        //Assert
        assertNull(actual);
    }
}