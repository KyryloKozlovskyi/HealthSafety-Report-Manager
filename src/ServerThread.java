import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket connection;
    private Object response;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // Constructor
    public ServerThread(Socket socket) {
        this.connection = socket;
    }

    // Run method to handle client requests
    @Override
    public void run() {
        try {
            // Initialize ObjectOutputStream and ObjectInputStream
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

            // Print client connection information
            System.out.println("Handler thread is running for client: " + connection.getInetAddress());

            // Send a request to the client
            out.writeObject("Hello from Server");
            out.flush(); // Flush the output stream to send data

            // Read response from the client
            response = in.readObject();
            System.out.println("Received response from client: " + response);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // Closing connection and streams
            try {
                in.close();
                out.close();
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

