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

import com.example.linkup.R;
import com.example.linkup.profile.UpdateProfile;
import com.example.linkup.utility.ImageUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.annotation.Nullable;

public class UserProfileFragment extends Fragment {

    ImageView profileIcon;
    TextView etName, etID, etEmail, etCourses;

    Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        profileIcon = view.findViewById(R.id.iv_profile_icon);
        etName = view.findViewById(R.id.tv_name_f1);
        etID = view.findViewById(R.id.tv_id_f1);
        etEmail = view.findViewById(R.id.tv_email);
        etCourses = view.findViewById(R.id.tv_course_schedule);

        button = view.findViewById(R.id.btn_edit);

        button.setOnClickListener(v -> {
            edit();
        });

        return view;
    }

    public void edit() {

        Intent intent = new Intent(getActivity(), UpdateProfile.class);
        startActivity(intent);

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentId = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference = firestore.collection("users").document(currentId);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            String profileResult = task.getResult().getString("profileImage");
                            String nameResult = task.getResult().getString("fullName");
                            String idResult = task.getResult().getString("rmitId");
                            String emailResult = task.getResult().getString("email");
                            String courseResult = task.getResult().getString("courseSchedule");

                            ImageUtils.loadImageAsync(profileResult, profileIcon);
                            etName.setText(nameResult);
                            etID.setText(idResult);
                            etEmail.setText(emailResult);
                            etCourses.setText(courseResult);

                        }else {
                            Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}