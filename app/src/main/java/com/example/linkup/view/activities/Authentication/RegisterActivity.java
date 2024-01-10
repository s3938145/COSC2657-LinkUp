package com.example.linkup.view.activities.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.linkup.R;
import com.example.linkup.model.User;
import com.example.linkup.service.FirebaseService;
import com.example.linkup.utility.ValidationUtils;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextFullName, editTextEmail, editTextPassword, editTextRetypePassword, editTextRMITId;
    private Button btnRegister; // Updated button reference
    private FirebaseService firebaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRetypePassword = findViewById(R.id.editTextRetypePassword);
        editTextRMITId = findViewById(R.id.editTextRMITId);
        btnRegister = findViewById(R.id.btnRegister);

        firebaseService = new FirebaseService(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        // Retrieve user input
        String fullName = editTextFullName.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String rmitId = editTextRMITId.getText().toString();
        String retypePassword = editTextRetypePassword.getText().toString();

        // Validations
        if (!ValidationUtils.isNotEmpty(fullName) ||
                !ValidationUtils.isNotEmpty(email) ||
                !ValidationUtils.isNotEmpty(password) ||
                !ValidationUtils.isNotEmpty(rmitId)) {
            showToast("Please fill in all fields");
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            showToast("Invalid email address");
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            showToast("Password must be at least 8 characters, include a number, a capital letter, and a special character");
            return;
        }

        if (!password.equals(retypePassword)) {
            showToast("Passwords do not match");
            return;
        }

        if (!ValidationUtils.isValidRmitId(rmitId)) {
            showToast("Invalid RMIT ID. It should start with 's' followed by 7 digits");
            return;
        }


        // If validation passes, proceed with Firebase Authentication registration
        firebaseService.signUpUser(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User registration successful, retrieve Firebase Authentication UID
                        String firebaseAuthUserId = firebaseService.getCurrentUser().getUid();

                        // Create a new User object with the Firebase UID
                        User newUser = new User(
                                firebaseAuthUserId,  // Set userId to Firebase Authentication UID
                                null,   // Set profileImage to null initially
                                rmitId,
                                fullName,
                                password,   // In a production scenario, consider using password hashing
                                email,
                                new ArrayList<>(),  // Initialize an empty friendList
                                null    // Set courseSchedule to null initially
                        );

                        // Get a reference to the "users" collection
                        CollectionReference usersCollection = firebaseService.getFirestore().collection("users");

                        // Set the new user to Firestore with the document ID as the Firebase UID
                        usersCollection.document(firebaseAuthUserId).set(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    showToast("User registered successfully");
                                    // Optionally, navigate to the next activity or perform other actions
                                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(loginIntent);
                                    finish(); // Close the current activity
                                })
                                .addOnFailureListener(e -> showToast("Failed to add user to Firestore"));
                    } else {
                        // User registration failed
                        showToast("User registration failed: " + task.getException().getMessage());
                    }
                });

    }

    private void showToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}