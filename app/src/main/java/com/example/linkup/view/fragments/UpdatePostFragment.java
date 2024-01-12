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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.linkup.R;
import com.example.linkup.model.Post;
import com.example.linkup.service.FirebaseService;
import com.example.linkup.utility.NavigationHelper;
import com.example.linkup.viewModel.PostViewModel;

public class UpdatePostFragment extends Fragment {

    private EditText editTextPostContent;
    private PostViewModel postViewModel;
    private FirebaseService firebaseService;
    private String postId;
    private Post currentPost;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false); // Use the same layout as for creating a post

        editTextPostContent = view.findViewById(R.id.editTextPostContent);
        Button buttonEdit = view.findViewById(R.id.buttonPost); // Assuming this ID is for the edit button
        Button buttonCancel = view.findViewById(R.id.buttonCancel);

        firebaseService = new FirebaseService(getContext());
        postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);

        postId = getArguments().getString("postId");
        if (postId != null && !postId.isEmpty()) {
            loadPostContent(postId);
        }

        buttonEdit.setOnClickListener(v -> editPost());
        buttonCancel.setOnClickListener(v -> cancelPost());

        return view;
    }

    private void loadPostContent(String postId) {
        LiveData<Post> postLiveData = postViewModel.getPostById(postId);

        // Observe the LiveData for changes
        postLiveData.observe(getViewLifecycleOwner(), post -> {
            if (post != null) {
                currentPost = post; // Store the current post
                editTextPostContent.setText(post.getPostContent());
            } else {
                Toast.makeText(getContext(), "Post not found or error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editPost() {
        String content = editTextPostContent.getText().toString().trim();
        if (!content.isEmpty() && currentPost != null) {
            // Create a new Post instance with updated content
            Post updatedPost = new Post(
                    currentPost.getPostId(),
                    currentPost.getPosterId(),
                    content,
                    currentPost.getPostDate(),
                    currentPost.getLikedByUsers()
            );

            postViewModel.updatePost(updatedPost);

            editTextPostContent.setText("");
            hideKeyboard();
            Toast.makeText(getContext(), "Post updated successfully", Toast.LENGTH_SHORT).show();
            NavigationHelper.navigateToFragment(requireView(), R.id.newsFeedFragment);
        } else {
            editTextPostContent.setError("Please enter some text to post.");
        }
    }

    private void cancelPost() {
        editTextPostContent.setText("");
        hideKeyboard();
        NavigationHelper.navigateToFragment(requireView(), R.id.newsFeedFragment);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextPostContent.getWindowToken(), 0);
    }
}



