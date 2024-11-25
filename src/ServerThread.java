import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

public class ServerThread extends Thread {
    private Socket connection;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String choice;
    private SharedObject sharedObject;
    private boolean loggedIn;
    private String loggedInUser;

    // Constructor
    public ServerThread(Socket socket) {
        this.connection = socket;
        sharedObject = new SharedObject();
        loadUsers("users.txt");
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

    // Method to generate a random number
    private int generateRandomNumber() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        return random.nextInt(100000) + 1;
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
                writeUsersToFile("users.txt");
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

    // Method to load users from file to the shared object
    private void loadUsers(String fileName) {
        User user;
        // Read data from file
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line
                String[] userFields = line.split("~");
                // Add user to the shared object
                if (userFields.length == 6) {
                    String name = userFields[0];
                    String employeeId = userFields[1];
                    String email = userFields[2];
                    String password = userFields[3];
                    String departmentName = userFields[4];
                    String role = userFields[5];
                    user = new User(name, employeeId, email, password, departmentName, role);
                    sharedObject.addUser(user);
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

    // Method to write data to a file
    private void writeUsersToFile(String fileName) {
        // Get all users and reports from the shared object
        LinkedList<User> users = sharedObject.getAllUsers();
        // Open a file to write data to
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write data to the file
            for (User user : users) {
                writer.write(user.getName() + "~" + user.getEmployeeId() + "~" + user.getEmail() + "~" + user.getPassword() + "~" + user.getDepartmentName() + "~" + user.getRole());
                writer.newLine();
            }
            System.out.println("User Data successfully written to " + fileName);
        } catch (IOException e) {
            System.err.println("An error occurred while writing users to file: " + e.getMessage());
            e.printStackTrace();
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
            Report report = new Report(reportType, String.valueOf(generateRandomNumber()), getCurrentDateTime(), sharedObject.getUserId(loggedInUser), ReportStatus.OPEN, "NONE");
            if (sharedObject.addReport(report)) {
                writeReportsToFile("reports.txt");
                sendMessage("Report successfully created!");
            } else {
                sendMessage("Report already exists! Try again.");
            }
        } catch (IOException | ClassNotFoundException | NumberFormatException e) {
            sendMessage("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to write reports data to a file
    private void writeReportsToFile(String fileName) {
        // Get all users and reports from the shared object
        LinkedList<Report> reports = sharedObject.getAllReports();
        // Open a file to write data to
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write data to the file
            for (Report report : reports) {
                writer.write(report.getReportType() + "~" + report.getReportId() + "~" + report.getDate() + "~" + report.getEmployeeId() + "~" + report.getStatus() + "~" + report.getAssignedEmployee());
                writer.newLine();
            }
            System.out.println("User Data successfully written to " + fileName);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to file: " + e.getMessage());
            e.printStackTrace();
        }
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