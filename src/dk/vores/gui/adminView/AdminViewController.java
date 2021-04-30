package dk.vores.gui.adminView;

import dk.vores.be.User;

public class AdminViewController {
    private User loggedAdmin;

    public User getLoggedAdmin() {
        return loggedAdmin;
    }

    public void setLoggedAdmin(User loggedAdmin) {
        this.loggedAdmin = loggedAdmin;
    }
}
