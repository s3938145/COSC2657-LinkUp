package com.example.linkup.repository;

import com.example.linkup.model.User;
import com.example.linkup.service.FirebaseService;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;

public class UserRepository {

    private final FirebaseService firebaseService;

    public UserRepository(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    public Task<Void> addUser(User user) {
        return firebaseService.getFirestore().collection("users").document(user.getUserId()).set(user);
    }

    public FirebaseUser getCurrentUser() {
        return firebaseService.getCurrentUser();
    }

    public Task<DocumentSnapshot> getUser(String userId) {
        return firebaseService.getFirestore().collection("users").document(userId).get();
    }

    public Task<String> getUserRole(String userId) {
        return firebaseService.getUserRole(userId);
    }

    public Task<Void> updateUser(User user) {
        return firebaseService.getFirestore().collection("users").document(user.getUserId()).set(user);
    }

    public Task<Void> deleteUser(String userId) {
        return firebaseService.getFirestore().collection("users").document(userId).delete();
    }

    public Task<Void> addFriendToUser(String userId, String friendId) {
        return firebaseService.getFirestore().collection("users").document(userId)
                .update("friendList", FieldValue.arrayUnion(friendId));
    }
    public Task<QuerySnapshot> getAllUsers() {
        return firebaseService.getFirestore().collection("users").get();
    }

    // Additional repository methods as needed...
}



