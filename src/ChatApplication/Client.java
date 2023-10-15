package ChatApplication;
import java.io.*;
import java.net.*;

public class Client {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {

        try {
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            String serverResponse;
            while ((serverResponse = in.readLine()) != null) {
                System.out.println(serverResponse);
                if (serverResponse.startsWith("WELCOME ")) {
                    while (true) {
                        String clientInput = consoleInput.readLine();
                        out.println(clientInput);
                        if (clientInput.equals("LISTENING")) {
                            String serverInput;
                            while ((serverInput = in.readLine()) != null) {
                                System.out.println(serverInput);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}