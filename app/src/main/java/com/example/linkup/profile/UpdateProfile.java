package com.example.linkup.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.linkup.R;
import com.example.linkup.view.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class UpdateProfile extends AppCompatActivity {

    EditText etName, etEmail, etID, etCourses;
    Button btnSave;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference reference;
    DocumentReference documentReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        documentReference = db.collection("users").document(currentuid);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etID = findViewById(R.id.etId);
        etCourses = findViewById(R.id.etCourses);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    String nameResult = task.getResult().getString("fullName");
                    String idResult = task.getResult().getString("rmitId");
                    String emailResult = task.getResult().getString("email");
                    String courseResult = task.getResult().getString("courseSchedule");

                    etName.setText(nameResult);
                    etID.setText(idResult);
                    etEmail.setText(emailResult);
                    etCourses.setText(courseResult);
                }else {
                    Toast.makeText(UpdateProfile.this, "No profile", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void updateProfile() {
        String name = etName.getText().toString();
        String id = etID.getText().toString();
        String email = etEmail.getText().toString();
        String courses = etCourses.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        final DocumentReference sDoc = db.collection("users").document(currentuid);
        db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        //DocumentSnapshot snapshot = transaction.get(sfDocRef);

                        transaction.update(sDoc, "fullName", name);
                        transaction.update(sDoc, "rmitId",id);
                        transaction.update(sDoc, "email", email);
                        transaction.update(sDoc, "courseSchedule", courses);

                        // Success
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateProfile.this, "updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateProfile.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfile.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}