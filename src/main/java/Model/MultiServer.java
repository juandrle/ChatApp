package Model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MultiServer {

    // HashMap<String, Integer> connected = new HashMap();
    Socket wantedSocket;
    Socket requestUser;
    Map<String, ClientHandler> usernameClientMap = new HashMap<>();
    List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        MultiServer multiServer = new MultiServer();
        multiServer.serverLoop();
    }

    void serverLoop() throws IOException{
        int port = 25656;

        ServerSocket serverSocket = new ServerSocket(port);

        while(true){
            Socket client = warteAufAnmeldung(serverSocket);

            //Thread clientThread = new ClientHandler(client, connected, clientSockets);
            ClientHandler clientHandler = new ClientHandler(client);
            clients.add(clientHandler);
            clientHandler.start();
        }

    }


    Socket warteAufAnmeldung(ServerSocket serverSocket) throws IOException{
        Socket socket = serverSocket.accept();
        System.out.print(socket.getInetAddress().toString()+ " "+ socket.getPort() + " connected");
        return socket;
    }



    class ClientHandler extends Thread {
        Socket clientSocket;
        boolean isInList = false;
        String userData;
        String username;
        String password;
        String line;
        String nachricht = null;
        boolean connected = true;



        public ClientHandler(Socket clientSocket) throws IOException {

            this.clientSocket = clientSocket;
            //this.clientSockets  = sockets;
            //clientSockets.add(clientSocket);
            //System.out.println(clientSockets);
            getClientsConnected();
        }

        @Override
        public void run() {
            try {
                DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
                while (connected) {
                    String cmd;
                    nachricht = dataIn.readUTF();

                        cmd = nachricht.split(" ")[0];
                        if (cmd.equals("LOGIN") || cmd.equals("CONNECT")) {
                            nachricht = nachricht.split(" ")[1];
                        }
                    //cmd = nachricht.trim();
                    System.out.println("CLIENT MESSAGE: " + cmd + nachricht);
                    switch (cmd) {
                        case "LOGIN" -> {
                            userData = nachricht;
                            username = userData.split(";")[0];
                            password = nachricht.split(";")[1];
                            schreibeNachricht(clientSocket, login(username, password));

                        }
                        case "CONNECT" -> {
                            if (getClientsConnected().contains(nachricht)) {
                                wantedSocket = usernameClientMap.get(nachricht).clientSocket;
                                requestUser = clientSocket;

                                schreibeNachricht(wantedSocket, "CHAT_REQUEST");
                                System.out.println("SEND  REQUEST");

                            } else {
                                System.out.println("USER NOT FOUND");
                                schreibeNachricht(clientSocket, "USER_NOT_FOUND");
                            }
                        }
                        case "GET_CLIENTS" -> {
                            updateAllClients();
                            System.out.println(getClientsConnected());
                        }
                        case "REQUEST_ACCEPTED" -> {
                            System.out.println(nachricht + ";" + wantedSocket.getInetAddress());

                            schreibeNachricht(requestUser,  nachricht + ";" + wantedSocket.getInetAddress());
                        }
                        case "REQUEST_DENIED" -> {
                            schreibeNachricht(requestUser, "REQUEST_DENIED");
                        }
                        case "EXIT" -> {
                            System.out.println("DISCONNECTED");
                            usernameClientMap.remove(username);
                            updateAllClients();
                            schreibeNachricht(clientSocket, "EXIT_SUCCESSFUL");
                            Thread.sleep(2000);
                            clientSocket.close();
                            connected = false;

                        }
                        default -> {


                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        void schreibeNachricht(Socket socket, String nachricht) throws IOException {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            printWriter.print(nachricht + "\n");
            printWriter.flush();
        }

        public String login(String username, String password) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/Accounts.csv"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/Accounts.csv", true));


            while ((line = reader.readLine()) != null) {
                if (username.equals(line.split(";")[0])) {
                    isInList = true;
                    break;
                }
            }
            if (usernameClientMap.containsKey(username))
                return "ALREADY_ONLINE";
            if (!isInList) {
                writer.write(username + ";" + password + "\n");
                System.out.println("gesendet regnew" + username + password);
                writer.close();
                usernameClientMap.put(username, this);
                return "REG_NEW";


            } else if (isInList) {
                if (password.equals(line.split(";")[1])) {
                    writer.close();
                    usernameClientMap.put(username, this);
                    return "REG_OK";


                } else {
                    reader = new BufferedReader(new FileReader("src/main/resources/Accounts.csv"));
                    return "PASSWORD_INVALID";

                }
            }
            return "FEHLER";
        }

        String getClientsConnected() {
            String clientsConnected = "CLIENT_LIST ";
            for (String i : usernameClientMap.keySet()) {
                if (i != null && !clientsConnected.contains(i)) clientsConnected += i + " ";
            }
            return clientsConnected;
        }

        void updateAllClients() throws IOException {

            for (ClientHandler x : usernameClientMap.values()) {
                //if (username.equals(x.username)) continue;
                x.schreibeNachricht(x.clientSocket, getClientsConnected());
            }
        }
    }
}
