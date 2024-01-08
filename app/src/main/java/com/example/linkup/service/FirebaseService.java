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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.linkup.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    // Firebase Authentication with Email and Password
    public Task<AuthResult> signUpUser(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> signInUser(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public void signOutUser() {
        firebaseAuth.signOut();
        googleSignInClient.signOut(); // To sign out from Google Sign-In if used
    }

    // Firebase Google Authentication
    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public Task<AuthResult> firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return firebaseAuth.signInWithCredential(credential);
    }

    // Get the currently signed-in user (if any)
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    // Firestore operations
    public FirebaseFirestore getFirestore() {
        return firestore;
    }

    // Firebase Storage operations
    public Task<Uri> uploadImageToStorage(String path, byte[] imageData) {
        StorageReference imageRef = firebaseStorage.getReference(path);
        return imageRef.putBytes(imageData).continueWithTask(task -> {
            if (!task.isSuccessful() && task.getException() != null) {
                throw task.getException();
            }
            return imageRef.getDownloadUrl();
        });
    }

    // Realtime Database operations
    public DatabaseReference getPostsReference() {
        return postsReference;
    }

    // ... other Firebase operations as needed ...
}




