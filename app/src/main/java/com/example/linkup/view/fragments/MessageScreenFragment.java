package com.example.linkup.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.linkup.R;
import com.example.linkup.adapter.UserAdapter;
import com.example.linkup.model.User;
import com.example.linkup.utility.NavigationHelper;
import com.example.linkup.view.activities.ChatActivity;
import com.example.linkup.viewModel.UserViewModel;
import com.example.linkup.service.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageScreenFragment extends Fragment {

    private UserViewModel userViewModel;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_message_screen_fragment, container, false);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Set up the UserAdapter with the click listener
        userAdapter = new UserAdapter(new ArrayList<>(), user -> {
            // Handle user click, navigate to ChatFragment, pass the user information
            navigateToChatFragment(user);
        });

        recyclerView.setAdapter(userAdapter);

        // Call the fetchUsers method to initiate the data fetching
        userViewModel.fetchUsers();

        userViewModel.getUsersLiveData().observe(getViewLifecycleOwner(), users -> {
            onUsersReceived(users);
        });

        return view;
    }

    private void onUsersReceived(List<User> users) {
        String currentUserId = getCurrentUserId();

        List<User> filteredUsers = new ArrayList<>();

        for (User user : users) {
            if (user != null && !user.getUserId().equals(currentUserId)) {
                filteredUsers.add(user);
            }
        }

        userAdapter.setUserList(filteredUsers);
    }

    private void navigateToChatFragment(User user) {
        Intent intent = new Intent(requireContext(), ChatActivity.class);

        // Pass the receiverUserId to ChatActivity
        intent.putExtra("receiverUserId", user.getUserId());

        startActivity(intent);
    }

    private String getCurrentUserId() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }
}
