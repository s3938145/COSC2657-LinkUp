package com.example.linkup.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.linkup.model.User;
import com.example.linkup.repository.UserRepository;
import com.example.linkup.service.FirebaseService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserViewModel extends AndroidViewModel {

    private UserRepository userRepository;
    private Map<String, MutableLiveData<User>> userCache;
    private MutableLiveData<User> userLiveData;
    private MutableLiveData<List<User>> usersLiveData;
    private MutableLiveData<String> errorMessage;
    private final MutableLiveData<User> selectedUser = new MutableLiveData<>();
    private MutableLiveData<String> currentUserRoleLiveData = new MutableLiveData<>();


    public UserViewModel(@NonNull Application application) {
        super(application); // Call the super constructor with the application context
        FirebaseService firebaseService = new FirebaseService(application.getApplicationContext());
        userRepository = new UserRepository(firebaseService);
        userCache = new HashMap<>();
        userLiveData = new MutableLiveData<>();
        usersLiveData = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void addUser(User user) {
        userRepository.addUser(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        userLiveData.postValue(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorMessage.postValue(e.getMessage());
                    }
                });
    }

    public LiveData<User> getUserLiveData(String userId) {
        if (!userCache.containsKey(userId)) {
            MutableLiveData<User> newUserLiveData = new MutableLiveData<>();
            userCache.put(userId, newUserLiveData);
            fetchUser(userId);
        }
        return userCache.get(userId);
    }

    private void fetchUser(String userId) {
        userRepository.getUser(userId)
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        MutableLiveData<User> liveData = userCache.get(userId);
                        if (liveData != null) {
                            liveData.postValue(user);
                        }
                    }
                })
                .addOnFailureListener(e -> errorMessage.postValue(e.getMessage()));
    }

    public void fetchAndStoreCurrentUserRole() {
        FirebaseUser currentUser = userRepository.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            userRepository.getUserRole(currentUserId).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    currentUserRoleLiveData.postValue(task.getResult());
                } else {
                    errorMessage.postValue(task.getException().getMessage());
                }
            });
        }
    }

    public LiveData<String> getCurrentUserRoleLiveData() {
        return currentUserRoleLiveData;
    }

    public void updateUser(User user) {
        userRepository.updateUser(user)
                .addOnSuccessListener(aVoid -> userLiveData.postValue(user))
                .addOnFailureListener(e -> errorMessage.postValue(e.getMessage()));
    }

    public void deleteUser(String userId) {
        userRepository.deleteUser(userId)
                .addOnSuccessListener(aVoid -> {
                    // You can add logic here for what happens after a user is successfully deleted
                })
                .addOnFailureListener(e -> errorMessage.postValue(e.getMessage()));
    }

    public void addFriend(String userId, String friendId) {
        userRepository.addFriendToUser(userId, friendId)
                .addOnSuccessListener(aVoid -> {
                    // You can add logic here for what happens after a friend is successfully added
                })
                .addOnFailureListener(e -> errorMessage.postValue(e.getMessage()));
    }

    public LiveData<List<User>> getUsersLiveData() {
        return usersLiveData;
    }

    public void fetchUsers() {
        userRepository.getAllUsers()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = queryDocumentSnapshots.toObjects(User.class);
                    usersLiveData.postValue(users);
                })
                .addOnFailureListener(e -> errorMessage.postValue(e.getMessage()));
    }
    public void setSelectedUser(User user) {
        selectedUser.setValue(user);
    }

    public LiveData<User> getSelectedUser() {
        return selectedUser;
    }
}




