package GUI;

import GUI.Login.LoginViewController;
import Model.Client;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;

public class ChatApplication extends Application {
    private Scene scene;
    private HashMap<Scenes, Pane> scenes;

    @Override
    public void init() throws Exception {
        scenes = new HashMap<>();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            ViewController<ChatApplication> controller;
            controller = new LoginViewController(this, new Client());
            scenes.put(Scenes.LOGIN_VIEW, controller.getRootView());

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

    public static void main(String[] args) {
        launch(args);
    }
}

