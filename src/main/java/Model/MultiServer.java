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
        int port = 25655;

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



    class ClientHandler extends Thread{
        Socket clientSocket;

        boolean isInList = false;
        String userData;
        String username;
        String password;
        String line;
        String nachricht = null;
        List<Socket> clientSockets;
        String clientsConnected = "";


        public int i =0;
        public ClientHandler(Socket clientSocket) throws IOException {

            this.clientSocket = clientSocket;
            //this.clientSockets  = sockets;
            //clientSockets.add(clientSocket);
            //System.out.println(clientSockets);
            i++;

        }

        @Override
        public void run(){
            int count = 0;

            try{
                DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
                BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/Accounts.csv"));
                BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/Accounts.csv", true));

                while(true) {
                    nachricht = dataIn.readUTF();
                    System.out.println("CLIENT MESSAGE: " + nachricht);
                    switch (count) {
                        case 0 -> {
                            userData = nachricht;
                            username = userData.split(";")[0];
                            password = userData.split(";")[1];
                            while ((line = reader.readLine()) != null) {
                                if (username.equals(line.split(";")[0])) {
                                    isInList = true;
                                    break;
                                }
                            }
                            if (!isInList) {
                                writer.write(username + ";" + password + "\n");
                                System.out.println("gesendet regnew"+ username + password);
                                schreibeNachricht(clientSocket, "REG_NEW");

                                writer.close();

                                usernameClientMap.put(username, this);

                            }else{
                                if (password.equals(line.split(";")[1])) {
                                    schreibeNachricht(clientSocket, "REG_OK");
                                    writer.close();
                                    usernameClientMap.put(username, this);
                                    count++;

                                }else{

                                    reader = new BufferedReader(new FileReader("src/main/resources/Accounts.csv"));
                                    schreibeNachricht(clientSocket, "PASSWORD_INVALID");


                                }
                            }




                        } case 2 ->{


                            if(clientsConnected.contains(nachricht)){

                                wantedSocket = usernameClientMap.get(nachricht).clientSocket;
                                requestUser = clientSocket;

                                schreibeNachricht(wantedSocket, "CHAT_REQUEST");
                                System.out.println("SEND  REQUEST");
                                count++;

                            }else{
                                schreibeNachricht(clientSocket, "USER_NOT_FOUND");
                            }

                        } case 3 ->{




                        }
                        default ->{
                            if(nachricht.equals("GET_CLIENTS")) {
                                for (Object i : usernameClientMap.keySet()) {
                                    if (i != null) clientsConnected += i + " ";


                                }
                                schreibeNachricht(clientSocket, clientsConnected);
                                count = 2;
                                System.out.println("default");
                            }



                            if(nachricht.split(" ")[0].equals("REQUEST_ACCEPTED")){
                                schreibeNachricht(requestUser, "REQUEST_ACCEPTED");
                                schreibeNachricht(requestUser,nachricht.split(" ")[1]+ ";" + wantedSocket.getInetAddress());

                            }else if(nachricht.equals("REQUEST_DENIED")){

                                schreibeNachricht(requestUser,"REQUEST_DENIED");

                            }


                        }
                    }



                    System.out.println(count);
                    if (nachricht.equalsIgnoreCase("exit")) {
                        clientSocket.close();
                        System.out.println("Disconnected from the server");
                        break;
                    }



                }

            }catch(IOException e){
                e.printStackTrace();
            }
        }


        void schreibeNachricht(Socket socket, String nachricht) throws IOException{
            PrintWriter printWriter= new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            printWriter.print(nachricht + "\n");
            printWriter.flush();
        }

    }


}
