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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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
    private String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_course_schedule, container, false);

        imageViewCourseSchedule = view.findViewById(R.id.imageViewCourseSchedule);
        textViewUserNameSchedule = view.findViewById(R.id.textViewUserNameSchedule);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        Button buttonClose = view.findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(v -> closeFragment());

        Bundle arguments = getArguments();
        if (arguments != null) {
            userId = arguments.getString("userId");
            if (userId != null && !userId.isEmpty()) {
                loadUserDetails(userId);
            }
        }
        Log.d("Course Schedule Fragment", "The current user ID is: " + userId);
        return view;
    }

    private void loadUserDetails(String userId) {
        LiveData<User> userLiveData = userViewModel.getUserLiveData(userId);

        userLiveData.observe(getViewLifecycleOwner(), user -> {
            if (user != null && user.getCourseSchedule() != null && !user.getCourseSchedule().isEmpty()) {
                textViewUserNameSchedule.setText(user.getFullName() + "'s Course Schedule");
                ImageUtils.loadImageAsync(user.getCourseSchedule(), imageViewCourseSchedule);

                imageViewCourseSchedule.setVisibility(View.VISIBLE); // Make the image view visible
                // Adjust the text view to be above the image view
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) textViewUserNameSchedule.getLayoutParams();
                layoutParams.bottomToTop = imageViewCourseSchedule.getId();
                textViewUserNameSchedule.setLayoutParams(layoutParams);
            } else {
                textViewUserNameSchedule.setText(user.getFullName() + "'s Course Schedule isn't available");
                Toast.makeText(getContext(), "User course schedule not found", Toast.LENGTH_SHORT).show();

                imageViewCourseSchedule.setVisibility(View.GONE); // Hide the image view

                // Center the text view in the parent ConstraintLayout
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) textViewUserNameSchedule.getLayoutParams();
                layoutParams.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
                layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                textViewUserNameSchedule.setLayoutParams(layoutParams);
            }
        });
    }

    private void closeFragment() {
        // Logic to pop the fragment from the stack
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
    }
}

