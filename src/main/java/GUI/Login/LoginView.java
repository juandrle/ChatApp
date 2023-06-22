package GUI.Login;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class LoginView extends BorderPane {
    Label header;
    TextField username;
    TextField password;
    Button loginButton;

    LoginView() {
        header = new Label("Please input Your Username and Password");
        header.setId("header");
        username = new TextField();
        username.setId("formTextfield");
        password = new TextField();
        password.setId("formTextfield");
        loginButton = new Button("Login/Register");
        VBox menu = new VBox(header, username,password,loginButton);
        menu.setSpacing(15);
        menu.setAlignment(Pos.CENTER);
        setCenter(menu);
    }

}
