package com.example.linkup.viewModel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.linkup.model.User;
import com.example.linkup.repository.UserRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.linkup.model.User;
import com.example.linkup.service.FirebaseService;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserViewModel extends ViewModel {

    private UserRepository userRepository;
    private MutableLiveData<User> userLiveData;
    private MutableLiveData<List<User>> usersLiveData;
    private MutableLiveData<String> errorMessage;

    public UserViewModel(Application application) {
        FirebaseService firebaseService = new FirebaseService(application.getApplicationContext()); // Pass context if needed
        userRepository = new UserRepository(firebaseService);
        userLiveData = new MutableLiveData<>();
        usersLiveData = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<List<User>> getUsersLiveData() {
        return usersLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void addUser(User user) {
        userRepository.addUser(user)
                .addOnSuccessListener(aVoid -> userLiveData.postValue(user))
                .addOnFailureListener(e -> errorMessage.postValue(e.getMessage()));
    }

    public void getUser(String userId) {
        userRepository.getUser(userId)
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    userLiveData.postValue(user);
                })
                .addOnFailureListener(e -> errorMessage.postValue(e.getMessage()));
    }

    public void updateUser(User user) {
        userRepository.updateUser(user)
                .addOnSuccessListener(aVoid -> userLiveData.postValue(user))
                .addOnFailureListener(e -> errorMessage.postValue(e.getMessage()));
    }

    public void deleteUser(String userId) {
        userRepository.deleteUser(userId)
                .addOnSuccessListener(aVoid -> { /* Handle deletion success */ })
                .addOnFailureListener(e -> errorMessage.postValue(e.getMessage()));
    }

    public void addFriend(String userId, String friendId) {
        userRepository.addFriendToUser(userId, friendId)
                .addOnSuccessListener(aVoid -> { /* Handle friend addition success */ })
                .addOnFailureListener(e -> errorMessage.postValue(e.getMessage()));
    }

    // Additional methods as needed...
}


