package GUI.Chat;

import Model.Message;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

// View to show the Chat
public class ChatView extends BorderPane {
    TextArea messageArea;
    Button sendMsgButton;
    Button sendDataButton;
    ListView<Message> messageHistory;
    FileChooser fileChooser;

    public ChatView(){
        fileChooser = new FileChooser();
        messageArea = new TextArea();
        messageArea.setId("messageArea");
        messageHistory = new ListView<>();
        messageHistory.setId("msgHistory");
        sendMsgButton = new Button();
        sendMsgButton.setId("sendMsgButton");
        sendDataButton = new Button();
        sendDataButton.setId("sendDataButton");
        VBox sendBox = new VBox(sendDataButton, sendMsgButton);
        sendBox.setId("sendBox");
        HBox messageBox = new HBox(messageArea, sendBox);
        messageBox.setId("msgBackground");
        VBox centering = new VBox(messageBox);
        VBox msgHistory = new VBox(messageHistory);
        msgHistory.setId("historyBackground");
        setCenter(msgHistory);
        setBottom(centering);
        centering.setAlignment(Pos.CENTER);

        setId("chat");

    }
}
