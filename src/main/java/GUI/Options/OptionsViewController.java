package GUI.Options;


import GUI.ChatApplication;
import GUI.Scenes;
import GUI.ViewController;
import Model.Client;

import java.io.IOException;

public class OptionsViewController {
    Client client;
    OptionsView view;
    ChatApplication application;

    public OptionsViewController(ChatApplication application, Client client) {
        this.application = application;
        this.client = client;
        view = new OptionsView();
        initialize();
    }
    public void initialize() {
        view.logout.setOnAction(e -> {
            try {
                client.disconnnect();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            application.switchScene(Scenes.LOGIN_VIEW);
        });
        view.disconnect.setOnAction(e -> {
            application.switchScene(Scenes.CHATPARTNER_VIEW);
            // Here needs to be UDP disconnect
        });
    }

    public OptionsView getView() {
        return view;
    }
}
