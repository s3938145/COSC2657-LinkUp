package com.example.linkup.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.linkup.model.User;
import com.example.linkup.repository.UserRepository;

import java.util.List;

public class UserViewModel extends ViewModel {

    private UserRepository userRepository;
    private MutableLiveData<User> userLiveData;
    private MutableLiveData<List<User>> usersLiveData;
    private MutableLiveData<String> errorMessage;

    public UserViewModel() {
        userRepository = new UserRepository();
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
        userRepository.addUser(user, new UserRepository.DataStatus() {
            @Override
            public void DataIsLoaded(User user) {
                // Not used for addUser
            }

            @Override
            public void DataIsInserted() {
                userLiveData.setValue(user);
            }

            @Override
            public void DataIsUpdated() {
                // Not used for addUser
            }

            @Override
            public void DataIsDeleted() {
                // Not used for addUser
            }

            @Override
            public void DataInsertFailed(Exception e) {
                errorMessage.setValue(e.getMessage());
            }

            @Override
            public void DataUpdateFailed(Exception e) {

            }
        });
    }

    public void getUser(String userId) {
        userRepository.getUser(userId, new UserRepository.DataStatus() {
            @Override
            public void DataIsLoaded(User user) {
                userLiveData.setValue(user);
            }

            @Override
            public void DataIsInserted() {
                // Not used for getUser
            }

            @Override
            public void DataIsUpdated() {
                // Not used for getUser
            }

            @Override
            public void DataIsDeleted() {
                // Not used for getUser
            }

            @Override
            public void DataInsertFailed(Exception e) {
                errorMessage.setValue(e.getMessage());
            }

            @Override
            public void DataUpdateFailed(Exception e) {

            }
        });
    }


    public void updateUser(User user) {
        userRepository.updateUser(user, new UserRepository.DataStatus() {
            @Override
            public void DataIsLoaded(User user) {
                // Not used for updateUser
            }

            @Override
            public void DataIsInserted() {
                // Not used for updateUser
            }

            @Override
            public void DataIsUpdated() {
                userLiveData.setValue(user);
            }

            @Override
            public void DataIsDeleted() {
                // Not used for updateUser
            }

            @Override
            public void DataInsertFailed(Exception e) {
                errorMessage.setValue(e.getMessage());
            }

            @Override
            public void DataUpdateFailed(Exception e) {

            }
        });
    }

    public void deleteUser(String userId) {
        userRepository.deleteUser(userId, new UserRepository.DataStatus() {
            @Override
            public void DataIsLoaded(User user) {
                // Not used for deleteUser
            }

            @Override
            public void DataIsInserted() {
                // Not used for deleteUser
            }

            @Override
            public void DataIsUpdated() {
                // Not used for deleteUser
            }

            @Override
            public void DataIsDeleted() {
                // Logic after deletion, if needed
            }

            @Override
            public void DataInsertFailed(Exception e) {
                errorMessage.setValue(e.getMessage());
            }

            @Override
            public void DataUpdateFailed(Exception e) {

            }
        });
    }

    public void addFriend(String userId, String friendId) {
        userRepository.addFriendToUser(userId, friendId, new UserRepository.DataStatus() {
            @Override
            public void DataIsLoaded(User user) {
                // Not used in this context
            }

            @Override
            public void DataIsInserted() {
                // Not used in this context
            }

            @Override
            public void DataIsUpdated() {
                // React to the friend being successfully added
            }

            @Override
            public void DataIsDeleted() {
                // Not used in this context
            }

            @Override
            public void DataInsertFailed(Exception e) {
                // Handle the error, for example, update LiveData that the UI observes
            }

            @Override
            public void DataUpdateFailed(Exception e) {
                // Handle the error, for example, update LiveData that the UI observes
            }
        });
    }

    // Additional methods as needed, e.g., for handling lists of users
}

