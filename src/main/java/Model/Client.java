package Model;

import java.io.*;
import java.net.Socket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;


public class Client {
    static Socket socket;
    int port;
    String address;
    
    String username;
    String password;

    Client(int port, String address){
        this.port = port;
        this.address = address;
        try {
            socket = new Socket(address,port);
        }catch(IOException e){
            e.printStackTrace();
        }


    }

    public static void main(String args[]){
        Client client = new Client(5556,"localhost");
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
            while (true) {
                if (!registerd) {

                    switch (leseNachricht(socket).strip()) {
                        case "REG_OK":
                            System.out.println("Du bist mit dem Server verbunden!");
                            System.out.println("Hier ist eine Liste mit clients die online sind!");
                            registerd = true;

                        case "PASSWORD_INVALID":
                            System.out.println("Passwort falsch bitte neu versuchen!");
                            password = bufferedReader.readLine();
                            userData = username + ";" + password;
                            dataOut.writeUTF(userData);
                            break;

                        case "REG_NEW":
                            System.out.println("Du wurdest als neue Benutzer angemeldet");
                            registerd = true;


                    }
                } else if (registerd) {
                    String connectedClients = leseNachricht(socket).strip();
                    System.out.println(connectedClients);
                    System.out.println("Mit welchem Client möchtests du verbinden");
                }


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
