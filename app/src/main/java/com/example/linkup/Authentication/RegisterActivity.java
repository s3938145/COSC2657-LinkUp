package com.example.linkup.Authentication;

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
        editTextRetypePassword = findViewById(R.id.editTextRetypePassword); // New reference
        editTextRMITId = findViewById(R.id.editTextRMITId);
        btnRegister = findViewById(R.id.btnRegister); // Updated reference

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

        // Perform basic validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || rmitId.isEmpty()) {
            showToast("Please fill in all fields");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid email address");
            return;
        }

        if (password.length() < 6) {
            showToast("Password must be at least 6 characters");
            return;
        }
        if (!password.equals(retypePassword)) {
            showToast("Passwords do not match");
            return;
        }

        // Additional validation for RMIT ID (assuming it should be numeric)
        try {
            Long.parseLong(rmitId);
        } catch (NumberFormatException e) {
            showToast("RMIT ID must be numeric only");
            return;
        }

        // Create a new User object
        User newUser = new User(
                null,   // Firestore will assign the userId
                null,   // Set profileImage to null initially
                rmitId,
                fullName,
                password,   // In a production scenario, consider using password hashing
                email,
                new ArrayList<>(),  // Initialize an empty friendList
                null    // Set courseSchedule to null initially
        );

        firebaseService.signUpUser(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User registration successful

                        // Get a reference to the "users" collection
                        CollectionReference usersCollection = firebaseService.getFirestore().collection("users");

                        // Add the new user to Firestore
                        usersCollection.add(newUser)
                                .addOnSuccessListener(documentReference -> {
                                    showToast("User registered successfully");
                                    // Optionally, navigate to the next activity or perform other actions
                                })
                                .addOnFailureListener(e -> showToast("Failed to add user to Firestore"));
                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish(); // Close the current activity to prevent going back to it from LoginActivity
                    } else {
                        // User registration failed
                        showToast("User registration failed");
                    }
                });

    }

    private void showToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}