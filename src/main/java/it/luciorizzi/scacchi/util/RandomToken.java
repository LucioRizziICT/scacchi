package it.luciorizzi.scacchi.util;

public class RandomToken {
    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String lower = upper.toLowerCase();
    public static final String digits = "0123456789";
    public static final String alphanum = upper + lower + digits;

    public static String generateToken(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("Length must be at least 1");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(alphanum.charAt((int) (Math.random() * alphanum.length())));
        }
        return sb.toString();
    }

}
