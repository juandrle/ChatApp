module com.example.chats {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens GUI to javafx.fxml;
    exports GUI;
    exports GUI.chatRequestPane;
    opens GUI.chatRequestPane to javafx.fxml;
}