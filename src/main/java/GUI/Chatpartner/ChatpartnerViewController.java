package GUI.Chatpartner;

import GUI.*;
import GUI.Chat.ChatViewController;
import GUI.Options.OptionsViewController;
import GUI.chatRequestPane.ChatRequestPane;
import GUI.chatRequestPane.ChatRequestPaneController;
import Model.Client;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;

public class ChatpartnerViewController extends ViewController<ChatApplication> {
    Client client;
    ChatpartnerView view;
    OptionsViewController optionsViewController;
    ChatViewController chatViewController;
    ChatRequestPane chatRequestPane;
    ChatRequestPaneController chatRequestPaneController;

    public ChatpartnerViewController(ChatApplication application, Client client) {
        super(application);
        this.client = client;
        chatRequestPaneController = new ChatRequestPaneController(application, client);
        chatRequestPane = (ChatRequestPane) chatRequestPaneController.getRootView();
        rootView = new ChatpartnerView();
        view = (ChatpartnerView) rootView;
        optionsViewController = new OptionsViewController(application,client);
        chatViewController = new ChatViewController(application, client);
        application.getScenes().put(Scenes.CHAT_VIEW, chatViewController.getRootView());
        initialize();
    }

    @Override
    public void initialize() {
        view.setBottom(chatRequestPane);
        chatRequestPane.setTranslateY(chatRequestPane.getPrefHeight() + 1);
        view.setTop(optionsViewController.getView());
        view.chatPartner.setCellFactory(e -> new ListCell<>(){
            Label username = new Label("Dummy Name");
            Button connect = new Button("connect");
            StackPane stackPane = new StackPane(username, connect);

            @Override
            protected void updateItem(String item, boolean empty) {

                super.updateItem(item, empty);
                setId("clientCell");
                if (!empty) {
                    username.setText(item);
                    StackPane.setAlignment(username, Pos.CENTER_LEFT);
                    StackPane.setAlignment(connect, Pos.CENTER_RIGHT);
                    connect.setOnAction(e -> {
                        try {
                            client.connection(item.strip());
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    setGraphic(stackPane);
                    //connect.setTranslateX((200) - username.getPrefWidth());

                } else  setGraphic(null);
            }
        });
        view.chatPartner.setItems(client.getClients());
        client.getClients().addListener((ListChangeListener<? super String>) c -> {
            view.chatPartner.setItems(client.getClients());
        });
        client.requestReceivedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) animationIn();
            else animationOut();
        });
        client.requestAcceptedProperty().addListener((observable, oldValue, newValue) -> {
            application.switchScene(Scenes.CHAT_VIEW);
        });
    }
    private void animationIn() {
        TranslateTransition transitionAnim = new TranslateTransition();
        transitionAnim.setNode(chatRequestPane);
        transitionAnim.setToY(0);
        transitionAnim.setDuration(Duration.millis(250));
        transitionAnim.setInterpolator(Interpolator.EASE_OUT);

        transitionAnim.playFromStart();
    }
    private void animationOut() {
        TranslateTransition transitionAnimBack = new TranslateTransition();
        transitionAnimBack.setNode(chatRequestPane);
        transitionAnimBack.setToY(chatRequestPane.getPrefHeight() + 1);
        transitionAnimBack.setDuration(Duration.millis(200));
        transitionAnimBack.setInterpolator(Interpolator.EASE_IN);

        transitionAnimBack.playFromStart();
    }
}
