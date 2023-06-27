package GUI.Login;

import GUI.AlertPane;
import GUI.Options.OptionsView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class LoginView extends BorderPane {
    Label header;
    TextField username;
    PasswordField password;
    Button loginButton;
    AlertPane alertPane;

    LoginView() {
        header = new Label("Please input your \n username and password");
        header.setId("header");
        username = new TextField();
        username.setId("formTextfield");
        password = new PasswordField();
        password.setId("formPasswordfield");
        loginButton = new Button("Login/Register");
        VBox menu = new VBox(header, username,password,loginButton);
        menu.setId("mainMenu");
        menu.setSpacing(15);
        menu.setAlignment(Pos.CENTER);
        alertPane = new AlertPane();
        setBottom(alertPane);
        alertPane.setTranslateY(alertPane.getPrefHeight() + 1);
        setCenter(menu);
        setId("loginScreen");
    }

}
