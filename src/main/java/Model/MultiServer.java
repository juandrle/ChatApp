package Model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class MultiServer {

    HashMap connected = new HashMap();

    public static void main(String[] args) throws IOException {
        MultiServer multiServer = new MultiServer();
        multiServer.serverLoop();
    }


    void serverLoop() throws IOException{
        int port = 25655;

        ServerSocket serverSocket = new ServerSocket(port);

        while(true){
            Socket client = warteAufAnmeldung(serverSocket);

            Thread clientThread = new ClientHandler(client);
            clientThread.start();
        }





    }



    Socket warteAufAnmeldung(ServerSocket serverSocket) throws IOException{
        Socket socket = serverSocket.accept();

        System.out.print("connected");
        return socket;
    }



    static class ClientHandler extends Thread{
        Socket clientSocket;
        int count = 0;
        boolean isInList = false;
        String userData;

        String username;
        String password;
        String line;
        String nachricht;

        public ClientHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;

        }

        public void run(){
            try{
                DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
                BufferedReader reader = new BufferedReader(new FileReader("src/main/java/resources/userData.csv"));
                BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/resources/userData.csv", true));

                while (true) {
                    nachricht = dataIn.readUTF();

                    switch (count) {
                        case 0 -> {
                            userData = nachricht;
                            username = userData.split(";")[0];
                            while ((line = reader.readLine()) != null) {
                                if (username.equals(line.split(";")[0])) {
                                    isInList = true;
                                    break;
                                }
                            }
                            if (!isInList) {
                                writer.write(username + ";");
                            }
                            count++;
                        }
                        case 1 -> {
                            password = userData.split(";")[1];
                            if (!isInList) {
                                writer.write(password + "\n");
                                schreibeNachricht(clientSocket, "REG_NEW");
                                count++;


                            } else {
                                if (password.equals(line.split(";")[1])) {
                                    schreibeNachricht(clientSocket, "REG_OK");

                                    count++;
                                }
                                else schreibeNachricht(clientSocket, "PASSWORD_INVALID");
                            }
                            writer.close();
                        }
                        default -> {

                        }
                    }
                    System.out.println(nachricht);
                    if (nachricht.equalsIgnoreCase("exit")) {
                        clientSocket.close();
                        System.out.println("Disconnected from the server");
                        break;
                    }


                    System.out.println();
                }

            }catch(IOException e){

            }
        }


        void schreibeNachricht(Socket socket, String nachricht) throws IOException{
            PrintWriter printWriter= new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            printWriter.print(nachricht + "\n");
            printWriter.flush();
        }

    }
    
    
}
