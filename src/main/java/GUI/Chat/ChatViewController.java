package GUI.Chat;

import GUI.ChatApplication;
import GUI.ViewController;
import Model.Client;

public class ChatViewController extends ViewController<ChatApplication> {
    Client client;
    ChatView view;
    public ChatViewController(ChatApplication application, Client client) {
        super(application);
        this.client = client;
        rootView = new ChatView();
        view = (ChatView) rootView;

    }

    @Override
    public void initialize() {

    }
}
