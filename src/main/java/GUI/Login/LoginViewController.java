package GUI.Login;

import GUI.ChatApplication;
import GUI.Chatpartner.ChatpartnerViewController;
import GUI.Options.OptionsViewController;
import GUI.Scenes;
import GUI.ViewController;
import Model.Client;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.io.IOException;

public class LoginViewController extends ViewController<ChatApplication> {
    LoginView view;
    Client client;
    OptionsViewController optionsViewController;
    ChatpartnerViewController chatpartnerViewController;

    public LoginViewController(ChatApplication application, Client client) {
        super(application);
        this.client = client;
        rootView = new LoginView();
        view = (LoginView) rootView;
        optionsViewController = new OptionsViewController(application, client);
        initialize();
    }

    @Override
    public void initialize() {
        view.setTop(optionsViewController.getView());
        view.loginButton.setOnAction(e -> {
            try {
                if (client.einloggen(view.username.getText(), view.password.getText())) {
                    chatpartnerViewController = new ChatpartnerViewController(application, client);
                    application.getScenes().put(Scenes.CHATPARTNER_VIEW, chatpartnerViewController.getRootView());
                    application.switchScene(Scenes.CHATPARTNER_VIEW);
                }
                else animation();
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            // implement check to Server here if Username and Password are right from MultiServerMethod
            //if (view.username.getText().isBlank() || view.password.getText().isBlank()) {
            //    return; // hier animation für fehler
            //}
            //application.switchScene(Scenes.CHATPARTNER_VIEW);
        });
    }

    private void animation() {
        SequentialTransition anim = new SequentialTransition();
        TranslateTransition transitionAnim = new TranslateTransition();
        transitionAnim.setNode(view.alertPane);
        transitionAnim.setToY(0);
        transitionAnim.setDuration(Duration.millis(250));
        transitionAnim.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition transitionAnimBack = new TranslateTransition();
        transitionAnimBack.setNode(view.alertPane);
        transitionAnimBack.setToY(view.alertPane.getPrefHeight() + 1);
        transitionAnimBack.setDelay(Duration.seconds(2));
        transitionAnimBack.setDuration(Duration.millis(200));
        transitionAnimBack.setInterpolator(Interpolator.EASE_IN);
        anim.getChildren().addAll(transitionAnim, transitionAnimBack);

        anim.playFromStart();
    }
}
