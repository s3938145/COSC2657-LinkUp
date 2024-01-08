package com.example.linkup.service;

import com.example.linkup.R;
import com.example.linkup.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import android.net.Uri;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.content.Context;
import android.net.Uri;

public class FirebaseService {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private GoogleSignInClient googleSignInClient;

    public FirebaseService(Context context) {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Configure Google Sign-In
        String clientId = context.getString(R.string.default_web_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientId)
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
    }

    // Firebase Google Authentication
    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public Task<AuthResult> firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return firebaseAuth.signInWithCredential(credential);
    }

    // Firestore Operations
    public Task<Void> addUserToFirestore(User user) {
        return firestore.collection("users").document(user.getUserId()).set(user);
    }

    public Task<Void> updateUserInFirestore(User user) {
        return firestore.collection("users").document(user.getUserId()).set(user);
    }

    public Task<Uri> uploadImageToStorage(String userId, String type, byte[] imageData) {
        String path = "images/" + userId + "_" + type + ".jpg";
        StorageReference imageRef = FirebaseStorage.getInstance().getReference(path);

        return imageRef.putBytes(imageData)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                });
    }

    public Task<Void> updateUserProfileImage(String userId, Uri imageUrl) {
        return firestore.collection("users").document(userId)
                .update("profileImage", imageUrl.toString());
    }

    public Task<Void> updateUserCourseSchedule(String userId, Uri imageUrl) {
        return firestore.collection("users").document(userId)
                .update("courseSchedule", imageUrl.toString());
    }
    // Add other Firestore operations as needed

    // Additional methods as needed for your app
}


