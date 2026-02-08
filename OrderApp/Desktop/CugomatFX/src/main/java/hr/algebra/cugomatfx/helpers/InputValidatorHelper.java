package hr.algebra.cugomatfx.helpers;

public class InputValidatorHelper {
    private static final String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$";
    private static final String phoneRegex = "^\\+?[0-9 ]{7,15}$";


    public static boolean isEmailValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.matches(emailRegex);
    }

    public static boolean isPhoneValid(String phone) {
        if (phone == null || phone.isEmpty()) {
            return true;
        }
        return phone.matches(phoneRegex);
    }
}
