import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket connection;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String choice;
    private SharedObject sharedObject;

    // Constructor
    public ServerThread(Socket socket) {
        this.connection = socket;
        sharedObject = new SharedObject();
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

    // Method to handle user registration on server side
    private void register() throws IOException, ClassNotFoundException {
        try {
            // Conversation with client
            System.out.println("DEBUG Registering user");
            sendMessage("Enter name: ");
            String name = (String) in.readObject();
            sendMessage("Enter employee ID: ");
            String employeeId = (String) in.readObject();
            sendMessage("Enter email: ");
            String email = (String) in.readObject();
            sendMessage("Enter password: ");
            String password = (String) in.readObject();
            sendMessage("Enter department name: ");
            String departmentName = (String) in.readObject();
            sendMessage("Enter role: ");
            String role = (String) in.readObject();
            // Verify
            sendMessage("You entered the following data: \nName: " + name + "\nEmployee ID: " + employeeId + "\nEmail: " + email + "\nPassword: " + password + "\nDepartment Name: " + departmentName + "\nRole: " + role);
            sharedObject.addUser(name, employeeId, email, password, departmentName, role);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during registration: " + e.getMessage());
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
                e.printStackTrace();
            }
        }
    }
}