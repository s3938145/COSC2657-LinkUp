package com.example.linkup.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.linkup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

public class UserProfile extends Fragment implements View.OnClickListener {

    ImageView imageView;
    TextView etName, etID, etEmail, etCourses;

    ImageButton imageButtonEdit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);

        imageView = getActivity().findViewById(R.id.iv_f1);
        etName = getActivity().findViewById(R.id.tv_name_f1);
        etID = getActivity().findViewById(R.id.tv_id_f1);
        etEmail = getActivity().findViewById(R.id.tv_email_f1);
        etCourses = getActivity().findViewById(R.id.tv_course_f1);

        imageButtonEdit = getActivity().findViewById(R.id.ib_edit_f1);

        imageButtonEdit.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.ib_edit_f1) {
            Intent intent = new Intent(getActivity(), UpdateProfile.class);
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentId = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference = firestore.collection("user").document(currentId);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            String nameResult = task.getResult().getString("name");
                            String idResult = task.getResult().getString("ID");
                            String emailResult = task.getResult().getString("Email");
                            String courseResult = task.getResult().getString("courses");

                            Picasso.get().load(currentId).into(imageView);
                            etName.setText(nameResult);
                            etID.setText(idResult);
                            etEmail.setText(emailResult);
                            etCourses.setText(courseResult);

                        }else {
                            Intent intent = new Intent(getActivity(),CreateProfile.class);
                            startActivity(intent);
                        }
                    }
                });

    }
}