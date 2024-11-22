import java.io.Serializable;

// User class to store user data
public class User implements Serializable {
    private String name; // Stores user name
    private String employeeId; // Stores unique ID
    private String email; // Stores user unique email
    private String password; // Stores user password
    private String departmentName; // Stores department name
    private String role; // Stores user role

    // Constructor
    public User(String name, String employeeId, String email, String password, String departmentName, String role) {
        this.name = name;
        this.employeeId = employeeId;
        this.email = email;
        this.password = password;
        this.departmentName = departmentName;
        this.role = role;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}