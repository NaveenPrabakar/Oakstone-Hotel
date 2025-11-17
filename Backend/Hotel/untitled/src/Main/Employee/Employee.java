package Main.Employee;

import java.time.LocalDateTime;

public class Employee {
    private int StaffID;
    private String name;
    private String role;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private double totalHoursWorked;

    public Employee(int StaffID, String name, String role){
        this.StaffID = StaffID;
        this.name = name;
        this.role = role;
        this.totalHoursWorked = 0.0;
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
}