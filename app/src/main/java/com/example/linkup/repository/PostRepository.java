package com.example.linkup.repository;

import androidx.lifecycle.LiveData;

import com.example.linkup.model.Post;
import com.example.linkup.service.FirebaseService;

import java.util.ArrayList;
import java.util.List;

public class PostRepository {

    private final FirebaseService firebaseService;
    private final String currentUserId;

    public PostRepository(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
        this.currentUserId = firebaseService.getCurrentUser().getUid();
    }

    public void addPost(String postContent, FirebaseService.FirebaseCallback<Void> callback) {
        List<String> likedByUser = new ArrayList<>();
        Post newPost = new Post(null, currentUserId, postContent, System.currentTimeMillis(), likedByUser);
        firebaseService.addPostToDatabase(newPost, callback);
    }


    public void getAllPosts(long lastLoadedPostDate, int limit, FirebaseService.FirebaseCallback<List<Post>> callback) {
        firebaseService.loadPostsFromDatabase(lastLoadedPostDate, limit, callback);
    }


    // Method to retrieve a single post by its postId
    public LiveData<Post> getPostById(String postId) {
        return firebaseService.getPostById(postId);
    }


    public void updatePost(Post post, FirebaseService.FirebaseCallback<Void> callback) {
        firebaseService.updatePostInDatabase(post, callback);
    }


    public void deletePost(String postId, FirebaseService.FirebaseCallback<Void> callback) {
        firebaseService.deletePostFromDatabase(postId, callback);
    }


    public void toggleLikeOnPost(String postId, FirebaseService.FirebaseCallback<Void> callback) {
        firebaseService.toggleLikeOnPost(postId, currentUserId, callback);
    }


    // Additional methods and logic as needed...
}




