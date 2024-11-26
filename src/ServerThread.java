import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

public class ServerThread extends Thread {
    private final Socket connection;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final SharedObject sharedObject;
    private boolean loggedIn;
    private String loggedInUser;

    // Constructor
    public ServerThread(Socket socket, SharedObject sharedObject) {
        this.connection = socket;
        this.sharedObject = sharedObject; // Create shared object inside the server code
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

    // Method to get the current date and time
    private Date getCurrentDateTime() {
        return new Date();
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

    // User related methods
    // Method to handle user registration. Server side conversation
    private void register() throws IOException, ClassNotFoundException {
        try {
            // Conversation with client
            //System.out.println("CONSOLE DEBUG Registering user");
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
            User user = new User(name, employeeId, email, password, departmentName, role);
            if (sharedObject.addUser(user)) {
                sharedObject.writeUsersToFile("users.txt");
                sendMessage("User successfully registered!");
            } else {
                sendMessage("User already exists! Try again.");
            }
            //showAllUsers(); // Debug
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during registration: " + e.getMessage());
        }
    }

    // Method to handle user login returns true if successful
    private boolean login() throws IOException, ClassNotFoundException {
        // Get credentials from the user
        sendMessage("Enter email: ");
        String email = (String) in.readObject();
        sendMessage("Enter password: ");
        String password = (String) in.readObject();
        // Compare credentials with the ones in the shared object
        if (sharedObject.checkCredentials(email, password)) {
            sendMessage("Login successful! Welcome, " + email);
            loggedInUser = email;
            return true;
        } else {
            sendMessage("Invalid email or password. Please try again.");
            return false;
        }
    }

    // Report related methods
    // Method to handle a report creation. Server side conversation
    private void createReport() throws IOException, ClassNotFoundException {
        try {
            // Get Report Type from the user
            sendMessage("Choose report type: 1 - Accident, 2 - Risk");
            int reportTypeChoice = Integer.parseInt((String) in.readObject());
            // Validate and set Report Type
            ReportType reportType;
            switch (reportTypeChoice) {
                case 1:
                    reportType = ReportType.ACCIDENT;
                    break;
                case 2:
                    reportType = ReportType.RISK;
                    break;
                default:
                    sendMessage("Invalid report type. Please enter 1 or 2.");
                    return;
            }
            // Create a new report and add it to the shared object
            Report report = new Report(reportType, String.valueOf(sharedObject.getAllReports().size() + 1), getCurrentDateTime(), sharedObject.getUserId(loggedInUser), ReportStatus.OPEN, "NONE");
            if (sharedObject.addReport(report)) {
                sharedObject.writeReportsToFile("reports.txt");
                sendMessage("Report successfully created!");
            } else {
                sendMessage("Report already exists! Try again.");
            }
        } catch (IOException | ClassNotFoundException | NumberFormatException e) {
            sendMessage("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to show all users DEBUG
    private void showAllReports() throws IOException {
        LinkedList<Report> reports = sharedObject.getAllReports();
        StringBuilder reportInfo = new StringBuilder("DEBUG Current Registered Reports:\n");
        for (Report report : reports) {
            reportInfo.append(report.getReportType()).append(", ").append(report.getReportId()).append(", ").append(report.getDate()).append(", ").append(report.getEmployeeId()).append(", ").append(report.getStatus()).append(", ").append(report.getAssignedEmployee()).append("\n");
        }
        sendMessage(reportInfo.toString());
        //System.out.println(reportInfo);
    }

    // Override run method to handle client requests. Runs the thread.
    @Override
    public void run() {
        try {
            // Create streams to send and receive data from the client
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

            //System.out.println("DEBUG Thread: Handler thread is running for client: " + connection.getInetAddress());
            // Server -> Client conversation
            // Send welcome message to client
            sendMessage("Welcome to the Health and Safety Report Manager!");
            String response;
            // Log in menu loop
            do {
                sendMessage("Choose an option: 1 - Register, 2 - Log in, 0 - Exit");
                response = (String) in.readObject();
                switch (response.trim().toLowerCase()) {
                    case "1":
                        register();
                        break;
                    case "2":
                        loggedIn = login();
                        sendMessage(String.valueOf(loggedIn));
                        break;
                }
                // If logged in, show menu
                if (loggedIn) {
                    do {
                        sendMessage("Choose an option: 1 - Create a report, 2 - Retrieve all reports, 3 - Assign the report, 4 - View assigned reports, 5 - Update password, 0 - Exit");
                        response = (String) in.readObject();
                        switch (response.trim().toLowerCase()) {
                            case "1":
                                createReport();
                                break;
                            case "2":
                                showAllReports();
                                break;
                        }
                    } while (!response.equalsIgnoreCase("0"));
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