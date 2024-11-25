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
    }

    //User related methods
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
    // Add report to the shared object and return true if successful
    public synchronized boolean addReport(Report report) {
        reports.add(report);
        return true;
    }

    // Method to return all reports in the list
    public synchronized LinkedList<Report> getAllReports() {
        return new LinkedList<>(reports); // Return a shallow copy to preserve encapsulation
    }
}