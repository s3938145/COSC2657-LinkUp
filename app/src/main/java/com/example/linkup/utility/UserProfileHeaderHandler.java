package com.example.linkup.utility;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.linkup.R;
import com.example.linkup.model.User;
import com.example.linkup.viewModel.UserViewModel;
import com.squareup.picasso.Picasso;

public class UserProfileHeaderHandler {

    // Method to update the user profile views
    public static void updateUserProfileViews(UserViewModel userViewModel, String userId, View rootView, LifecycleOwner lifecycleOwner) {
        // Get references to the views
        ImageView imageViewProfile = rootView.findViewById(R.id.imageViewProfile);
        TextView textViewUserName = rootView.findViewById(R.id.textViewUserName);
        TextView textViewUserEmail = rootView.findViewById(R.id.textViewUserEmail);

        // Observe changes in the user data
        // Fetch user details and update the views
        userViewModel.getUser(userId);
        Log.e("Current User ID", userId);
        userViewModel.getUserLiveData().observe(lifecycleOwner, user -> {
            if (user != null && user.getUserId().equals(userId)) {
                textViewUserName.setText(user.getFullName());
                textViewUserEmail.setText(user.getEmail());
                Log.e("Given User's name", user.getFullName());
                Log.e("Given User's email", user.getEmail());
                if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                    Picasso.get().load(user.getProfileImage()).into(imageViewProfile);
                }
            }
        });

        // Handle potential errors
        userViewModel.getErrorMessage().observe(lifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                // Handle the error message, e.g., show a toast or log it
            }
        });
    }
}


