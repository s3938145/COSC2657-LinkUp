package com.example.linkup.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.linkup.R;
import com.example.linkup.model.Post;
import com.example.linkup.model.User;
import com.example.linkup.service.FirebaseService;
import com.example.linkup.utility.NavigationHelper;
import com.example.linkup.utility.UserProfileHeaderHandler;
import com.example.linkup.viewModel.PostViewModel;
import com.example.linkup.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseUser;

public class CreatePostFragment extends Fragment {

    private UserViewModel userViewModel;
    private PostViewModel postViewModel;
    private EditText editTextPostContent;
    private FirebaseService firebaseService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        editTextPostContent = view.findViewById(R.id.editTextPostContent);
        Button buttonPost = view.findViewById(R.id.buttonPost);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);

        // Initialize FirebaseService
        firebaseService = new FirebaseService(getContext());

        // Initialize ViewModels
        postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Find the included user profile header layout
        View userProfileHeader = view.findViewById(R.id.user_profile_header);

        // Get currently sign in user's
        String currentUserId = firebaseService.getCurrentUser().getUid();

        // Update user profile header views
         UserProfileHeaderHandler.updateUserProfileViews(userViewModel, currentUserId, userProfileHeader, getViewLifecycleOwner());

        buttonPost.setOnClickListener(v -> {
            createPost();
        });
        buttonCancel.setOnClickListener(v -> {
            cancelPost();
        });

        return view;
    }

    private void createPost() {
        String content = editTextPostContent.getText().toString().trim();
        if (!content.isEmpty()) {
            // Creating a post with current time as date and 0 likes
            postViewModel.addPost(content);

            // Clear input and hide keyboard
            editTextPostContent.setText("");
            hideKeyboard();

            // Display a toast message
            Toast.makeText(getContext(), "Post created successfully", Toast.LENGTH_SHORT).show();

            NavigationHelper.navigateToFragment(requireView(), R.id.newsFeedFragment);
        } else {
            editTextPostContent.setError("Please enter some text to post.");
        }
    }

    private void cancelPost() {
        // Clear input and hide keyboard
        editTextPostContent.setText("");
        hideKeyboard();

        NavigationHelper.navigateToFragment(requireView(), R.id.newsFeedFragment);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextPostContent.getWindowToken(), 0);
    }
}

