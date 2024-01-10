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

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private UserRepository userRepository;
    private MutableLiveData<User> userLiveData;
    private MutableLiveData<List<User>> usersLiveData;
    private MutableLiveData<String> errorMessage;

    public UserViewModel(@NonNull Application application) {
        super(application); // Call the super constructor with the application context
        FirebaseService firebaseService = new FirebaseService(application.getApplicationContext());
        userRepository = new UserRepository(firebaseService);
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

    // Additional methods and logic as needed...
}




