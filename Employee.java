
import java.time.LocalDate;

/**
 * Full-access employee user.
 * 
 * MILESTONE 2 ENHANCEMENTS:
 * - Added department tracking
 * - Added employee ID and hire date
 * - Added permission details
 */
public class Employee extends User {
    private String department;
    private LocalDate hireDate;
    private String employeeNumber;

    public Employee(String userId, String name) {
        super(userId, name, "Employee");
        this.department = "General Operations";
        this.hireDate = LocalDate.now();
        this.employeeNumber = userId;
    }

    public Employee(String userId, String name, String department) {
        super(userId, name, "Employee");
        this.department = department;
        this.hireDate = LocalDate.now();
        this.employeeNumber = userId;
    }

    // Getters and Setters
    public String getDepartment() { return department; }
    public void setDepartment(String department) { 
        if (department != null && !department.isEmpty()) {
            this.department = department; 
        }
    }
    
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate date) { 
        if (date != null) {
            this.hireDate = date; 
        }
    }
    
    public String getEmployeeNumber() { return employeeNumber; }

    @Override public boolean canDeleteRecords()    { return true; }
    @Override public boolean canUpdateInventory()  { return true; }
    @Override public boolean canViewDonorRecords() { return true; }

    @Override
    public String toString() {
        return String.format("User[%s] %s (Employee - %s) | Emp#: %s | Hired: %s",
            getUserId(), getName(), department, employeeNumber, hireDate);
    }
}
