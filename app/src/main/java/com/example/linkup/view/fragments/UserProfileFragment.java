package com.example.linkup.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.linkup.R;
import com.example.linkup.profile.UpdateProfile;
import com.example.linkup.utility.ImageUtils;
import com.example.linkup.viewModel.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.annotation.Nullable;

public class UserProfileFragment extends Fragment {

    private String currentId;
    ImageView profileIcon;
    TextView etName, etID, etEmail, courseSchedule;
    Button button;
    UserViewModel userViewModel; // Add UserViewModel

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Initialize views
        profileIcon = view.findViewById(R.id.iv_profile_icon);
        etName = view.findViewById(R.id.tv_name_f1);
        etID = view.findViewById(R.id.tv_id_f1);
        etEmail = view.findViewById(R.id.tv_email);
        courseSchedule = view.findViewById(R.id.tv_course_schedule);
        button = view.findViewById(R.id.btn_edit);

        // Set up click listeners
        courseSchedule.setOnClickListener(v -> navigateToShowCourseSchedule());
        button.setOnClickListener(v -> edit());

        // Initialize UserViewModel
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get current user ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentId = user.getUid();
            loadUserData(currentId);
        }
    }

    private void loadUserData(String userId) {
        userViewModel.getUserLiveData(userId).observe(getViewLifecycleOwner(), user -> {
            if (user != null && user.getUserId().equals(userId)) {
                etName.setText(user.getFullName());
                etID.setText(user.getRmitId()); // Assuming getRmitId() is the method to get the ID
                etEmail.setText(user.getEmail());
                if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                    ImageUtils.loadImageAsync(user.getProfileImage(), profileIcon);
                }
            }
        });
    }

    private void edit() {
        Intent intent = new Intent(getActivity(), UpdateProfile.class);
        startActivity(intent);
    }

    private void navigateToShowCourseSchedule() {
        if (currentId != null ) {
            UserProfileFragmentDirections.ActionUserProfileToShowCourseScheduleFragment action =
                    UserProfileFragmentDirections.actionUserProfileToShowCourseScheduleFragment(currentId);
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(action);
        }
    }
}
