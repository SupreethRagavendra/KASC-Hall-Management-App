package com.kasc.hall;

// UserData.java

import android.view.View;

public class UserData {
    private static String facultyName;
    private static String facultyId;

    public static String getFacultyName() {
        return facultyName;
    }

    public static void setFacultyName(String name) {
        facultyName = name;
    }

    public static String getFacultyId() {
        return facultyId;
    }

    public static void setFacultyId(String id) {
        facultyId = id;

    }
    private void bookEvent(View view) {
        // Use UserData to get faculty name and ID
        String facultyName = UserData.getFacultyName();
        String facultyId = UserData.getFacultyId();

    }

}
