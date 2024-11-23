import java.io.Serializable;
import java.util.LinkedList;

public class SharedObject implements Serializable {
    private LinkedList<User> list;

    // Constructor
    public SharedObject() {
        list = new LinkedList<User>();
    }

    // Add a new user to the list
    public synchronized void addUser(String name, String employeeId, String email, String password, String departmentName, String role) {
        User temp = new User(name.toLowerCase(), employeeId.toLowerCase(), email.toLowerCase(), password.toLowerCase(), departmentName.toLowerCase(), role.toLowerCase());
        list.add(temp);
    }

    // Method to return all users in the list
    public synchronized LinkedList<User> getAllUsers() {
        return new LinkedList<>(list); // Return a shallow copy to preserve encapsulation
    }
}
