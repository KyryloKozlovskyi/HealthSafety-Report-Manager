import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 9090;

    // Main method to start the server
    public static void main(String[] args) {
        SharedObject sharedObject = new SharedObject(); // Shared object to store users and reports
        // Create a server socket to listen for client connections
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            // Wait for a client connection
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ServerThread handler = new ServerThread(clientSocket, sharedObject); // Create and start a new thread for each client connection
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}