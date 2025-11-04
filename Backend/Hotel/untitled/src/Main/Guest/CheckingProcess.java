package Main.Guest;

import Main.Employee.Employee;

public interface CheckingProcess {
    public void checkin(Employee frontdesk);
    public void checkout(Employee frontdesk);
}
