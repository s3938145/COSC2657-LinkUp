package com.example.linkup.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.linkup.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public Task<String> getUserRole(String userId) {
        // Create a TaskCompletionSource to manage the result of the Firestore call
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();

        DocumentReference userDocRef = firestore.collection("users").document(userId);
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User user = document.toObject(User.class);
                    if (user != null && user.getUserRole() != null) {
                        // Resolve the Task with the userRole
                        taskCompletionSource.setResult(user.getUserRole());
                    } else {
                        // Handle the case where userRole is not set
                        taskCompletionSource.setResult(null);
                    }
                } else {
                    // Handle the case where the user document does not exist
                    taskCompletionSource.setException(new Exception("User not found"));
                }
            } else {
                // Handle any errors
                taskCompletionSource.setException(task.getException());
            }
        });

        return taskCompletionSource.getTask();
    }


    // Firestore methods
    public FirebaseFirestore getFirestore() {
        return firestore;
    }

    // Firebase Storage methods
    public Task<Uri> uploadImageToStorage(String path, byte[] imageData) {
        // Generate a unique filename using UUID and timestamp
        String uniqueFilename = "image_" + UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + ".jpg";

        // Create a reference to the unique file path
        StorageReference imageRef = firebaseStorage.getReference(path + uniqueFilename);

        // Upload the image data
        return imageRef.putBytes(imageData).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                if (task.getException() != null) {
                    throw task.getException();
                } else {
                    throw new IOException("Failed to upload image due to unknown error");
                }
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

                List<String> likedByUsers = new ArrayList<>(p.getLikedByUsers());
                if (likedByUsers.contains(userId)) {
                    likedByUsers.remove(userId);
                } else {
                    likedByUsers.add(userId);
                }

                mutableData.setValue(new Post(p.getPostId(), p.getPosterId(), p.getPostContent(), p.getPostDate(), likedByUsers));
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



    public void updateFcmTokenForCurrentUser(String newToken) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userDocRef = firestore.collection("users").document(userId);

            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    // Create a new User object with updated FCM token
                    User updatedUser = new User(user.getUserId(), user.getUserRole(), user.getProfileImage(), user.getRmitId(), user.getFullName(), user.getPassword(), user.getEmail(), user.getFriendList(), user.getCourseSchedule(), newToken);

                    // Write the updated user back to Firestore
                    userDocRef.set(updatedUser)
                            .addOnSuccessListener(aVoid -> Log.d("FirebaseService", "FCM Token updated successfully"))
                            .addOnFailureListener(e -> Log.w("FirebaseService", "Error updating FCM Token", e));
                }
            }).addOnFailureListener(e -> Log.w("FirebaseService", "Error fetching user for FCM Token update", e));
        }
    }

    public void checkAndUpdateUserRole(String userId) {
        DocumentReference userDocRef = firestore.collection("users").document(userId);

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null && (user.getUserRole() == null || user.getUserRole().isEmpty())) {
                // Update only the userRole field in Firestore
                userDocRef.update("userRole", "user")
                        .addOnSuccessListener(aVoid -> Log.d("TAG", "UserRole set to 'user' for user: " + userId))
                        .addOnFailureListener(e -> Log.w("TAG", "Failed to update UserRole for user: " + userId));
            }
        }).addOnFailureListener(e -> Log.w("TAG", "Error fetching user document for UserRole check", e));
    }






    // ... other Firebase operations as needed ...

    public interface FirebaseCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }


}





