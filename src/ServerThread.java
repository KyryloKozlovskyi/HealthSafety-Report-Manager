import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

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
        loadUsers("users.txt");
    }

    // Method to handle user registration. Server side conversation
    private void register() throws IOException, ClassNotFoundException {
        try {
            // Conversation with client
            System.out.println("CONSOLE DEBUG Registering user");
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
            // Verify data
            //sendMessage("You entered the following data: \nName: " + name + "\nEmployee ID: " + employeeId + "\nEmail: " + email + "\nPassword: " + password + "\nDepartment Name: " + departmentName + "\nRole: " + role);
            if (sharedObject.addUser(name, employeeId, email, password, departmentName, role)) {
                writeSharedObjectToFile("users.txt");
                sendMessage("User successfully registered!");
            } else {
                sendMessage("User already exists! Try again.");
            }
            //showAllUsers(); // Debug
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during registration: " + e.getMessage());
        }
    }

    // Method to handle user login
    private void login() throws IOException, ClassNotFoundException {
        sendMessage("Enter email: ");
        String email = (String) in.readObject();
        sendMessage("Enter password: ");
        String password = (String) in.readObject();
        if (sharedObject.checkCredentials(email, password)) {
            sendMessage("Login successful! Welcome, " + email);
        } else {
            sendMessage("Invalid email or password. Please try again.");
        }
    }

    // Method to send a message to the client
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

    // Method to load users from file to shared object
    private void loadUsers(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userFields = line.split("~");
                if (userFields.length == 6) {
                    String name = userFields[0];
                    String employeeId = userFields[1];
                    String email = userFields[2];
                    String password = userFields[3];
                    String departmentName = userFields[4];
                    String role = userFields[5];
                    sharedObject.addUser(name, employeeId, email, password, departmentName, role);
                } else {
                    System.err.println("Invalid user data format in file: " + fileName);
                }
            }
            //System.out.println("DEBUG All users loaded from " + fileName);
            //showAllUsers();
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + fileName);
            e.printStackTrace();
        }
    }

    // Method to show all users DEBUG
    private void showAllUsers() throws IOException {
        LinkedList<User> users = sharedObject.getAllUsers();
        StringBuilder userInfo = new StringBuilder("DEBUG Current Registered Users:\n");
        for (User user : users) {
            userInfo.append(user.getName()).append(", ").append(user.getEmployeeId()).append(", ").append(user.getEmail()).append("\n");
        }
        //sendMessage(userInfo.toString());
        System.out.println(userInfo);
    }

    // Method to write data to a file
    private void writeSharedObjectToFile(String fileName) {
        LinkedList<User> users = sharedObject.getAllUsers();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (User user : users) {
                writer.write(user.getName() + "~" + user.getEmployeeId() + "~" + user.getEmail() + "~" + user.getPassword() + "~" + user.getDepartmentName() + "~" + user.getRole());
                writer.newLine();
            }
            System.out.println("Data successfully written to " + fileName);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to file: " + e.getMessage());
            e.printStackTrace();
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
                switch (response.trim().toLowerCase()) {
                    case "1":
                        register();
                        break;
                    case "2":
                        login();
                        break;
                }
            } while (!response.equalsIgnoreCase("0"));
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