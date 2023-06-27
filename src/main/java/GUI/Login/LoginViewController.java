package GUI.Login;

import GUI.ChatApplication;
import GUI.Scenes;
import GUI.ViewController;
import Model.Client;

public class LoginViewController extends ViewController<ChatApplication> {
    LoginView view;
    Client client;

    public LoginViewController(ChatApplication application, Client client) {
        super(application);
        this.client = client;
        rootView = new LoginView();
        view = (LoginView) rootView;
        initialize();
    }

    @Override
    public void initialize() {

        view.loginButton.setOnAction(e -> {
            System.out.println("LoginButton pressed");
            // implement check to Server here if Username and Password are right from MultiServerMethod
            System.out.println(view.username.getText() + " " + view.password.getText());
            if (view.username.getText().isBlank() || view.password.getText().isBlank()) {
                return; // hier animation für fehler
            }
            application.switchScene(Scenes.CHATPARTNER_VIEW);
        });
    }
}
