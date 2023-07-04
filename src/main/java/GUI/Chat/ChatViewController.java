package GUI.Chat;

import GUI.ChatApplication;
import GUI.Options.OptionsViewController;
import GUI.ViewController;
import Model.Client;
import Model.Message;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class ChatViewController extends ViewController<ChatApplication> {
    Client client;
    ChatView view;
    OptionsViewController optionsViewController;

    public ChatViewController(ChatApplication application, Client client) {
        super(application);
        this.client = client;
        rootView = new ChatView();
        view = (ChatView) rootView;
        optionsViewController = new OptionsViewController(application, client);
        initialize();

    }

    @Override
    public void initialize() {
        view.setTop(optionsViewController.getView());
        // hier send Message funktion
        // view.sendButton.setOnAction(e -> );
        view.messageHistory.setCellFactory(e -> new ListCell<>() {
            Label username = new Label();
            Label message = new Label();
            VBox vBox = new VBox(username, message);

            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                setId("chatCell");
                vBox.setId("chatCellBox");
                username.setId("username");
                if (!empty) {
                    username.setText(item.getUser());
                    if (username.getText().equals(client.getUsername()))
                        vBox.setId("chatCellBoxOwn");
                    else vBox.setId("chatCellBox");
                    message.setText(item.getMessage());
                    Platform.runLater(() -> setGraphic(vBox));
                }
            }
        });
        client.getMessage().addListener((ListChangeListener<? super Message>) c -> {
            view.messageHistory.setItems(client.getMessage());
            Platform.runLater(()->view.messageHistory.scrollTo(client.getMessage().size()-1));
        });
        view.messageArea.setOnKeyPressed(event -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                client.sendClientMessage(view.messageArea.getText());
                view.messageArea.clear();
            }

        });
        view.sendMsgButton.setOnAction(e -> {
            client.sendClientMessage(view.messageArea.getText());
            view.messageArea.clear();
        });
        view.sendDataButton.setOnAction(e -> {
            File file = view.fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                client.sendClientFile(file);
            }
        });
    }
}
