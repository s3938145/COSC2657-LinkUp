package com.example.linkup.service;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
    public void addPostToDatabase(Post post, FirebaseCallback<Void> callback) {
        String postId = postsReference.push().getKey();
        Post newPost = new Post(postId, post.getPosterId(), post.getPostContent(), System.currentTimeMillis(), post.getLikedByUsers());
        postsReference.child(postId).setValue(newPost)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    public void loadPostsFromDatabase(long lastLoadedPostDate, int limit, FirebaseCallback<List<Post>> callback) {
        postsReference.orderByChild("postDate").endAt(lastLoadedPostDate).limitToLast(limit)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Post> postList = new ArrayList<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Post post = postSnapshot.getValue(Post.class);
                            postList.add(0, post);
                        }
                        callback.onSuccess(postList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onFailure(databaseError.toException());
                    }
                });
    }


    // Method to retrieve a single post by its postId
    public LiveData<Post> getPostById(String postId) {
        MutableLiveData<Post> postLiveData = new MutableLiveData<>();

        postsReference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                if (post != null) {
                    postLiveData.postValue(post);
                } else {
                    postLiveData.postValue(null); // Indicate that the post was not found
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the database error, for example, by logging it
            }
        });

        return postLiveData;
    }

    public void updatePostInDatabase(Post post, FirebaseCallback<Void> callback) {
        postsReference.child(post.getPostId()).setValue(post)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }


    public void deletePostFromDatabase(String postId, FirebaseCallback<Void> callback) {
        postsReference.child(postId).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }


    public void toggleLikeOnPost(String postId, String userId, FirebaseCallback<Void> callback) {
        DatabaseReference postRef = postsReference.child(postId);
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
                    callback.onFailure(databaseError.toException());
                } else {
                    callback.onSuccess(null);
                }
            }
        });
    }

    // ... other Firebase operations as needed ...

    public interface FirebaseCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }
}





