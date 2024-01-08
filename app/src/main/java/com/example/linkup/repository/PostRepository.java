package com.example.linkup.repository;

import com.example.linkup.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostRepository {

    private DatabaseReference databaseReference;
    private String currentUserId;

    public PostRepository() {
        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void addPost(Post post, final DataStatus dataStatus) {
        // Add a new post to Realtime Database
        String postId = databaseReference.push().getKey();
        Post newPost = new Post(postId, currentUserId, post.getPostContent(), System.currentTimeMillis(), post.getPostLikes());
        databaseReference.child(postId).setValue(newPost)
                .addOnSuccessListener(aVoid -> dataStatus.DataIsInserted())
                .addOnFailureListener(e -> dataStatus.DataOperationFailed(e));
    }

    public void getAllPosts(final DataStatus dataStatus, long lastLoadedPostDate, int limit) {
        // Implement lazy loading by loading posts in batches
        databaseReference.orderByChild("postDate").endAt(lastLoadedPostDate).limitToLast(limit)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Post> postList = new ArrayList<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Post post = postSnapshot.getValue(Post.class);
                            postList.add(0, post); // Adds each new post to the beginning of the list
                        }
                        dataStatus.DataIsLoaded(postList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        dataStatus.DataOperationFailed(databaseError.toException());
                    }
                });
    }

    public void updatePost(Post post, final DataStatus dataStatus) {
        // Update post information in Realtime Database
        databaseReference.child(post.getPostId()).setValue(post)
                .addOnSuccessListener(aVoid -> dataStatus.DataIsUpdated())
                .addOnFailureListener(e -> dataStatus.DataOperationFailed(e));
    }

    public void deletePost(String postId, final DataStatus dataStatus) {
        // Delete post from Realtime Database
        databaseReference.child(postId).removeValue()
                .addOnSuccessListener(aVoid -> dataStatus.DataIsDeleted())
                .addOnFailureListener(e -> dataStatus.DataOperationFailed(e));
    }

    // Interface for callback
    public interface DataStatus {
        void DataIsLoaded(List<Post> posts);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
        void DataOperationFailed(Exception e);
    }
}


