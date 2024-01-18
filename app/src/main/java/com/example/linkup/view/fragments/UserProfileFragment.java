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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.annotation.Nullable;

public class UserProfileFragment extends Fragment {

    ImageView imageView;
    TextView etName, etID, etEmail, etCourses;

    Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        imageView = view.findViewById(R.id.iv_f1);
        etName = view.findViewById(R.id.tv_name_f1);
        etID = view.findViewById(R.id.tv_id_f1);
        etEmail = view.findViewById(R.id.tv_email_f1);
        etCourses = view.findViewById(R.id.tv_course_f1);

        button = view.findViewById(R.id.ib_edit_f1);

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
                            String nameResult = task.getResult().getString("fullName");
                            String idResult = task.getResult().getString("rmitId");
                            String emailResult = task.getResult().getString("email");
                            String courseResult = task.getResult().getString("courseSchedule");

                            //Picasso.get().load(currentId).into(imageView);
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