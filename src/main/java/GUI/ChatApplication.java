package GUI;

import GUI.Chat.ChatViewController;
import GUI.Chatpartner.ChatpartnerViewController;
import GUI.Login.LoginViewController;
import Model.Client;
import Model.MultiServer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;

public class ChatApplication extends Application {
    private Scene scene;
    private HashMap<Scenes, Pane> scenes;
    Client client;

    @Override
    public void init() throws Exception {
        scenes = new HashMap<>();
        //client = new Client(25656, "localhost");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            ViewController<ChatApplication> controller;
            controller = new LoginViewController(this);
            scenes.put(Scenes.LOGIN_VIEW, controller.getRootView());
            // for making the style
            //controller = new ChatViewController(this, client);
            //scenes.put(Scenes.CHAT_VIEW, controller.getRootView());

            Pane root = scenes.get(Scenes.LOGIN_VIEW);
            scene = new Scene(root, 640, 800);
            scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchScene(Scenes sceneName) {
        Pane nextScene;

        if (scenes.containsKey(sceneName)) {
            nextScene = scenes.get(sceneName);
            scene.setRoot(nextScene);
        }
    }

    public HashMap<Scenes, Pane> getScenes() {
        return scenes;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

