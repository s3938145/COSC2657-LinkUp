package com.example.linkup.model;

import java.util.List;

public class User {

    private String userId;               // Unique identifier assigned by Firestore
    private String userRole;            // User's role
    private String profileImage;         // URL or reference to the user's profile image
    private String rmitId;               // Separate unique identifier for the user
    private String fullName;             // User's full name
    private String password;             // User's password (consider storing a hash instead of plain text)
    private String email;                // User's email address
    private List<String> friendList;  // List of Firestore document IDs representing friends
    private String courseSchedule;       // URL or reference to the user's course schedule image
    private String fcmToken;

    // Default constructor (required for Firebase)
    public User() {
    }

    // Constructor with all fields
    public User(String userId, String userRole,String profileImage, String rmitId, String fullName, String password, String email, List<String> friendList, String courseSchedule, String fcmToken) {
        this.userId = userId;
        this.userRole = userRole;
        this.profileImage = profileImage;
        this.rmitId = rmitId;
        this.fullName = fullName;
        this.password = password;
        this.email = email;
        this.friendList = friendList;
        this.courseSchedule = courseSchedule;
        this.fcmToken = fcmToken;
    }

    // Getters and setters for all fields
    public String getUserId() {
        return userId;
    }
    public String getUserRole() { return userRole; }

    public String getProfileImage() {
        return profileImage;
    }

    public String getRmitId() {
        return rmitId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getFriendList() {
        return friendList;
    }

    public String getCourseSchedule() {
        return courseSchedule;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    // Optionally, override toString(), equals(), and hashCode() methods as needed
}

