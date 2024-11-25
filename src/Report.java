import java.util.Date;

// Report class to store report data
public class Report {
    private ReportType reportType; // Stores report type (Accident Report/New Health/Safety Risk Report)
    private String reportId;  // Stores unique ID
    private Date date; // Stores date of report creation
    private String employeeId; // Stores employeeId of the Report Creation
    private ReportStatus status; // Stores report status ( Open/Assigned/Closed)
    private String assignedEmployee; // Stores employeeId of the employee assigned to the report

    // Constructor
    public Report(ReportType reportType, String reportId, Date date, String employeeId, ReportStatus status, String assignedEmployee) {
        this.reportType = reportType;
        this.reportId = reportId;
        this.date = date;
        this.employeeId = employeeId;
        this.status = status;
        this.assignedEmployee = assignedEmployee;
    }

    // Getters and Setters
    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getAssignedEmployee() {
        return assignedEmployee;
    }

    public void setAssignedEmployee(String assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
    }

    @Override
    public String toString() {
        return "Report{" + "reportType=" + reportType + ", reportId='" + reportId + '\'' + ", date=" + date + ", employeeId='" + employeeId + '\'' + ", status=" + status + ", assignedEmployee='" + assignedEmployee + '\'' + '}';
    }
}