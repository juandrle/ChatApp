package GUI.Chatpartner;

import Model.Client;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
// View to show all available Chatpartner in ListView of Clients (might change)
public class ChatpartnerView extends BorderPane {
    ListView<String> chatPartner;
    Label username;

    public ChatpartnerView(){
        username = new Label();
        username.setStyle("-fx-font-size: 30; -fx-text-fill: white");
        setTop(username);

        chatPartner = new ListView<>();
        chatPartner.setId("clientList");
        VBox vBox = new VBox(chatPartner);
        setCenter(vBox);
        vBox.setId("background");
        setId("chatpartner");
    }
}
