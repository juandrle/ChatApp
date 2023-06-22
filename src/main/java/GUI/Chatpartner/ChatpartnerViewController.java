package GUI.Chatpartner;

import GUI.ChatApplication;
import GUI.ViewController;
import Model.Client;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatpartnerViewController extends ViewController<ChatApplication> {
    Client client;
    ChatpartnerView view;

    public ChatpartnerViewController(ChatApplication application, Client client) {
        super(application);
        this.client = client;
        rootView = new ChatpartnerView();
        view = (ChatpartnerView) rootView;
    }

    @Override
    public void initialize() {
        view.chatPartner.setCellFactory(e -> new ListCell<>(){
            Label username = new Label("Dummy Name");
            Button connect = new Button("connect");
            HBox hBox = new HBox(username, connect);
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(hBox);
                if (!empty) {
                    username.setText("input getter here");
                    setGraphic(hBox);

                }
            }
        });
    }
}
