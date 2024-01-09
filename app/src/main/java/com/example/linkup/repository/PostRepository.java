package com.example.linkup.repository;

import com.example.linkup.model.Post;
import com.example.linkup.service.FirebaseService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

import com.example.linkup.model.Post;

public class PostRepository {

    private final FirebaseService firebaseService;
    private final String currentUserId;

    public PostRepository(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
        this.currentUserId = firebaseService.getCurrentUser().getUid();
    }

    public void addPost(Post post, final DataStatus dataStatus) {
        // The postId will be generated and set inside the FirebaseService
        Post newPost = new Post(null, currentUserId, post.getPostContent(), System.currentTimeMillis(), post.getPostLikes());
        firebaseService.addPostToDatabase(newPost, dataStatus);
    }

    public void getAllPosts(final DataStatus dataStatus, long lastLoadedPostDate, int limit) {
        firebaseService.loadPostsFromDatabase(dataStatus, lastLoadedPostDate, limit);
    }

    public void updatePost(Post post, final DataStatus dataStatus) {
        firebaseService.updatePostInDatabase(post, dataStatus);
    }

    public void deletePost(String postId, final DataStatus dataStatus) {
        firebaseService.deletePostFromDatabase(postId, dataStatus);
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




