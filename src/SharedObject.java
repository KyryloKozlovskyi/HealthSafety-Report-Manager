import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class SharedObject implements Serializable {
    private LinkedList<User> users;
    private Set<String> emailSet;
    private Set<String> employeeIdSet;

    // Constructor
    public SharedObject() {
        users = new LinkedList<User>();
        emailSet = new HashSet<>();
        employeeIdSet = new HashSet<>();
    }

    // Check if the email is unique
    public synchronized boolean isEmailUnique(String email) {
        return !emailSet.contains(email);
    }

    // Check if the employee ID is unique
    public synchronized boolean isEmployeeIdUnique(String employeeId) {
        return !employeeIdSet.contains(employeeId);
    }

    // Add user to the shared object
    public synchronized boolean addUser(String name, String employeeId, String email, String password, String departmentName, String role) {
        // Check if the email and employee ID are unique before adding to the list
        if (isEmailUnique(email) && isEmployeeIdUnique(employeeId)) {
            User newUser = new User(name, employeeId, email, password, departmentName, role);
            users.add(newUser);
            emailSet.add(email);
            employeeIdSet.add(employeeId);
            return true;
        } else {
            return false;
        }
    }

    // Method to verify if the email and password are correct
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
}
