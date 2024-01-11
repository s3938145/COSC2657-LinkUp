package com.example.linkup.service;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.linkup.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.example.linkup.model.Post;
import com.example.linkup.repository.PostRepository.DataStatus;

import java.util.ArrayList;
import java.util.List;

public class FirebaseService {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final FirebaseStorage firebaseStorage;
    private final GoogleSignInClient googleSignInClient;
    private final DatabaseReference postsReference;

    public FirebaseService(Context context) {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        postsReference = FirebaseDatabase.getInstance().getReference("posts");

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    // Firebase Authentication methods
    public Task<AuthResult> signUpUser(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> signInUser(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public void signOutUser() {
        firebaseAuth.signOut();
        googleSignInClient.signOut(); // Sign out from Google Sign-In if used
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public Task<AuthResult> firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return firebaseAuth.signInWithCredential(credential);
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    // Firestore methods
    public FirebaseFirestore getFirestore() {
        return firestore;
    }

    // Firebase Storage methods
    public Task<Uri> uploadImageToStorage(String path, byte[] imageData) {
        StorageReference imageRef = firebaseStorage.getReference(path);
        return imageRef.putBytes(imageData).continueWithTask(task -> {
            if (!task.isSuccessful() && task.getException() != null) {
                throw task.getException();
            }
            return imageRef.getDownloadUrl();
        });
    }

    // Realtime Database operations for posts
    public void addPostToDatabase(Post post, final DataStatus dataStatus) {
        String postId = postsReference.push().getKey();
        // Assuming Post class has a constructor or a static method to create a new instance with postId
        Post newPost = new Post(postId, post.getPosterId(), post.getPostContent(), System.currentTimeMillis(), post.getLikedByUsers());
        postsReference.child(postId).setValue(newPost)
                .addOnSuccessListener(aVoid -> dataStatus.DataIsInserted())
                .addOnFailureListener(dataStatus::DataOperationFailed);
    }

    public void loadPostsFromDatabase(final DataStatus dataStatus, long lastLoadedPostDate, int limit) {
        postsReference.orderByChild("postDate").endAt(lastLoadedPostDate).limitToLast(limit)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Post> postList = new ArrayList<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Post post = postSnapshot.getValue(Post.class);
                            postList.add(0, post);
                        }
                        dataStatus.DataIsLoaded(postList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        dataStatus.DataOperationFailed(databaseError.toException());
                    }
                });
    }

    public void updatePostInDatabase(Post post, final DataStatus dataStatus) {
        postsReference.child(post.getPostId()).setValue(post)
                .addOnSuccessListener(aVoid -> dataStatus.DataIsUpdated())
                .addOnFailureListener(dataStatus::DataOperationFailed);
    }

    public void deletePostFromDatabase(String postId, final DataStatus dataStatus) {
        postsReference.child(postId).removeValue()
                .addOnSuccessListener(aVoid -> dataStatus.DataIsDeleted())
                .addOnFailureListener(dataStatus::DataOperationFailed);
    }

    public void toggleLikeOnPost(Post post, String userId, final DataStatus dataStatus) {
        DatabaseReference postRef = postsReference.child(post.getPostId());
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                List<String> likedByUsers = p.getLikedByUsers() != null ? new ArrayList<>(p.getLikedByUsers()) : new ArrayList<>();
                if (likedByUsers.contains(userId)) {
                    likedByUsers.remove(userId);
                } else {
                    likedByUsers.add(userId);
                }

                Post updatedPost = new Post(p.getPostId(), p.getPosterId(), p.getPostContent(), p.getPostDate(), likedByUsers);
                mutableData.setValue(updatedPost);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    dataStatus.DataOperationFailed(databaseError.toException());
                } else {
                    dataStatus.DataIsUpdated();
                }
            }
        });
    }
    // ... other Firebase operations as needed ...
}





