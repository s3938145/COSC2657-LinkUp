package com.example.linkup.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MessageScreenFragment extends Fragment {

    private UserViewModel userViewModel;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private SearchView searchView;
    private List<User> originalUsers;

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
        searchView = view.findViewById(R.id.searchView);
        setupSearchView();

        recyclerView.setAdapter(userAdapter);

        // Call the fetchUsers method to initiate the data fetching
        userViewModel.fetchUsers();

        userViewModel.getUsersLiveData().observe(getViewLifecycleOwner(), users -> {
            originalUsers = users;
            onUsersReceived(users);
        });

        return view;
    }

    private void onUsersReceived(List<User> users) {
        String currentUserId = getCurrentUserId();

        List<User> filteredUsers = new ArrayList<>();

        for (User user : users) {
            if (user != null && !user.getUserId().equals(currentUserId)) {
                hasUserSentRecentMessage(user.getUserId(), hasRecentMessage -> {
                    user.setHasRecentMessage(hasRecentMessage);
                    filteredUsers.add(user);

                    // Check if all users have been processed
                    if (filteredUsers.size() == users.size() - 1) {
                        Collections.sort(filteredUsers, (user1, user2) -> Boolean.compare(user2.hasRecentMessage(), user1.hasRecentMessage()));
                        userAdapter.setUserList(filteredUsers);
                    }
                });
            }
        }
    }


    private interface OnRecentMessageCheckListener {
        void onRecentMessageCheckComplete(boolean hasRecentMessage);
    }
    private void hasUserSentRecentMessage(String userId, OnRecentMessageCheckListener listener) {
        DatabaseReference messagesReference = FirebaseDatabase.getInstance().getReference("messages");
        Query recentMessagesQuery = messagesReference.orderByChild("senderId").equalTo(userId).limitToLast(1);

        recentMessagesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasRecentMessage = dataSnapshot.exists(); // Check if there's any message
                listener.onRecentMessageCheckComplete(hasRecentMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onRecentMessageCheckComplete(false); // Handle errors
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle submission if needed
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Update the user list based on the current query
                filterUsers(newText);
                return true;
            }
        });
    }

    private void filterUsers(String query) {
        List<User> filteredUsers = new ArrayList<>();

        if (userAdapter != null) {
            List<User> allUsers = userAdapter.getUserList(); // Assuming you have a method to get all users
            if (allUsers != null) {
                for (User user : allUsers) {
                    if (user.getFullName().toLowerCase().contains(query.toLowerCase())) {
                        filteredUsers.add(user);
                    }
                }
            }
        }

        userAdapter.setUserList(filteredUsers);
    }



    private void navigateToChatFragment(User user) {
        hasUserSentRecentMessage(user.getUserId(), hasRecentMessage -> {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra("receiverUserId", user.getUserId());
            startActivity(intent);
        });
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
