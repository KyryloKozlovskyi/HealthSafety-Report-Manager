import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket connection;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // Constructor
    public ServerThread(Socket socket) {
        this.connection = socket;
    }

    // Method to send message to client
    private void sendMessage(String message) {
        try {
            out.writeObject(message);
            out.flush();
            System.out.println("server>" + message);
        } catch (IOException ioException) {
            System.err.println("Error sending message: " + ioException.getMessage());
            ioException.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Create streams to send and receive data from the client
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

            System.out.println("DEBUG Thread: Handler thread is running for client: " + connection.getInetAddress());
            // Server -> Client conversation
            // Send welcome message to client
            sendMessage("Welcome to the Health and Safety Report Manager!");
            String response;
            // Menu loop
            do {
                sendMessage("Choose an option: 1 - Register, 2 - Log in, 0 - Exit");
                response = (String) in.readObject();
            } while (!response.equalsIgnoreCase("1") && !response.equalsIgnoreCase("2") && !response.equalsIgnoreCase("0"));
        } catch (IOException ioException) {
            System.err.println("IO Exception: " + ioException.getMessage());
            ioException.printStackTrace();
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Class not found: " + classNotFoundException.getMessage());
            classNotFoundException.printStackTrace();
        } finally {
            try {
                // Close the socket and streams
                in.close();
                out.close();
                connection.close();
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}