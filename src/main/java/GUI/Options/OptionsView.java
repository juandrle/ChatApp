package GUI.Options;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class OptionsView extends MenuBar {
    public Menu menu;
    public MenuItem disconnect;
    public MenuItem logout;
    public OptionsView(){
        disconnect = new MenuItem("Disconnect");
        logout = new MenuItem("Logout");
        menu = new Menu("Connection");
        logout.setId("menu");
        disconnect.setId("menu");
        menu.getItems().addAll(disconnect,logout);
        this.getMenus().add(menu);
        menu.setId("menu");
        this.setId("menubar");
    }
}
