package ChatApplication;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static Map<String, Socket> clients = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server is running on port " + PORT);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new ClientHandler(clientSocket).start();
        }
    }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        public ClientHandler(Socket socket) {
            this.socket = socket;

        }
        @Override public void run() {
            try {

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println("Welcome! Please register by sending 'REGISTER <username>'.");
                String input;
                while ((input = in.readLine()) != null) {
                    if (input.startsWith("REGISTER ")) {
                        String[] parts = input.split(" ");

                        if (parts.length == 2){
                            String newUsername = parts[1];
                            if (!clients.containsKey(newUsername)) {
                                username = newUsername;
                                clients.put(username, socket);
                                out.println("WELCOME " + username);
                                broadcast(username + " has joined the chat.");
                            }
                            else {
                                out.println("Username already taken. Please choose a different one.");
                            }
                        }
                }
                else if (input.equals("LIST")) {
                    out.println("Connected clients: " + String.join(", ", clients.keySet()));
                }
                else if (input.equals("LISTENING")) {
                     //Client waits for input from the server
                }
                else if (input.startsWith("SEND ")) {
                        String[] parts = input.split(" ", 3);
                    if (parts.length == 3) {
                        String toUsername = parts[1];
                        String message = parts[2];
                        Socket toSocket = clients.get(toUsername);
                        if (toSocket != null) { PrintWriter toOut = new PrintWriter(toSocket.getOutputStream(), true);
                            toOut.println("Message from " + username + ": " + message);
                        }
                        else {
                            out.println("Client '" + toUsername + "' is not connected.");
                        }
                    }
                }
                else {
                    out.println("Invalid command. Use REGISTER, LIST, LISTENING, or SEND.");
                    }
                }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (username != null) {
                clients.remove(username);
                broadcast(username + " has left the chat.");
        }
        try {
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        }
        }
    }

    private static void broadcast(String message) {
        for (Socket clientSocket : clients.values()) {
            try {
                PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
                clientOut.println(message);
    }
            catch (IOException e) {
                e.printStackTrace();
    }
        }
    }
}