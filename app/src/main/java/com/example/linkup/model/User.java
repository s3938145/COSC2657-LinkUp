package com.example.linkup.model;

import java.util.List;

public class User {

    private String userId;               // Unique identifier assigned by Firestore
    private String profileImage;         // URL or reference to the user's profile image
    private String rmitId;               // Separate unique identifier for the user
    private String fullName;             // User's full name
    private String password;             // User's password (consider storing a hash instead of plain text)
    private String email;                // User's email address
    private List<String> friendList;  // List of Firestore document IDs representing friends
    private String courseSchedule;       // URL or reference to the user's course schedule image

    // Default constructor (required for Firebase)
    public User() {
    }

    // Constructor with all fields
    public User(String userId, String profileImage, String rmitId, String fullName, String password, String email, List<String> friendList, String courseSchedule) {
        this.userId = userId;
        this.profileImage = profileImage;
        this.rmitId = rmitId;
        this.fullName = fullName;
        this.password = password;
        this.email = email;
        this.friendList = friendList;
        this.courseSchedule = courseSchedule;
    }

    // Getters and setters for all fields
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getRmitId() {
        return rmitId;
    }

    public void setRmitId(String rmitId) {
        this.rmitId = rmitId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<String> friendList) {
        this.friendList = friendList;
    }

    public String getCourseSchedule() {
        return courseSchedule;
    }

    public void setCourseSchedule(String courseSchedule) {
        this.courseSchedule = courseSchedule;
    }

    // Optionally, override toString(), equals(), and hashCode() methods as needed
}

