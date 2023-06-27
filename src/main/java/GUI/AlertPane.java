package GUI;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class AlertPane extends HBox {
    public Label alertLabel;
    public AlertPane() {
        alertLabel = new Label("Invalid Password");
        alertLabel.setStyle("-fx-text-fill:rgb(255,255,255);");

        this.getChildren().addAll(alertLabel);
        this.setStyle("-fx-background-color:rgb(255,0,0, 0.5);");
        this.setPrefHeight(30);
        this.setMaxHeight(30);
        this.setAlignment(Pos.CENTER);
    }


}
