package Main.Employee;

public abstract class JobApplication {
    public String applicantName;
    public String positionApplied;
    public String resumeText;
    public String applicationDate;
    public String applicationStatus;
    
    public abstract void submitApplication();
    public abstract void reviewApplication();
}
