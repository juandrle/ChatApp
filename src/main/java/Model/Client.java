package Model;

import javafx.scene.chart.PieChart;

import java.io.*;
import java.net.Socket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;


public class Client {
    DatagramSocket udpSocket;


    byte buffer[] = new byte[65000];
    Socket socket;
    int port;

    String address;
    InetAddress partnerAddress;
    InetAddress packetAddress;
    int packetPort;
    boolean requestReceived = false;
    String username;
    String password;

    String serverMesage;

    public Client(int port, String address){


        this.port = port;
        this.address = address;
        try {
            socket = new Socket(address,port);

        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void startChatProtocol(String port, String ip){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String msg;
        boolean running = true;
        try {
            while(running) {

                partnerAddress = InetAddress.getByName(ip.replace("/","").strip());

                udpSocket = new DatagramSocket();

                msg= "--connection established--";
                System.out.println(msg);
                buffer = msg.getBytes();

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, partnerAddress, Integer.parseInt(port));
                udpSocket.send(packet);
                startChatProtocol();
                running = false;
            }


        }catch(Exception e){
            e.printStackTrace();
        }


    }
    public void startChatProtocol(){
        buffer = new byte[65000];

        Thread receiveMessages = new Thread(this::receiveMessage);
        receiveMessages.start();


        InetAddress address;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String msg = username + ": ";
        try {
            while (true) {
                //System.out.println();
                msg = username + ": " +bufferedReader.readLine();

                System.out.println("                 " + msg);
                sendMessage(msg);


            }
        }catch(Exception e) {

        }
    }

    public void receiveMessage(){
        try{
            while(true) {

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(packet);
                packetPort= packet.getPort();
                packetAddress = packet.getAddress();

                String received = new String(packet.getData(), 0, packet.getLength());
                Thread.sleep(2000);
                System.out.println(received);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }
    public void sendMessage(String msg){
        byte[] messageBytes;

        messageBytes = msg.getBytes();
        try{
            int maxPacketSize = udpSocket.getSendBufferSize()- 4;
            int totalPackets = (int) Math.ceil((double) messageBytes.length / maxPacketSize);
            System.out.println("TOTAL PACKETS: " + totalPackets + " MAXPACKETSIZE: " + maxPacketSize + " MESSAGEBYTES: "+ messageBytes);
            buffer = Integer.toString(totalPackets).getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, packetAddress, packetPort);
            udpSocket.send(packet);

            //buffer = msg.getBytes();

            for(int i = 0;i < totalPackets; i++) {
                System.out.println(i);
                int offset = i*maxPacketSize;
                int length = Math.min(maxPacketSize, messageBytes.length - offset);
                buffer = new byte[length];
                System.arraycopy(messageBytes, offset, buffer, 0, length);

                packet = new DatagramPacket(buffer, buffer.length, packetAddress, packetPort);
                udpSocket.send(packet);


            }
        }
        catch (Exception e) {

        }



    }

    public static void main(String args[]){
        Client client = new Client(25655,"localhost");
        try{

            client.clientLoop();
        }catch(IOException e){
            e.printStackTrace();
        }




    }
    public void clientLoop() throws IOException{

        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        boolean registerd = false;
        try {
            System.out.println("Geben sie ihren Username ein!");

            username = bufferedReader.readLine();
            System.out.println("Geben sie ihr Passwort ein!");
            password = bufferedReader.readLine();
            System.out.println(username);
            System.out.println(password);

            String userData = username + ";" + password;
            dataOut.writeUTF(userData);


            if (!registerd) {
                while(!registerd){
                    switch (leseNachricht(socket).strip()) {
                        case "REG_OK":
                            System.out.println("Du bist mit dem Server verbunden!");

                            registerd = true;
                            break;


                        case "PASSWORD_INVALID":
                            System.out.println("Passwort falsch bitte neu versuchen!");
                            password = bufferedReader.readLine();
                            userData = username + ";" + password;
                            dataOut.writeUTF(userData);
                            System.out.println(userData);
                            break;


                        case "REG_NEW":
                            System.out.println("Du wurdest als neue Benutzer angemeldet");
                            registerd = true;


                    }
                }
            }
            if (registerd) {


                Thread serverMessageHandler = new Thread(() -> {

                    try {
                        while (true) {

                            serverMesage = leseNachricht(socket).strip();
                            System.out.println("RECEIVED FROM SERVER: " + serverMesage);
                            switch (serverMesage) {
                                case "REQUEST_ACCEPTED":
                                    System.out.println("starting chat!");
                                    serverMesage = leseNachricht(socket);
                                    String chatPartnerPort = serverMesage.split(";")[0];
                                    String chatPartnerIP = serverMesage.split(";")[1];

                                    startChatProtocol(chatPartnerPort,chatPartnerIP);
                                    break;


                                case "CHAT_REQUEST":
                                    System.out.println("Chat Einladung erhalten! y: zum akzeptieren ODER n: zum ablehnen!");
                                    requestReceived = true;

                                    break;

                                case "USER_NOT_FOUND":
                                    System.out.println("Benutzer nicht gefunden, bitte erneut eingeben!");

                                case "REQUEST_DENIED":
                                    System.out.println("ABGELEHNT");

                                default:
                                    System.out.println(serverMesage);
                            }
                        }
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                });

                Thread userInputHandler = new Thread(() -> {

                    try {
                        while (true) {
                            String eingabe[] = bufferedReader.readLine().split(" ");
                            //System.out.println("UR INPUT: "+ eingabe);
                            switch (eingabe[0]) {
                                case "!clients":
                                    dataOut.writeUTF("GET_CLIENTS");
                                    //System.out.println(serverMesage);
                                    break;

                                case "!connect":
                                    //System.out.println("UR INPUT: "+ eingabe[0]);
                                    dataOut.writeUTF(eingabe[1]);

                                    break;

                                case "y":
                                    if (requestReceived) {
                                        udpSocket = new DatagramSocket();
                                        dataOut.writeUTF("REQUEST_ACCEPTED "+ udpSocket.getLocalPort());
                                        System.out.println("Starting chat!");
                                        startChatProtocol();
                                    }
                                    break;

                                case "n":
                                    if (requestReceived) {
                                        dataOut.writeUTF("REQUEST_DENIED");
                                    }
                                    break;

                                default:
                                    //System.out.println("USER INPUT:" + eingabe);
                            }
                            //System.out.println("end");
                        }
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }catch(ArrayIndexOutOfBoundsException e){
                        System.out.print("Gib ein client ein zu dem du connecten willst!");
                    }
                });

                userInputHandler.start();
                serverMessageHandler.start();




            }





        }catch(IOException e){
            e.printStackTrace();
        }


    }

    void setUsername(String username){
        this.username = username;
    }
    String getUsername(){
        return this.username;
    }
    void setPassword(String password){
        this.password = password;
    }
    String getPassword(){
        return this.password;
    }

    String leseNachricht(Socket socket) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        char[] buffer = new char[200];
        int anzahlZeichen = bufferedReader.read(buffer, 0, 200);
        return new String(buffer, 0, anzahlZeichen);
    }

}
