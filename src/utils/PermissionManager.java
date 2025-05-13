package utils;

import model.User;

public class PermissionManager {
    public static final String ROLE_USER = "user";
    public static final String ROLE_ADMIN = "admin";

    public static boolean canModifyData(User user) {
        if (user == null) return false;
        return !user.getRole().equalsIgnoreCase(ROLE_USER);
    }

    public static boolean canAddData(User user) {
        return canModifyData(user);
    }

    public static boolean canDeleteData(User user) {
        return canModifyData(user);
    }
} 