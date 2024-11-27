import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;

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
        this.sharedObject = sharedObject;
    }

    // Get current date and time
    private Date getCurrentDateTime() {
        return new Date();
    }

    // Sends a message to the client
    private void sendMessage(String message) throws IOException {
        try {
            out.writeObject(message);
            out.flush();
            System.out.println("server>" + message);
        } catch (IOException ioException) {
            System.err.println("Error sending message: " + ioException.getMessage());
            ioException.printStackTrace();
        }
    }

    // Handle registration process on the server side
    private void register() throws IOException, ClassNotFoundException {
        try {
            // Get user data from the client
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
            User user = new User(name, employeeId, email, password, departmentName, role);
            // Add user to the shared object and write to file
            if (sharedObject.addUser(user)) {
                sharedObject.writeUsersToFile("users.txt");
                sendMessage("User successfully registered!");
            } else {
                sendMessage("User already exists! Try again.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during registration: " + e.getMessage());
        }
    }

    // Handles login process on the server side
    private boolean login() throws IOException, ClassNotFoundException {
        try {
            // Get user credentials from the client
            sendMessage("Enter email: ");
            String email = (String) in.readObject();
            sendMessage("Enter password: ");
            String password = (String) in.readObject();
            // Compare credentials with the ones in the shared object
            if (sharedObject.checkCredentials(email, password)) {
                sendMessage("Login successful! Welcome, " + email);
                loggedInUser = email; // Store the logged-in user email
                return true;
            } else {
                sendMessage("Invalid email or password. Please try again.");
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during log in: " + e.getMessage());
            return false;
        }
    }

    // Handles report creation process on the server side
    private void createReport() throws IOException, ClassNotFoundException, NumberFormatException {
        try {
            // Get the report type from the user
            sendMessage("Choose report type: 1 - Accident, 2 - Risk");
            int reportTypeChoice = Integer.parseInt((String) in.readObject());
            // Validate and set report Type
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
            sendMessage("Error during report creation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Handles displaying all reports on the server side
    private void showAllReports() throws IOException {
        try {
            LinkedList<Report> reports = sharedObject.getAllReports();
            StringBuilder reportInfo = new StringBuilder("Current Registered Reports:\n");
            for (Report report : reports) {
                reportInfo.append(report.getReportType()).append(", ").append(report.getReportId()).append(", ").append(report.getDate()).append(", ").append(report.getEmployeeId()).append(", ").append(report.getStatus()).append(", ").append(report.getAssignedEmployee()).append("\n");
            }
            sendMessage(reportInfo.toString());
        } catch (IOException e) {
            System.err.println("Error during report(all) output: " + e.getMessage());
        }
    }

    // Handles assigning a report to an employee and updating the report status on the server side
    public void assignEmployee() throws IOException, ClassNotFoundException {
        try {
            // Get report ID
            sendMessage("Enter report ID: ");
            String reportId = (String) in.readObject();
            Report report = sharedObject.getReportById(reportId);
            if (report == null) {
                sendMessage("Report not found! Try again.");
                return;
            }
            // Get employee ID
            sendMessage("Enter employee ID: ");
            String employeeId = (String) in.readObject();
            if (sharedObject.isEmployeeIdUnique(employeeId)) {
                sendMessage("Employee not found! Try again.");
                return;
            }
            // Get report status
            sendMessage("Enter report status: 1 - OPEN, 2 - ASSIGNED, 3 - CLOSED");
            String statusChoice = (String) in.readObject();
            switch (statusChoice) {
                case "1":
                    report.setStatus(ReportStatus.OPEN);
                    sendMessage("Report status set to OPEN.");
                    break;
                case "2":
                    report.setStatus(ReportStatus.ASSIGNED);
                    sendMessage("Report status set to ASSIGNED.");
                    break;
                case "3":
                    report.setStatus(ReportStatus.CLOSED);
                    sendMessage("Report status set to CLOSED.");
                    break;
                default:
                    sendMessage("Invalid choice. Status not updated.");
                    return;
            }
            // Assign report to the employee
            report.setAssignedEmployee(employeeId);
            sharedObject.writeReportsToFile("reports.txt");
            sendMessage("Employee assigned and report status updated successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during assigned employee/status update: " + e.getMessage());
        }
    }

    // Handles displaying all assigned reports on the server side
    private void showAssignedReports() throws IOException {
        try {
            LinkedList<Report> reports = sharedObject.getAllReports(); // Get all reports
            StringBuilder reportInfo = new StringBuilder("Current Registered Reports:\n");
            // Loop through all reports and display only the ones assigned to the logged-in user
            for (Report report : reports) {
                if (report.getAssignedEmployee().equals(sharedObject.getUserId(loggedInUser))) {
                    reportInfo.append(report.getReportType()).append(", ").append(report.getReportId()).append(", ").append(report.getDate()).append(", ").append(report.getEmployeeId()).append(", ").append(report.getStatus()).append(", ").append(report.getAssignedEmployee()).append("\n");
                }
            }
            // If no reports are assigned to the user, display a message
            if (reportInfo.toString().equals("Current Registered Reports:\n")) {
                reportInfo.append("No reports assigned to you.");
            }
            sendMessage(reportInfo.toString());
        } catch (IOException e) {
            System.err.println("Error during assigned employee reports output: " + e.getMessage());
        }
    }

    // Handles password update process on the server side
    private void updatePassword() throws IOException, ClassNotFoundException {
        try {
            // Get the new password from the user
            sendMessage("Enter new password: ");
            String newPassword = (String) in.readObject();
            // Update the password in the shared object
            if (sharedObject.updatePassword(loggedInUser, newPassword)) {
                sendMessage("Password updated successfully!");
            } else {
                sendMessage("An error occurred while updating the password.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during updating the password: " + e.getMessage());
        }
    }

    // Overridden run method to handle client requests and responses on the server side
    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
            sendMessage("Welcome to the Health and Safety Report Manager!");
            String response;
            // Login menu loop
            do {
                // Login menu
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
                if (loggedIn) {
                    // Main menu loop
                    do {
                        // Main menu
                        sendMessage("Choose an option: 1 - Create a report, 2 - Retrieve all reports, 3 - Assign the report, 4 - View assigned reports, 5 - Update password, 0 - Exit");
                        response = (String) in.readObject();
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