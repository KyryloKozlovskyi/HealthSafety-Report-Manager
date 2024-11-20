import java.io.Serializable;
import java.util.Date;

// Report class to store report data
public class Report implements Serializable {
    private String reportType; // Stores report type (Accident Report/New Health/Safety Risk Report)
    private int reportId;  // Stores unique ID
    private Date date; // Stores date of report creation
    private int employeeId; // Stores employeeId of the Report Creation
    private String status; // Stores report status ( Open/Assigned/Closed)
    private int assignedEmployee; // Stores employeeId of the employee assigned to the report

    // Constructor
    public Report(String reportType, int reportId, Date date, int employeeId, String status, int assignedEmployee) {
        this.reportType = reportType;
        this.reportId = reportId;
        this.date = date;
        this.employeeId = employeeId;
        this.status = status;
        this.assignedEmployee = assignedEmployee;
    }

    // Getters and Setters
    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAssignedEmployee() {
        return assignedEmployee;
    }

    public void setAssignedEmployee(int assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
    }
}