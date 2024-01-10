package com.example.linkup.model;

import java.util.List;

import java.util.Collections;

public final class Post {
    private final String postId;         // Unique identifier for the post
    private final String posterId;       // Unique identifier of the user who posted
    private final String postContent;    // Content of the post
    private final long postDate;         // Timestamp for when the post was made
    private final int postLikes;         // Number of likes on the post
    private final List<String> likedByUsers; // List of user IDs who liked the post

    // Constructor for Firebase (necessary for data retrieval)
    public Post() {
        postId = null;
        posterId = null;
        postContent = null;
        postDate = 0;
        postLikes = 0;
        likedByUsers = Collections.emptyList();
    }

    // Constructor with all fields
    public Post(String postId, String posterId, String postContent, long postDate, List<String> likedByUsers) {
        this.postId = postId;
        this.posterId = posterId;
        this.postContent = postContent;
        this.postDate = postDate;
        this.postLikes = likedByUsers.size();
        this.likedByUsers = likedByUsers;
    }

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

    public List<String> getLikedByUsers() {
        return likedByUsers;
    }

    // Other methods as needed...
}



