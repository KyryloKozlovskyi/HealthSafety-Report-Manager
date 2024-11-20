import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "localhost"; //
    private static final int SERVER_PORT = 9090;

    // Main method to start the client
    public static void main(String[] args) {
        // Create a socket to connect to the server
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            System.out.println("Client is running");

            // Send a request to the server
            out.writeObject("Hello from Client");
            out.flush();

            // Read response from the server
            Object response = in.readObject();
            System.out.println("Received response from server: " + response);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}