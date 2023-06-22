package Model;
// class to save Messages for the MessageHistory in the Chat
public class Message {
    String user;
    String message;

    Message(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }
}
