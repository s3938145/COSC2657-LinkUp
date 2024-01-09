package com.example.linkup.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.linkup.R;
import com.example.linkup.model.Post;
import com.example.linkup.utility.UserProfileHeaderHandler;
import com.example.linkup.viewModel.PostViewModel;
import com.example.linkup.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class CreatePostFragment extends Fragment {

    private UserViewModel userViewModel;
    private PostViewModel postViewModel;
    private EditText editTextPostContent;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        editTextPostContent = view.findViewById(R.id.editTextPostContent);
        Button buttonPost = view.findViewById(R.id.buttonPost);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);

        // Initialize ViewModels
        postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Find the included user profile header layout
        View userProfileHeader = view.findViewById(R.id.user_profile_header);

        // Update user profile header views
        UserProfileHeaderHandler.updateUserProfileViews(userViewModel, userProfileHeader, getViewLifecycleOwner());

        buttonPost.setOnClickListener(v -> createPost());
        buttonCancel.setOnClickListener(v -> cancelPost());

        return view;
    }

    private void createPost() {
        String content = editTextPostContent.getText().toString().trim();
        if (!content.isEmpty()) {
            // Creating a post with current time as date and 0 likes
            Post newPost = new Post(null, FirebaseAuth.getInstance().getCurrentUser().getUid(), content, System.currentTimeMillis(), 0);
            postViewModel.addPost(newPost);

            // Clear input and hide keyboard
            editTextPostContent.setText("");
            hideKeyboard();

            // Optionally navigate back or show a success message
        } else {
            editTextPostContent.setError("Please enter some text to post.");
        }
    }

    private void cancelPost() {
        // Clear input and hide keyboard
        editTextPostContent.setText("");
        hideKeyboard();

        // Optionally navigate back or clear any status messages
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextPostContent.getWindowToken(), 0);
    }
}

