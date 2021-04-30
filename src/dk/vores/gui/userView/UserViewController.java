package dk.vores.gui.userView;

import dk.vores.be.User;

public class UserViewController {
    private User loggedUser;

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }
}
