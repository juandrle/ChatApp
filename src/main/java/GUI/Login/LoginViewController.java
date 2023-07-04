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
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import java.io.IOException;

public class LoginViewController extends ViewController<ChatApplication> {
    LoginView view;
    Client client;
    OptionsViewController optionsViewController;
    ChatpartnerViewController chatpartnerViewController;

    public LoginViewController(ChatApplication application) {
        super(application);
        rootView = new LoginView();
        view = (LoginView) rootView;
        initialize();
    }

    @Override
    public void initialize() {
        view.loginButton.setOnAction(this::loginHandler);
        view.password.setOnKeyPressed(this::loginHandler);
        view.username.setOnKeyPressed(this::loginHandler);

    }

    private void loginHandler(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            login();
        }
    }

    private void login() {
        try {
            this.client = new Client(25656, "localhost");
            if (client.einloggen(view.username.getText(), view.password.getText())) {
                chatpartnerViewController = new ChatpartnerViewController(application, client);
                application.getScenes().put(Scenes.CHATPARTNER_VIEW, chatpartnerViewController.getRootView());
                application.switchScene(Scenes.CHATPARTNER_VIEW);
            } else {
                view.password.clear();
                view.alertPane.alertLabel.setText(client.errMessageProperty().get());
                animation();
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }


    private void loginHandler(ActionEvent actionEvent){
        login();
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
