package Main.Employee;

public class employee {
    private int StaffID;
    private String name;
    private String role;

    public employee(int StaffID, String name, String role){
        this.StaffID = StaffID;
        this.name = name;
        this.role = role;
    }

    private int getId(){
        return StaffID;
    }

    private String getName(){
        return name;
    }

    private String role() {
        return role;
    }
}
