package GUI.Login;

import GUI.ChatApplication;
import GUI.Chatpartner.ChatpartnerViewController;
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
    }

    @Override
    public void initialize() {
        view.loginButton.setOnAction(e -> {
            // implement check to Server here if Username and Password are right from MultiServerMethod
            application.switchScene(Scenes.CHATPARTNER_VIEW);
        });
    }
}
