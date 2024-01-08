package com.example.linkup.repository;

import com.example.linkup.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private FirebaseFirestore firestore;

    public UserRepository() {
        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
    }

    public void addUser(User user, final DataStatus dataStatus) {
        // If userId is null or empty, Firestore will auto-generate it
        String userId = (user.getUserId() == null || user.getUserId().isEmpty()) ? firestore.collection("users").document().getId() : user.getUserId();
        user.setUserId(userId);

        firestore.collection("users").document(userId).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsInserted();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        dataStatus.DataInsertFailed(e);
                    }
                });
    }

    public void getUser(String userId, final DataStatus dataStatus) {
        // Retrieve user information from Firestore
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        dataStatus.DataIsLoaded(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle the error
                    }
                });
    }

    public void updateUser(User user, final DataStatus dataStatus) {
        // Update user information in Firestore
        firestore.collection("users").document(user.getUserId()).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsUpdated();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle the error
                    }
                });
    }

    public void deleteUser(String userId, final DataStatus dataStatus) {
        // Delete user from Firestore
        firestore.collection("users").document(userId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsDeleted();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle the error
                    }
                });
    }

    public void addFriendToUser(String userId, String friendId, final DataStatus dataStatus) {
        DocumentReference userRef = firestore.collection("users").document(userId);

        firestore.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(userRef);
                    User user = snapshot.toObject(User.class);
                    if (user != null) {
                        List<String> friendList = user.getFriendList();
                        if (friendList == null) {
                            friendList = new ArrayList<>();
                        }
                        if (!friendList.contains(friendId)) {
                            friendList.add(friendId);
                            user.setFriendList(friendList);
                            transaction.set(userRef, user);
                        } else {
                            // Friend already in list, handle this case if needed
                        }
                    }
                    return null;
                }).addOnSuccessListener(aVoid -> dataStatus.DataIsUpdated())
                .addOnFailureListener(e -> dataStatus.DataUpdateFailed(e));
    }

    // Interface for callback
    public interface DataStatus {
        void DataIsLoaded(User user);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
        void DataInsertFailed(Exception e);
        void DataUpdateFailed(Exception e);
    }
}


