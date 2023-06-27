package GUI.Chat;

import GUI.ChatApplication;
import GUI.ViewController;
import Model.Client;
import Model.Message;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

public class ChatViewController extends ViewController<ChatApplication> {
    Client client;
    ChatView view;
    public ChatViewController(ChatApplication application, Client client) {
        super(application);
        this.client = client;
        rootView = new ChatView();
        view = (ChatView) rootView;
        initialize();

    }

    @Override
    public void initialize() {
        // hier send Message funktion
       // view.sendButton.setOnAction(e -> );
        view.messageHistory.setCellFactory(e -> new ListCell<>(){
            Label username = new Label();
            Label message = new Label();
            VBox vBox = new VBox(username, message);

            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    username.setText(item.getUser());
                    message.setText(item.getMessage());
                    setGraphic(vBox);
                }
            }
        });
    }
}
