package com.example.linkup.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.example.linkup.R;
import com.example.linkup.service.FirebaseService;
import com.example.linkup.utility.UserProfileHeaderHandler;
import com.example.linkup.viewModel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private FirebaseService firebaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseService = new FirebaseService(this);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(bottomNav, navController);


        // Only proceed if firebaseService is not null
        if (firebaseService != null) {
            FirebaseUser currentUser = firebaseService.getCurrentUser();
            if (currentUser != null) {
                // Fetch the user's ID
                String userId = currentUser.getUid();

                // Find the included user profile header layout
                View userProfileHeader = findViewById(R.id.user_profile_header);
                if (userProfileHeader != null) {
                    UserProfileHeaderHandler.updateUserProfileViews(userViewModel, userId, userProfileHeader, this);
                }
            }
        }
    }
}