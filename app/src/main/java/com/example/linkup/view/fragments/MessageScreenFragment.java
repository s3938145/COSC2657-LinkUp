package com.example.linkup.view.fragments;

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
import com.example.linkup.viewModel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

// MessageScreenFragment.java

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
        userAdapter.setUserList(users);
    }

    private void navigateToChatFragment(User user) {
        userViewModel.setSelectedUser(user);

        // Pass the user ID or other necessary data to the ChatFragment
        Bundle args = new Bundle();
        args.putString("userId", user.getUserId());
        Navigation.findNavController(requireView()).navigate(R.id.action_messageScreenFragment_to_chatFragment, args);
    }
}

