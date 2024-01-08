package com.example.linkup.model;

import java.util.Date;

public class Post {

    private String postId;        // Unique identifier for the post
    private String posterId;      // Unique identifier of the user who posted
    private String postContent;   // Content of the post
    private long postDate;        // Timestamp for when the post was made
    private int postLikes;        // Number of likes on the post

    // Default constructor (required for Firebase Realtime Database)
    public Post() {
    }

    // Constructor with all fields
    public Post(String postId, String posterId, String postContent, long postDate, int postLikes) {
        this.postId = postId;
        this.posterId = posterId;
        this.postContent = postContent;
        this.postDate = postDate;
        this.postLikes = postLikes;
    }

    // Getters and setters for all fields
    public String getPostId() {
        return postId;
    }

    public String getPosterId() {
        return posterId;
    }

    public String getPostContent() {
        return postContent;
    }

    public long getPostDate() {
        return postDate;
    }

    public int getPostLikes() {
        return postLikes;
    }

}


