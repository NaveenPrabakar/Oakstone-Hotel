package Main.Controller;

public class PasswordUtils {
    public static boolean isValid(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false;        // uppercase
        if (!password.matches(".*[a-z].*")) return false;        // lowercase
        if (!password.matches(".*\\d.*")) return false;          // digit
        if (!password.matches(".*[!@#$%^&*()_+\\-={}:;<>?,.].*")) return false; // special char

        return true;
    }

    public static String strength(String password) {
        int score = 0;

        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-={}:;<>?,.].*")) score++;

        if (score <= 2) return "Weak";
        if (score <= 4) return "Moderate";
        return "Strong";
    }
}
