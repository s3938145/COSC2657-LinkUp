package com.example.linkup.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.linkup.R;
import com.example.linkup.model.Post;
import com.example.linkup.model.User;
import com.example.linkup.utility.ImageUtils;
import com.example.linkup.utility.NavigationHelper;
import com.example.linkup.viewModel.PostViewModel;
import com.example.linkup.viewModel.UserViewModel;
import com.squareup.picasso.Picasso;

public class ShowCourseScheduleFragment extends Fragment {

    private ImageView imageViewCourseSchedule;
    private TextView textViewUserNameSchedule;
    private UserViewModel userViewModel;
    private PostViewModel postViewModel;
    private String postId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_course_schedule, container, false);

        imageViewCourseSchedule = view.findViewById(R.id.imageViewCourseSchedule);
        textViewUserNameSchedule = view.findViewById(R.id.textViewUserNameSchedule);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);

        Button buttonClose = view.findViewById(R.id.buttonClose); // Button to close the fragment
        buttonClose.setOnClickListener(v -> closeFragment());

        Bundle arguments = getArguments();
        if (arguments != null) {
            postId = arguments.getString("postId");
            if (postId != null && !postId.isEmpty()) {
                loadPostDetails(postId);
            }
        }
        Log.d("Course Schedule Fragment", "The current post ID is: " + postId);
        return view;
    }

    private void loadPostDetails(String postId) {
        LiveData<Post> postLiveData = postViewModel.getPostById(postId);

        postLiveData.observe(getViewLifecycleOwner(), post -> {
            if (post != null) {
                loadUserCourseSchedule(post.getPosterId());
            } else {
                Toast.makeText(getContext(), "Post not found or error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserCourseSchedule(String userId) {
        LiveData<User> userLiveData = userViewModel.getUserLiveData(userId);

        Log.d("Course Schedule Fragment", "The current user ID is: " + userId);
        userLiveData.observe(getViewLifecycleOwner(), user -> {
            if (user != null && user.getCourseSchedule() != null && !user.getCourseSchedule().isEmpty()) {
                textViewUserNameSchedule.setText(user.getFullName() + "'s Course Schedule");
                ImageUtils.loadImageAsync(user.getCourseSchedule(), imageViewCourseSchedule);

                imageViewCourseSchedule.setVisibility(View.VISIBLE); // Make the image view visible
                // Reset the text view to be above the image view
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) textViewUserNameSchedule.getLayoutParams();
                layoutParams.bottomToTop = imageViewCourseSchedule.getId();
                textViewUserNameSchedule.setLayoutParams(layoutParams);
            } else {
                textViewUserNameSchedule.setText(user.getFullName() + "'s Course Schedule isn't available");
                Toast.makeText(getContext(), "User course schedule not found", Toast.LENGTH_SHORT).show();

                imageViewCourseSchedule.setVisibility(View.GONE); // Hide the image view

                // Set the text view to be centered in the parent ConstraintLayout
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) textViewUserNameSchedule.getLayoutParams();
                layoutParams.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
                layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                textViewUserNameSchedule.setLayoutParams(layoutParams);
            }
        });
    }


    private void closeFragment() {
        // Logic to close or pop the fragment from the stack
        NavigationHelper.navigateToFragment(requireView(), R.id.newsFeedFragment);
    }
}
