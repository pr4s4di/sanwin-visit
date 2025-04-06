package com.statics;

public class StaticCurrentUser {

    private static String currentUserName;

    public static String getCurrentUserName() {
        return currentUserName;
    }

    public static void setCurrentUserName(String currentUserName) {
        StaticCurrentUser.currentUserName = currentUserName;
    }
}
