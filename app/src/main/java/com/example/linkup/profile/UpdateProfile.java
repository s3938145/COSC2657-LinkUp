package com.example.linkup.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.linkup.R;
import com.example.linkup.service.FirebaseService;
import com.example.linkup.utility.ImageUtils;
import com.example.linkup.view.activities.MainActivity;
import com.example.linkup.viewModel.UserViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class UpdateProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private UserViewModel userViewModel;
    private FirebaseService firebaseService;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // UI Components
    ImageView imagePicker; // Assuming this is your ImageView for picking the image
    EditText etName, etEmail, etID, etCourses;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        // Initialize FirebaseService and ViewModel
        firebaseService = new FirebaseService(this);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize UI components
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etID = findViewById(R.id.etId);
        etCourses = findViewById(R.id.etCourses);
        btnSave = findViewById(R.id.btnSave);
        imagePicker = findViewById(R.id.imagePicker); // Replace with your actual ImageView ID

        // Set listeners
        imagePicker.setOnClickListener(v -> openFileChooser());
        btnSave.setOnClickListener(view -> {
            if (imageUri != null) {
                fetchProfile();
            } else {
                fetchProfile();
            }
        });

        // Fetch and observe user data
        fetchAndObserveUserData();
    }

    private void fetchAndObserveUserData() {
        FirebaseUser currentUser = firebaseService.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            userViewModel.getUserLiveData(currentUserId).observe(this, user -> {
                if (user != null) {
                    imageUri = Uri.parse(user.getProfileImage());
                    etName.setText(user.getFullName());
                    etEmail.setText(user.getEmail());
                    etID.setText(user.getRmitId());
                    etCourses.setText(user.getCourseSchedule());

                    if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                        ImageUtils.loadImageAsync(user.getProfileImage(),imagePicker);
                    }
                }
            });
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // You can also update an ImageView to show the selected image
             imagePicker.setImageURI(imageUri);
        }
    }


    private void fetchProfile() {
        String name = etName.getText().toString();
        String id = etID.getText().toString();
        String email = etEmail.getText().toString();
        String courses = etCourses.getText().toString();

        Log.d("Image URI", String.valueOf(imageUri));

        if (imageUri != null && !Objects.requireNonNull(imageUri.getScheme()).startsWith("http")) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                byte[] imageData = ImageUtils.compressImage(bitmap,60);

                // Upload the compressed image to Firebase Storage
                firebaseService.uploadImageToStorage("profile_images/", imageData )
                        .addOnSuccessListener(uri -> {
                            String profileImageUri = uri.toString();
                            updateProfile(profileImageUri, name, id, email, courses);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(UpdateProfile.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            updateProfile(null, name, id, email, courses);
        }
    }

    private void updateProfile(String profile, String name, String id, String email, String courses) {
        FirebaseUser user = firebaseService.getCurrentUser();
        String currentUserId = user.getUid();

        final DocumentReference sDoc = db.collection("users").document(currentUserId);
        db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        //DocumentSnapshot snapshot = transaction.get(sfDocRef);
                        if (profile != null) {
                            transaction.update(sDoc, "profileImage", profile);
                        }

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
