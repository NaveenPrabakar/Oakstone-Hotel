package Main.Data;

import Main.Main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class ReportWriter implements DataAnalysis {
    @Override
    public void createReport(String title, List<String> content) {
        try {
            File file = new File(title + ".txt");
            try (PrintWriter writer = new PrintWriter(file)) {

                // Same header format as DataPanel
                writer.println("====== Data Report ======");
                writer.println("Report Title: " + title);
                writer.println("================================\n");

                // Writes all provided content lines
                for (String line : content) {
                    writer.println(line);
                }

                System.out.println("Report saved to: " + file.getAbsolutePath());
            }

        } catch (IOException e) {
            System.out.println("Error writing report: " + e.getMessage());
        }
    }

    @Override
    public void editReport(String filename) {
        Scanner sc = new Scanner(System.in);
        File file = new File(filename);

        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(file.toPath());

            while (true) {
                System.out.println("\n===== Current Report =====");
                for (int i = 0; i < lines.size(); i++) {
                    System.out.println((i + 1) + ": " + lines.get(i));
                }
                System.out.println("==========================");

                System.out.println("\nEdit Options:");
                System.out.println("1. Edit a Line");
                System.out.println("2. Delete a Line");
                System.out.println("3. Add a New Line");
                System.out.println("4. Save & Exit");
                System.out.print("Choose: ");

                int choice = sc.nextInt();
                sc.nextLine();

                if (choice == 1) {
                    System.out.print("Enter line number to edit: ");
                    int lineNum = sc.nextInt();
                    sc.nextLine();

                    if (lineNum < 1 || lineNum > lines.size()) {
                        System.out.println("Invalid line.");
                        continue;
                    }

                    System.out.println("Current: " + lines.get(lineNum - 1));
                    System.out.print("New text: ");
                    lines.set(lineNum - 1, sc.nextLine());

                } else if (choice == 2) {
                    System.out.print("Enter line number to delete: ");
                    int lineNum = sc.nextInt();
                    sc.nextLine();

                    if (lineNum < 1 || lineNum > lines.size()) {
                        System.out.println("Invalid line.");
                        continue;
                    }

                    lines.remove(lineNum - 1);
                    System.out.println("Line removed.");

                } else if (choice == 3) {
                    System.out.print("Enter new line to append: ");
                    lines.add(sc.nextLine());

                } else if (choice == 4) {
                    Files.write(file.toPath(), lines);
                    System.out.println("Report updated successfully!");
                    break;

                } else {
                    System.out.println("Invalid choice.");
                }
            }

        } catch (IOException e) {
            System.out.println("Error editing report: " + e.getMessage());
        }
    }

    @Override
    public void deleteReport(String filename) {
        Scanner sc = new Scanner(System.in);
        File file = new File(filename);

        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        System.out.print("Are you sure you want to delete '" + filename + "'? (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();

        if (confirm.equals("y")) {
            if (file.delete()) {
                System.out.println("Report deleted successfully: " + filename);
            } else {
                System.out.println("Failed to delete report.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
}
