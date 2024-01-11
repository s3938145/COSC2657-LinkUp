package com.example.linkup.utility;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;

import com.example.linkup.R;
import com.example.linkup.viewModel.UserViewModel;
import com.squareup.picasso.Picasso;

public class UserProfileHeaderHandler {

    public static void updateUserProfileViews(UserViewModel userViewModel, String userId, View rootView, LifecycleOwner lifecycleOwner) {
        ImageView imageViewProfile = rootView.findViewById(R.id.imageViewProfile);
        TextView textViewUserName = rootView.findViewById(R.id.textViewUserName);
        TextView textViewUserEmail = rootView.findViewById(R.id.textViewUserEmail);

        // Fetch user data
        userViewModel.getUser(userId);
        userViewModel.getUserLiveData().observe(lifecycleOwner, user -> {
            if (user != null && user.getUserId().equals(userId)) {
                textViewUserName.setText(user.getFullName());
                textViewUserEmail.setText(user.getEmail());
                if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                    Picasso.get().load(user.getProfileImage()).into(imageViewProfile);
                }
            }
        });
    }
}





