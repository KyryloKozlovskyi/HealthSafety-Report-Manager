import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class SharedObject {
    private LinkedList<User> users; // Stores users
    private LinkedList<Report> reports; // Stores reports
    // Used for checking if email and employee ID are unique
    private Set<String> emailSet;
    private Set<String> employeeIdSet;

    // Constructor
    public SharedObject() {
        users = new LinkedList<User>();
        reports = new LinkedList<Report>();
        emailSet = new HashSet<>();
        employeeIdSet = new HashSet<>();
        loadUsers("users.txt");
        loadReports("reports.txt");
    }

    //User related methods
    // Method to load users from file to the shared object
    public synchronized void loadUsers(String fileName) {
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
                    addUser(user);
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

    // Check if the email is unique
    public synchronized boolean isEmailUnique(String email) {
        return !emailSet.contains(email);
    }

    // Check if the employee ID is unique
    public synchronized boolean isEmployeeIdUnique(String employeeId) {
        return !employeeIdSet.contains(employeeId);
    }

    // Add user to the shared object and return true if successful
    public synchronized boolean addUser(User user) {
        // Check if the email and employee ID are unique before adding to the list
        if (isEmailUnique(user.getEmail()) && isEmployeeIdUnique(user.getEmployeeId())) {
            users.add(user);
            emailSet.add(user.getEmail());
            employeeIdSet.add(user.getEmployeeId());
            writeUsersToFile("users.txt");
            return true;
        } else {
            return false;
        }
    }

    // Method to verify if the email and password are correct and return true if successful
    public synchronized boolean checkCredentials(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    // Method to return all users in the list
    public synchronized LinkedList<User> getAllUsers() {
        return new LinkedList<>(users); // Return a shallow copy to preserve encapsulation
    }

    public synchronized String getUserId(String email) {
        String id = "";
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                id = user.getEmployeeId();
            }
        }
        return id;
    }

    // Report related methods
    // Method to write data to a file
    public synchronized void writeUsersToFile(String fileName) {
        // Get all users and reports from the shared object
        LinkedList<User> users = getAllUsers();
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

    // Method to load users from file to the shared object
    public synchronized void loadReports(String fileName) {
        Report report;
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

        // Read data from file
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line
                String[] reportFields = line.split("~");
                // Add user to the shared object
                if (reportFields.length == 6) {
                    ReportType reportType = ReportType.valueOf(reportFields[0]);
                    String reportId = reportFields[1];
                    Date date = dateFormat.parse(reportFields[2]);
                    String employeeId = reportFields[3];
                    ReportStatus status = ReportStatus.valueOf(reportFields[4]);
                    String assignedEmployee = reportFields[5];
                    report = new Report(reportType, reportId, date, employeeId, status, assignedEmployee);
                    addReport(report);
                }
            }
        } catch (ParseException e) {
            System.err.println("Date parsing error: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + fileName);
            e.printStackTrace();
        }
    }

    // Add report to the shared object and return true if successful
    public synchronized boolean addReport(Report report) {
        reports.add(report);
        writeReportsToFile("reports.txt");
        return true;
    }

    // Method to return all reports in the list
    public synchronized LinkedList<Report> getAllReports() {
        return new LinkedList<>(reports); // Return a shallow copy to preserve encapsulation
    }

    // Method to write reports data to a file
    public synchronized void writeReportsToFile(String fileName) {
        // Get all users and reports from the shared object
        LinkedList<Report> reports = getAllReports();
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
}