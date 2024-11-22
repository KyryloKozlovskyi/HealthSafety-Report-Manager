import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 9090;
    private Scanner input;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket connection;
    private String choice;

    // Constructor
    public Client() {
        input = new Scanner(System.in);
    }

    // Method to send message to server
    private void sendMessage(String message) {
        try {
            out.writeObject(message);
            out.flush();
            System.out.println("client>" + message);
        } catch (IOException ioException) {
            System.err.println("Error sending message: " + ioException.getMessage());
            ioException.printStackTrace();
        }
    }

    // Method for user registration
    private void register() throws IOException, ClassNotFoundException {
        String response;
        try {
            // Conversation with server
            System.out.println("DEBUG Registering user");
            // Name
            response = (String) in.readObject();
            System.out.println(response);
            response = input.nextLine();
            sendMessage(response);
            // Employee ID
            response = (String) in.readObject();
            System.out.println(response);
            response = input.nextLine();
            sendMessage(response);
            // Email
            response = (String) in.readObject();
            System.out.println(response);
            response = input.nextLine();
            sendMessage(response);
            // Password
            response = (String) in.readObject();
            System.out.println(response);
            response = input.nextLine();
            sendMessage(response);
            // Department Name
            response = (String) in.readObject();
            System.out.println(response);
            response = input.nextLine();
            sendMessage(response);
            // Role
            response = (String) in.readObject();
            System.out.println(response);
            response = input.nextLine();
            sendMessage(response);
            // Verify
            response = (String) in.readObject();
            System.out.println(response);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during registration: " + e.getMessage());
        }
    }

    // Method to run the client
    public void run() {
        try {
            connection = new Socket(SERVER_ADDRESS, SERVER_PORT); // Create a socket to connect to the server
            out = new ObjectOutputStream(connection.getOutputStream()); // Create output stream to send data to the server
            in = new ObjectInputStream(connection.getInputStream()); // Create input stream to receive data from the server
            System.out.println("DEBUG Client: Client is running on port: " + connection.getLocalPort());
            // Client -> Server conversation
            String response;
            // Receive and print the welcome message from the server
            response = (String) in.readObject();
            System.out.println(response);
            // Menu loop
            do {
                response = (String) in.readObject();
                System.out.println(response);
                response = input.nextLine();
                sendMessage(response);
                choice = response;
            } while (!response.equalsIgnoreCase("1") && !response.equalsIgnoreCase("2") && !response.equalsIgnoreCase("0"));
            if (choice.equalsIgnoreCase("1")) {
                register();
            }
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
            }
        }
    }

    // Main method to start the client
    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}