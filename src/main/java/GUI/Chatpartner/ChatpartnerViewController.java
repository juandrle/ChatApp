package GUI.Chatpartner;

import GUI.ChatApplication;
import GUI.Options.OptionsViewController;
import GUI.Scenes;
import GUI.ViewController;
import Model.Client;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ChatpartnerViewController extends ViewController<ChatApplication> {
    Client client;
    ChatpartnerView view;
    OptionsViewController optionsViewController;

    public ChatpartnerViewController(ChatApplication application, Client client) {
        super(application);
        this.client = client;
        rootView = new ChatpartnerView();
        view = (ChatpartnerView) rootView;
        optionsViewController = new OptionsViewController(application,client);
        initialize();
    }

    @Override
    public void initialize() {
        //view.setTop(optionsViewController.getView());
        view.username.setText(client.getUsername());
        view.chatPartner.setCellFactory(e -> new ListCell<>(){
            Label username = new Label("Dummy Name");
            Button connect = new Button("connect");
            HBox hBox = new HBox(username, connect);
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setId("clientCell");
                if (!empty) {
                    username.setText(item);
                    connect.setOnAction(e -> {
                        try {
                            client.connection(item);
                            application.switchScene(Scenes.CHAT_VIEW);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    setGraphic(hBox);

                } else setGraphic(null);
            }
        });
        view.chatPartner.setItems(client.getClients());
        client.getClients().addListener((ListChangeListener<? super String>) c -> {
            view.chatPartner.setItems(client.getClients());
        });
        client.requestReceivedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(client.requestReceivedProperty().get());
                application.switchScene(Scenes.CHAT_VIEW);
        });
    }
}
