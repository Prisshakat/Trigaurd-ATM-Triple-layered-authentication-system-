
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// OTP Service Class
class OTPService {
    private Map<String, String> otpStore = new HashMap<>();

    // Generate 6-digit OTP
    public String generateOTP(String username) {
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(999999));
        otpStore.put(username, otp);
        return otp;
    }

    // Validate OTP
    public boolean verifyOTP(String username, String enteredOtp) {
        if (otpStore.containsKey(username)) {
            String validOtp = otpStore.get(username);
            return validOtp.equals(enteredOtp);
        }
        return false;
    }
}

// Authentication Engine Class
class AuthenticationEngine {
    private Map<String, String> userDatabase = new HashMap<>(); // username → password
    private OTPService otpService = new OTPService();

    public AuthenticationEngine() {
        // Sample user data (in real project, this comes from database)
        userDatabase.put("user123", "password123");
        userDatabase.put("atmUser", "securePass");
    }

    // Step 1: Verify username & password
    public boolean verifyCredentials(String username, String password) {
        return userDatabase.containsKey(username) && userDatabase.get(username).equals(password);
    }

    // Step 2: Generate OTP and send (console output for now)
    public String initiateOTP(String username) {
        if (userDatabase.containsKey(username)) {
            String otp = otpService.generateOTP(username);
            System.out.println("OTP for " + username + ": " + otp); // In real project, send via SMS/Email
            return otp;
        }
        return null;
    }

    // Step 3: Validate OTP
    public boolean validateOTP(String username, String otp) {
        return otpService.verifyOTP(username, otp);
    }
}

// Main Class to Run
public class Main {
    public static void main(String[] args) {
        AuthenticationEngine authEngine = new AuthenticationEngine();

        String username = "user123";
        String password = "password123";

        // Step 1: Verify credentials
        if (authEngine.verifyCredentials(username, password)) {
            System.out.println("✅ Credentials verified!");

            // Step 2: Send OTP
            String otp = authEngine.initiateOTP(username);

            // Simulate user entering OTP
            String enteredOtp = otp; // here we directly use correct OTP
            if (authEngine.validateOTP(username, enteredOtp)) {
                System.out.println("✅ Authentication Successful! Access Granted.");
            } else {
                System.out.println("❌ Invalid OTP. Access Denied.");
            }
        } else {
            System.out.println("❌ Invalid username/password.");
        }
    }
}
