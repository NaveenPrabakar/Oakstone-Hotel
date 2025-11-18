package Main.Data;

import java.util.List;

public interface DataAnalysis {
    void createReport(String title, List<String> content);
    void editReport(String filename);
    void deleteReport(String filename);
}

