package GUI.Options;


import GUI.ChatApplication;
import GUI.Scenes;
import GUI.ViewController;
import Model.Client;

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
        view.logout.setOnAction(e -> application.switchScene(Scenes.LOGIN_VIEW));
        view.disconnect.setOnAction(e -> application.switchScene(Scenes.CHATPARTNER_VIEW));
    }

    public OptionsView getView() {
        return view;
    }
}
