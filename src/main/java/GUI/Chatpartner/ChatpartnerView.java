package GUI.Chatpartner;

import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
// View to show all available Chatpartner in ListView of Clients (might change)
public class ChatpartnerView extends BorderPane {
    ListView<String> chatPartner;


    public ChatpartnerView(){

        chatPartner = new ListView<>();
        chatPartner.setId("clientList");
        VBox vBox = new VBox(chatPartner);
        setCenter(vBox);

        vBox.setId("background");
        setId("chatpartner");
    }
}
