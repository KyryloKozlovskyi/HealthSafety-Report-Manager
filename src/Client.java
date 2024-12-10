import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    // Server address and port
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 9090;
    // Input scanner and IO streams
    private final Scanner input;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket connection; // Stores the connection to the server
    private boolean loggedIn; // Flag to check if user is logged in

    // Constructor
    public Client() {
        input = new Scanner(System.in);
    }

    // Sends a message to the server
    private void sendMessage(String message) throws IOException {
        try {
            // Send message to server
            out.writeObject(message);
            out.flush();
            System.out.println("client>" + message);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Handles client side conversation for user registration
    private void register() throws IOException, ClassNotFoundException {
        String response;
        try {
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

    // Handles client side conversation for user login
    private void login() throws IOException, ClassNotFoundException {
        String response;
        try {
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
            // Verify
            response = (String) in.readObject();
            System.out.println(response);
            // Log-in Flag
            response = (String) in.readObject();
            loggedIn = Boolean.parseBoolean(response);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during log in: " + e.getMessage());
        }
    }

    // Handles client side conversation for creating a report
    private void createReport() throws IOException, ClassNotFoundException, NumberFormatException {
        String response;
        try {
            // Report Type
            response = (String) in.readObject();
            System.out.println(response);
            response = input.nextLine();
            sendMessage(response);
            // Verify
            response = (String) in.readObject();
            System.out.println(response);
        } catch (IOException | ClassNotFoundException | NumberFormatException e) {
            System.err.println("Error during report creation: " + e.getMessage());
        }
    }

    // Handles displaying all reports on the client side
    private void showAllReports() throws IOException, ClassNotFoundException {
        String response;
        try {
            // Verify
            response = (String) in.readObject();
            System.out.println(response);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during report(all) output: " + e.getMessage());
        }
    }

    // Handles client side conversation for assigning an employee to a report and updating the status
    public void assignEmployee() throws IOException, ClassNotFoundException {
        String response;
        try {
            // Report ID
            response = (String) in.readObject();
            System.out.println(response);
            String reportId = input.nextLine();
            sendMessage(reportId);
            // Check server response
            response = (String) in.readObject();
            if (response.equals("Report not found! Try again.")) {
                System.out.println(response);
                return;
            }
            // Employee ID
            System.out.println(response);
            String employeeId = input.nextLine();
            sendMessage(employeeId);
            // Check server response
            response = (String) in.readObject();
            if (response.equals("Employee not found! Try again.")) {
                System.out.println(response);
                return;
            }
            // Report status
            System.out.println(response);
            String statusChoice = input.nextLine();
            sendMessage(statusChoice);
            // Check server response
            response = (String) in.readObject();
            if (response.equals("Invalid choice. Status not updated.")) {
                System.out.println(response);
                return;
            }
            // Verify
            response = (String) in.readObject();
            System.out.println(response);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during assigned employee/status update: " + e.getMessage());
        }
    }

    // Handles client side conversation for viewing assigned reports
    private void showAssignedReports() throws IOException, ClassNotFoundException, NumberFormatException {
        String response;
        try {
            // Verify
            response = (String) in.readObject();
            System.out.println(response);
        } catch (IOException | ClassNotFoundException | NumberFormatException e) {
            System.err.println("Error during assigned employee reports output: " + e.getMessage());
        }
    }

    // Handles client side conversation for updating password
    private void updatePassword() throws IOException, ClassNotFoundException {
        String response;
        try {
            // Password
            response = (String) in.readObject();
            System.out.println(response);
            response = input.nextLine();
            sendMessage(response);
            // Verify
            response = (String) in.readObject();
            System.out.println(response);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during updating the password: " + e.getMessage());
        }
    }

    // Runs the client
    public void run() {
        try {
            // Connect to the server and create steams
            connection = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
            String response;
            // Receive and print the welcome message from the server
            response = (String) in.readObject();
            System.out.println(response);
            // Login menu loop
            do {
                // Login menu
                response = (String) in.readObject();
                System.out.println(response);
                response = input.nextLine();
                sendMessage(response);
                switch (response.trim().toLowerCase()) {
                    case "1":
                        register();
                        break;
                    case "2":
                        login();
                        break;
                }
                if (loggedIn) {
                    // Main menu loop
                    do {
                        // Main menu
                        response = (String) in.readObject();
                        System.out.println(response);
                        response = input.nextLine();
                        sendMessage(response);
                        switch (response.trim().toLowerCase()) {
                            case "1":
                                createReport();
                                break;
                            case "2":
                                showAllReports();
                                break;
                            case "3":
                                assignEmployee();
                                break;
                            case "4":
                                showAssignedReports();
                                break;
                            case "5":
                                updatePassword();
                                break;
                        }
                    } while (!response.equalsIgnoreCase("0"));
                }
            } while (!response.equalsIgnoreCase("0"));
        } catch (IOException ioException) {
            System.err.println("IO Exception Client Run: " + ioException.getMessage());
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