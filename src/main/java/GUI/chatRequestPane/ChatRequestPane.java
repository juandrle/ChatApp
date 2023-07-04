package GUI.chatRequestPane;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ChatRequestPane extends HBox {
    Label chatRequest;
    Button confirm;
    Button decline;

    public ChatRequestPane(){
        chatRequest = new Label("You got a new ChatRequest!");
        confirm = new Button("Confirm");
        confirm.setId("confirmButton");
        decline = new Button("Decline");
        decline.setId("declineButton");
        HBox buttonBox = new HBox(confirm, decline);
        buttonBox.setSpacing(15);
        buttonBox.setTranslateX(70);
        buttonBox.setAlignment(Pos.CENTER);
        this.getChildren().addAll(chatRequest, buttonBox);
        this.setPrefHeight(40);
        this.setMaxHeight(40);
        this.setAlignment(Pos.CENTER);
        setId("chatRequest");
    }

    public void setChatRequestText(String username) {
        chatRequest.setText("You got a new request to chat from " + username);
    }
}
