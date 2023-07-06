package Model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.*;
import java.io.IOException;
import java.util.Arrays;


public class Client {
    DatagramSocket udpSocket;
    ObservableList<String> clients = FXCollections.observableArrayList();
    ObservableList<Message> message = FXCollections.observableArrayList();


    byte[] buffer = new byte[65000];
    Socket socket;
    int port;
    String[] serverMessageArray;
    String address;
    InetAddress partnerAddress;
    InetAddress packetAddress;
    int packetPort;
    SimpleBooleanProperty requestReceived;
    SimpleBooleanProperty requestAccepted;
    SimpleStringProperty errMessage;
    String username;
    String password;
    Thread receiveMessages;

    DatagramPacket ackPacket;

    String serverMesage;
    private long fileSendingDuration;

    public Client(int port, String address) {

        this.port = port;
        this.address = address;
        try {
            socket = new Socket(address, port);

        } catch (IOException e) {
            e.printStackTrace();
        }
        requestReceived = new SimpleBooleanProperty(false);
        requestAccepted = new SimpleBooleanProperty(false);
        errMessage = new SimpleStringProperty();

    }

    public void startChatProtocol(String port, String ip) {
        String msg;
        boolean running = true;
        try {
            this.udpSocket = new DatagramSocket();
            while (running) {

                partnerAddress = InetAddress.getByName(ip.replace("/", "").strip());


                msg = "MESSAGE:" + this.username + ":-connection_established--";
                //sendClientMessage(msg);
                //message.add(new Message(this.username, msg.split(":")[1]));
                System.out.println(msg);
                buffer = msg.getBytes();

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, partnerAddress, Integer.parseInt(port));
                udpSocket.send(packet);


                running = false;
                startChatProtocol();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void startChatProtocol() throws SocketException {
        buffer = new byte[65535];
        receiveMessages = new Thread(this::receiveClientMessage);
        receiveMessages.start();
    }

    public void receiveClientMessage() {
        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(packet);
                packetPort = packet.getPort();
                packetAddress = packet.getAddress();

                String received = new String(packet.getData(), 0, packet.getLength());
                String msgPrefix = received.split(":")[0].trim();
                System.out.println("RECEIVED: "+ received+ "PREFIX: "+ msgPrefix);
                switch (msgPrefix) {
                    case "MESSAGE" -> {
                        byte[] ackBytes = "ACK".getBytes();
                        DatagramPacket ack = new DatagramPacket(ackBytes, ackBytes.length,packetAddress,packetPort);
                        udpSocket.send(ack);
                        System.out.println("ACK SEND");


                        String[] receivedArray = received.split(":");
                        String username = receivedArray[1];
                        if (receivedArray.length > 3)
                            for (String parts : Arrays.stream(receivedArray).toList().subList(3, receivedArray.length)) {
                                receivedArray[2] += ":" + parts;
                            }


                        System.out.println("Received packet");
                        if (received.endsWith(":-connection_established--")) sendClientMessage("MESSAGE:CONNECTION_OK");
                        if (!received.endsWith(":-connection_established--") && !received.endsWith("CONNECTION_OK"))
                            message.add(new Message(username, receivedArray[2]));





                        System.out.println(receivedArray[1]);
                    }
                    case "FILE" -> {
                        int totalPackets = Integer.parseInt(received.split(":")[4]);
                        String suffix = received.split(":")[5];
                        fileSendingDuration = System.currentTimeMillis();

                        // Create a new file to save the received data
                        File receivedFile = new File("received_file." + suffix);
                        try (BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(receivedFile))) {
                            for (int packetNumber = 1; packetNumber <= totalPackets; packetNumber++) {

                                udpSocket.receive(packet);
                                fileOutputStream.write(packet.getData(), 0, packet.getLength());
                                byte[] ackBytes = "ACK".getBytes();
                                DatagramPacket ack = new DatagramPacket(ackBytes, ackBytes.length,packetAddress,packetPort);
                                udpSocket.send(ack);
                                System.out.println("Received packet " + packetNumber + " of " + totalPackets);
                            }
                            System.out.println("File received successfully.");
                            fileSendingDuration -= System.currentTimeMillis();
                            message.add(new Message(username, "File received successfully in " + (-fileSendingDuration) + "ms"));
                            fileSendingDuration = 0;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    case "ACK" ->{
                        ackPacket = packet;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void clientLoop() throws IOException {

        Thread serverMessageHandler = new Thread(this::serverMessageHandler);
        serverMessageHandler.start();
        //Thread userInputHandler = new Thread(this::userInputHandler);
        //userInputHandler.start();

    }

    public void serverMessageHandler() {
        boolean connected = true;
        try {
            while (connected) {
                serverMessageArray = getServerMessage(socket).split(" ");
                serverMesage = serverMessageArray[0];
                serverMessageArray[0] = "";
                System.out.println("RECEIVED FROM SERVER: " + serverMesage);
                switch (serverMesage.trim()) {
                    case "CLIENT_LIST" -> {
                        clients.clear();
                        clients.addAll(Arrays.asList(serverMessageArray).subList(1, serverMessageArray.length - 1));
                        clients.remove(username);
                        System.out.println(clients);
                    }
                    case "REQUEST_ACCEPTED" -> {
                        System.out.println("starting chat!");
                        requestAccepted.set(true);
                        String chatPartnerPort = serverMessageArray[1].split(";")[0];
                        String chatPartnerIP = serverMessageArray[1].split(";")[1];
                        System.out.println(serverMessageArray[0] + serverMessageArray[1]);

                        startChatProtocol(chatPartnerPort, chatPartnerIP);
                    }
                    case "CHAT_REQUEST" -> {
                        System.out.println("Chat Einladung erhalten! y: zum akzeptieren ODER n: zum ablehnen!");
                        requestReceived.set(true);
                    }
                    case "EXIT_SUCCESSFUL" -> {
                        socket.close();
                        connected = false;
                    }
                    case "USER_NOT_FOUND" -> System.out.println("Benutzer nicht gefunden, bitte erneut eingeben!");
                    case "REQUEST_DENIED" -> System.out.println("ABGELEHNT");
                    default -> System.out.println(serverMesage);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // GUI METHODS
    public synchronized void sendClientFile(File file) {
        String msgPrefix = "FILE:";
        try (BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(file))) {
            String suffix = ":" + file.getPath().split("\\.")[file.getPath().split("\\.").length - 1];
            int maxPacketSize = udpSocket.getSendBufferSize() - 128;
            byte[] fileBuffer = new byte[maxPacketSize-128];
            System.out.println(fileBuffer.length);
            long fileSize = file.length();
            int totalPackets = (int) Math.ceil((double) fileSize / maxPacketSize);

            // Send total packets information
            System.out.println("SEND: "+ msgPrefix + "TOTAL_PACKETS:"+ totalPackets + suffix);
            sendClientMessage(msgPrefix + "TOTAL_PACKETS:" + totalPackets + suffix);
            wait(1);

            DatagramPacket packet;
            int bytesRead;
            int packetNumber = 1;

            while ((bytesRead = fileInputStream.read(fileBuffer)) != -1) {
                packet = new DatagramPacket(fileBuffer, bytesRead, packetAddress, packetPort);
                boolean packetSent = false;

                while (!packetSent) {
                    try {
                        ackPacket = null;
                        udpSocket.send(packet);
                        System.out.println("Sent packet " + packetNumber + " of " + totalPackets);
                        packetSent = true;
                    } catch (IOException e) {
                        // Error occurred while sending the packet, retry
                        e.printStackTrace();
                        System.err.println("Error sending packet " + packetNumber + ". Retrying...");
                    }
                }
                // Wait for acknowledgment
                boolean ackReceived = false;
                while (!ackReceived) {
                    try {
                        //udpSocket.receive(packet);
                        // Assuming the acknowledgment message is "ACK"
                        String ackMessage = new String(ackPacket.getData(), 0, ackPacket.getLength());
                        if (ackMessage.equals("ACK")) {
                            System.out.println("Received acknowledgment for packet " + packetNumber);
                            ackReceived = true;
                        }
                    } catch (Exception e) {
                        // Error occurred while receiving acknowledgment, retry
                        System.err.println("Error receiving acknowledgment for packet " + packetNumber + ". Retrying...");
                    }
                }
                packetNumber++;
                fileBuffer = new byte[maxPacketSize];
            }

            System.out.println("File sent successfully.");
            fileSendingDuration -= System.currentTimeMillis();
            message.add(new Message(this.username, "File sent successfully in " + (-fileSendingDuration) + "ms"));
            fileSendingDuration = 0;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void sendClientMessage(String msg) {
        byte[] messageBytes;
        String msgPrefix = "";
        if (!msg.startsWith("FILE:")) {
            msgPrefix = "MESSAGE:";
        }else if(msg.startsWith("FILE:")){
            msgPrefix = "FILE:";
        }
        msg = msgPrefix + username + ":" + msg;
        messageBytes = msg.getBytes();
        try {
            int maxPacketSize = udpSocket.getSendBufferSize() - 4;
            buffer = new byte[maxPacketSize];
            int totalPackets = (int) Math.ceil((double) messageBytes.length / maxPacketSize);
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, packetAddress, packetPort);
            String[] sendArray = msg.split(":");
            if (sendArray.length > 3)
                for (String parts : Arrays.stream(sendArray).toList().subList(3, sendArray.length)) {
                    sendArray[2] += ":" + parts;
                }
            if (!msg.endsWith(":-connection_established--") && !msg.endsWith("CONNECTION_OK") && !msgPrefix.equals("FILE:"))
                message.add(new Message(this.username, sendArray[2]));
            if (msgPrefix.equals("FILE:")) {
                message.add(new Message(this.username, "Sending File..."));
                fileSendingDuration = System.currentTimeMillis();
            }
            udpSocket.send(packet);

            //buffer = msg.getBytes();

            /*for (int i = 1; i < totalPackets; i++) {
                System.out.println(i);
                int offset = i * maxPacketSize;
                int length = Math.min(maxPacketSize, messageBytes.length - offset);
                buffer = new byte[length];
                System.arraycopy(messageBytes, offset, buffer, 0, length);

                packet = new DatagramPacket(buffer, buffer.length, packetAddress, packetPort);
                udpSocket.send(packet);
            }*/
            if(!msg.endsWith("CONNECTION_OK")) {
                boolean ackReceived = false;
                while (!ackReceived) {

                    try {
                        //udpSocket.receive(packet);
                        // Assuming the acknowledgment message is "ACK"
                        String ackMessage = new String(ackPacket.getData(), 0, ackPacket.getLength());
                       //udpSocket.setSoTimeout(3000);
                        if (ackMessage.equals("ACK")) {
                            System.out.println("Received acknowledgment for packet ");
                            ackReceived = true;
                        }
                    } catch (Exception e) {
                        // Error occurred while receiving acknowledgment, retry
                        e.printStackTrace();
                        System.err.println("Error receiving acknowledgment for packet. Retrying...");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean einloggen(String username, String password) throws IOException, InterruptedException {
        this.password = password;
        this.username = username;
        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());

        String userData = "LOGIN " + username + ";" + password;
        sendServerMessage(userData);
        String serverCmd = getServerMessage(socket).strip();

        switch (serverCmd) {
            case "REG_OK" -> {
                System.out.println("Du bist mit dem Server verbunden!");
                sendServerMessage("GET_CLIENTS");
                clientLoop();
                return true;
            }
            case "PASSWORD_INVALID" -> {
                System.out.println("Passwort falsch bitte neu versuchen!");
                System.out.println(userData);
                errMessage.set("The password is invalid");
                return false;
            }
            case "REG_NEW" -> {
                System.out.println("Du wurdest als neue Benutzer angemeldet");
                //dataOut.writeUTF("GET_CLIENTS");
                sendServerMessage("GET_CLIENTS");
                clientLoop();
                return true;
            }
            case "ALREADY_ONLINE" -> {
                errMessage.set("The user is already logged in");
                return false;
            }
            default -> {
                System.out.println(serverCmd);
            }
        }

        return false;
    }

    public void confirmChatRequest() throws IOException {
        udpSocket = new DatagramSocket();
        sendServerMessage("REQUEST_ACCEPTED " + udpSocket.getLocalPort());
        startChatProtocol();
        requestAccepted.set(true);
        requestReceived.set(false);
    }
    public void declineChatRequest() throws IOException {
        sendServerMessage("REQUEST_DENIED");
        requestReceived.set(false);
    }
    public void connection(String username) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
        try {
            sendServerMessage("CONNECT " + username);
        } catch (Exception ignored) {

        }
    }

    public void sendServerMessage(String message) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
        try {
            dataOut.writeUTF(message);

        } catch (Exception ignored) {

        }
    }

    String getServerMessage(Socket socket) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        char[] buffer = new char[200];
        int anzahlZeichen = bufferedReader.read(buffer, 0, 200);
        return new String(buffer, 0, anzahlZeichen);
    }

    void setUsername(String username) {
        this.username = username;
    }


    void setPassword(String password) {
        this.password = password;
    }

    String getPassword() {
        return this.password;
    }

    public ObservableList<String> getClients() {
        return clients;
    }

    public ObservableList<Message> getMessage() {
        return message;
    }

    public SimpleBooleanProperty requestReceivedProperty() {
        return requestReceived;
    }

    public String getUsername() {
        return this.username;
    }

    public void disconnect() throws IOException {
        sendServerMessage("EXIT");
    }

    public SimpleBooleanProperty requestAcceptedProperty() {
        return requestAccepted;
    }

    public SimpleStringProperty errMessageProperty() {
        return errMessage;
    }

    // CONSOLE METHODS
    public void userConsoleLogin() {
        boolean registerd = false;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Geben sie ihren Username ein!");
            username = bufferedReader.readLine();
            System.out.println("Geben sie ihr Passwort ein!");
            password = bufferedReader.readLine();

            String userData = username + ";" + password;
            sendServerMessage(userData);


            if (!registerd) {
                while (!registerd) {
                    switch (getServerMessage(socket).strip()) {
                        case "REG_OK":
                            System.out.println("Du bist mit dem Server verbunden!");
                            registerd = true;
                            break;

                        case "PASSWORD_INVALID":
                            System.out.println("Passwort falsch bitte neu versuchen!");
                            password = bufferedReader.readLine();
                            userData = username + ";" + password;
                            sendServerMessage(userData);
                            break;

                        case "REG_NEW":
                            System.out.println("Du wurdest als neue Benutzer angemeldet");
                            registerd = true;
                            break;
                    }
                }
            }
        } catch (IOException e) {

        }
    }
    public void userInputHandler() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String[] eingabe = bufferedReader.readLine().split(" ");
                switch (eingabe[0]) {
                    case "!clients":
                        sendServerMessage("GET_CLIENTS");
                        break;

                    case "!connect":
                        sendServerMessage(eingabe[1]);

                        break;

                    case "y":
                        if (requestReceived.get()) {
                            udpSocket = new DatagramSocket();
                            sendServerMessage("REQUEST_ACCEPTED " + udpSocket.getLocalPort());
                            System.out.println("Starting chat!");
                            startChatProtocol();
                        }
                        break;

                    case "n":
                        if (requestReceived.getValue()) {
                            sendServerMessage("REQUEST_DENIED");
                        }
                        break;

                    default:

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.print("Gib ein client ein zu dem du connecten willst!");
        }
    }

    public static void main(String args[]) {
        Client client = new Client(25655, "localhost");
        try {
            client.clientLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
