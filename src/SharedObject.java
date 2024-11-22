import java.util.LinkedList;

public class SharedObject {

    private LinkedList<User> list;

    public SharedObject() {
        list = new LinkedList<User>();
    }
    // Add a new user to the list
    public synchronized void addUser(String name, String employeeId, String email, String password, String departmentName, String role) {
        User temp = new User(name, employeeId, email, password, departmentName, role);
        list.add(temp);
    }
}
