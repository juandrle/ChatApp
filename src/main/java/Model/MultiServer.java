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



        public ClientHandler(Socket clientSocket) throws IOException {

            this.clientSocket = clientSocket;
            //this.clientSockets  = sockets;
            //clientSockets.add(clientSocket);
            //System.out.println(clientSockets);

        }

        @Override
        public void run() {
            try {
                DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
                while (true) {
                    nachricht = dataIn.readUTF();
                    String cmd = nachricht.split(" ")[0];
                    nachricht = nachricht.split(" ")[1];
                    System.out.println("CLIENT MESSAGE: " + cmd + nachricht);
                    switch (cmd) {
                        case "LOGIN" -> {
                            userData = nachricht;
                            username = userData.split(";")[0];
                            password = nachricht.split(";")[1];
                            schreibeNachricht(clientSocket, login(username, password));
                            updateAllClients();

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
                            schreibeNachricht(clientSocket, getClientsConnected());
                            System.out.println(getClientsConnected());
                        }
                        case "REQUEST_ACCEPTED" -> {
                            schreibeNachricht(requestUser, "REQUEST_ACCEPTED");
                            schreibeNachricht(requestUser, nachricht + ";" + wantedSocket.getInetAddress());
                        }
                        case "REQUEST_DENIED" -> {
                            schreibeNachricht(requestUser, "REQUEST_DENIED");
                        }
                        case "EXIT" -> {
                            clients.remove(clientSocket);
                            System.out.println("DISCONNECTED");
                            updateAllClients();
                            clientSocket.close();
                        }
                        default -> {


                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
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
            for (Object i : usernameClientMap.keySet()) {
                if (i != null) clientsConnected += i + " ";
            }
            return clientsConnected;
        }

        void updateAllClients() throws IOException {

            for (ClientHandler x : clients) {
                x.schreibeNachricht(x.clientSocket, getClientsConnected());
            }
        }
    }
}
