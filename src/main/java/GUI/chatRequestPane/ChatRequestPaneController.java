package GUI.chatRequestPane;

import GUI.ChatApplication;
import GUI.ViewController;
import GUI.chatRequestPane.ChatRequestPane;
import Model.Client;

import java.io.IOException;

public class ChatRequestPaneController extends ViewController<ChatApplication> {
    ChatRequestPane view;
    Client client;

    public ChatRequestPaneController(ChatApplication application, Client client) {
        super(application);
        this.client = client;
        rootView = new ChatRequestPane();
        view = (ChatRequestPane) rootView;
        initialize();
    }

    @Override
    public void initialize() {
        view.confirm.setOnAction(e -> {
            try {
                client.confirmChatRequest();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        view.decline.setOnAction(e -> {
            try {
                client.declineChatRequest();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
