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

    public void addPost(String postContent, final DataStatus dataStatus) {
        List likedByUser = new ArrayList<String>();
        // The postId will be generated and set inside the FirebaseService
        Post newPost = new Post(null, currentUserId, postContent, System.currentTimeMillis(), likedByUser);
        firebaseService.addPostToDatabase(newPost, dataStatus);
    }

    public void getAllPosts(final DataStatus dataStatus, long lastLoadedPostDate, int limit) {
        firebaseService.loadPostsFromDatabase(dataStatus, lastLoadedPostDate, limit);
    }

    // Method to retrieve a single post by its postId
    public LiveData<Post> getPostById(String postId) {
        return firebaseService.getPostById(postId);
    }

    public void updatePost(Post post, final DataStatus dataStatus) {
        firebaseService.updatePostInDatabase(post, dataStatus);
    }

    public void deletePost(String postId, final DataStatus dataStatus) {
        firebaseService.deletePostFromDatabase(postId, dataStatus);
    }

    public void toggleLikeOnPost(String postId, final DataStatus dataStatus) {
        firebaseService.toggleLikeOnPost(postId, currentUserId,dataStatus);
    }


    // Interface for callback
    public interface DataStatus {
        void DataIsLoaded(List<Post> posts);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
        void DataOperationFailed(Exception e);
    }

    // Additional methods and logic as needed...
}




