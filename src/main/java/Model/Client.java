package Model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;


public class Client {
    DatagramSocket udpSocket;
    ObservableList<String> clients = FXCollections.observableArrayList();
    ObservableList<Message> message = FXCollections.observableArrayList();


    byte buffer[] = new byte[65000];
    Socket socket;
    int port;
    String serverMessageArray[];

    String address;
    InetAddress partnerAddress;
    InetAddress packetAddress;
    int packetPort;
    SimpleBooleanProperty requestReceived ;
    String username;
    String password;

    String serverMesage;

    public Client(int port, String address) {

        this.port = port;
        this.address = address;
        try {
            socket = new Socket(address, port);

        } catch (IOException e) {
            e.printStackTrace();
        }
        requestReceived = new SimpleBooleanProperty(false);

    }

    public void startChatProtocol(String port, String ip) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String msg;
        boolean running = true;
        try {
            while (running) {

                partnerAddress = InetAddress.getByName(ip.replace("/", "").strip());

                udpSocket = new DatagramSocket();

                msg = "-connection:established--";
                message.add(new Message(this.username, msg));
                System.out.println(msg);
                //buffer = msg.getBytes();

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, partnerAddress, Integer.parseInt(port));
                udpSocket.send(packet);
                startChatProtocol();
                running = false;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void startChatProtocol() {
        buffer = new byte[65000];
        Thread receiveMessages = new Thread(this::receiveClientMessage);
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
                message.add(new Message(received.split(":")[0], received.split(":")[1]));

                System.out.println(received);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendClientFile(File file){

    }

    public void sendClientMessage(String msg) {
        byte[] messageBytes;
        msg = username + ": " + msg;
        messageBytes = msg.getBytes();
        try {
            int maxPacketSize = udpSocket.getSendBufferSize() - 4;
            buffer = new byte[maxPacketSize];
            int totalPackets = (int) Math.ceil((double) messageBytes.length / maxPacketSize);
            System.out.println("TOTAL PACKETS: " + totalPackets + " MAXPACKETSIZE: " + maxPacketSize + " MESSAGEBYTES: " + messageBytes);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, packetAddress, packetPort);
            message.add(new Message(this.username, msg.split(":")[1]));
            udpSocket.send(packet);

            //buffer = msg.getBytes();

            for (int i = 0; i < totalPackets; i++) {
                System.out.println(i);
                int offset = i * maxPacketSize;
                int length = Math.min(maxPacketSize, messageBytes.length - offset);
                buffer = new byte[length];
                System.arraycopy(messageBytes, offset, buffer, 0, length);

                packet = new DatagramPacket(buffer, buffer.length, packetAddress, packetPort);
                udpSocket.send(packet);


            }
        } catch (Exception e) {

        }

    }

    public boolean einloggen(String username, String password) throws IOException, InterruptedException {
        this.password = password;
        this.username = username;
        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());

        String userData ="LOGIN "+ username + ";" + password;
        sendServerMessage(userData);

        switch (getServerMessage(socket).strip()) {
            case "REG_OK" -> {
                System.out.println("Du bist mit dem Server verbunden!");
                //dataOut.writeUTF("GET_CLIENTS");
                sendServerMessage("GET_CLIENTS");
                clientLoop();
                return true;
            }
            case "PASSWORD_INVALID" -> {
                System.out.println("Passwort falsch bitte neu versuchen!");
                System.out.println(userData);
                return false;
            }
            case "REG_NEW" -> {
                System.out.println("Du wurdest als neue Benutzer angemeldet");
                //dataOut.writeUTF("GET_CLIENTS");
                sendServerMessage("GET_CLIENTS");
                clientLoop();
                return true;
            }
        }

        return false;
    }

    public static void main(String args[]) {
        Client client = new Client(25655, "localhost");
        try {
            client.clientLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clientLoop() throws IOException {

        Thread serverMessageHandler = new Thread(this::serverMessageHandler);
        serverMessageHandler.start();
        Thread userInputHandler = new Thread(this::userInputHandler);
        userInputHandler.start();

    }


    public void serverMessageHandler(){
        try {
            while (true) {
                serverMessageArray = getServerMessage(socket).split(" ");
                serverMesage = serverMessageArray[0];
                serverMessageArray[0] = "";
                System.out.println("RECEIVED FROM SERVER: " + serverMesage);
                switch (serverMesage.trim()) {
                    case "CLIENT_LIST":
                        clients.clear();
                        clients.addAll(Arrays.asList(serverMessageArray).subList(1, serverMessageArray.length));
                        System.out.println(clients);
                        break;

                    case "REQUEST_ACCEPTED":
                        System.out.println("starting chat!");
                        serverMesage = getServerMessage(socket);
                        String chatPartnerPort = serverMesage.split(";")[0];
                        String chatPartnerIP = serverMesage.split(";")[1];

                        startChatProtocol(chatPartnerPort, chatPartnerIP);
                        break;


                    case "CHAT_REQUEST":
                        System.out.println("Chat Einladung erhalten! y: zum akzeptieren ODER n: zum ablehnen!");
                        requestReceived.set(true);
                        udpSocket = new DatagramSocket();
                        //dataOut.writeUTF("REQUEST_ACCEPTED " + udpSocket.getLocalPort());
                        sendServerMessage("REQUEST_ACCEPTED " + udpSocket.getLocalPort());
                        System.out.println("Starting chat!");
                        startChatProtocol();

                        break;

                    case "USER_NOT_FOUND":
                        System.out.println("Benutzer nicht gefunden, bitte erneut eingeben!");
                        break;

                    case "REQUEST_DENIED":
                        System.out.println("ABGELEHNT");
                        break;

                    default:
                        System.out.println(serverMesage);
                }
            }
        }catch(IOException e){e.printStackTrace();}
    }

    public void userInputHandler(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String eingabe[] = bufferedReader.readLine().split(" ");
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
        }catch(IOException e){

        }
    }

    public void connection(String username) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
        try {
            sendServerMessage(username);
        }catch(Exception ignored){

        }
    }
    public void sendServerMessage(String message) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
        try {
            dataOut.writeUTF(message);

        }catch(Exception ignored){

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

}
