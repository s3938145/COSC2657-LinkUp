package com.example.linkup.repository;

import com.example.linkup.model.User;
import com.example.linkup.service.FirebaseService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import com.example.linkup.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

public class UserRepository {

    private final FirebaseService firebaseService;

    public UserRepository(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    public Task<Void> addUser(User user) {
        return firebaseService.getFirestore().collection("users").document(user.getUserId()).set(user);
    }

    public Task<DocumentSnapshot> getUser(String userId) {
        return firebaseService.getFirestore().collection("users").document(userId).get();
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

    // Additional repository methods as needed...
}



