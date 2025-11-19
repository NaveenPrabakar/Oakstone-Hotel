package Main.Employee;

import java.time.LocalDateTime;

public class Employee {
    private int StaffID;
    private String name;
    private String role;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private double totalHoursWorked;
    private String username;
    private String password;

    public Employee(int StaffID, String name, String role, String username, String password){
        this.StaffID = StaffID;
        this.name = name;
        this.role = role;
        this.totalHoursWorked = 0.0;
        this.username = username;
        this.password = password;
    }

    public Employee(int id, String name, String role) {
        this.StaffID = StaffID;
        this.name = name;
        this.role = role;
    }

    public int getId(){
        return StaffID;
    }

    public String getName(){
        return name;
    }

    public String role() {
        return role;
    }

    public LocalDateTime getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(LocalDateTime clockInTime) {
        this.clockInTime = clockInTime;
    }

    public LocalDateTime getClockOutTime() {
        return clockOutTime;
    }

    public void setClockOutTime(LocalDateTime clockOutTime) {
        this.clockOutTime = clockOutTime;
    }

    public double getTotalHoursWorked() {
        return totalHoursWorked;
    }

    public void addHoursWorked(double hours) {
        this.totalHoursWorked += hours;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}