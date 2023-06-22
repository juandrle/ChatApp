package GUI.Chat;

import Model.Message;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
// View to show the Chat
public class ChatView extends BorderPane {
    TextArea messageArea;
    ListView<Message> messageHistory;

    public ChatView(){
        messageArea = new TextArea();
        messageHistory = new ListView<>();

    }
}
