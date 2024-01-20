package com.example.linkup.profile;

import android.app.ProgressDialog;
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
import android.widget.TextView;
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
import java.util.function.Consumer;

public class UpdateProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_COURSE_IMAGE_REQUEST = 2;
    private Uri imageUri;
    private Uri courseImageUri;
    private UserViewModel userViewModel;
    private FirebaseService firebaseService;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;

    // UI Components
    ImageView profilePicker;
    EditText etName, etEmail, etID;
    TextView coursePicker;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        // Initialize FirebaseService and ViewModel
        firebaseService = new FirebaseService(this);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        progressDialog = new ProgressDialog(this);

        // Initialize UI components
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etID = findViewById(R.id.etId);
        btnSave = findViewById(R.id.btnSave);
        coursePicker = findViewById(R.id.courseSchedule);
        profilePicker = findViewById(R.id.profilePicker);

        // Set listeners for uploading profile icon
        profilePicker.setOnClickListener(v -> openFileChooserForProfile());

        // Set listeners for uploading course schedule
        coursePicker.setOnClickListener(v -> openFileChooserForCourse());

        btnSave.setOnClickListener(view -> {
            fetchProfile();
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
                    // Check and handle profile image
                    if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                        ImageUtils.loadImageAsync(user.getProfileImage(), profilePicker);
                    } else {
                        // Handle the case where profile image is null or empty
                        profilePicker.setImageResource(R.drawable.create_post); // Replace with your default image resource
                    }
                    imageUri = user.getProfileImage() != null ? Uri.parse(user.getProfileImage()) : null;

                    // Set text fields
                    etName.setText(user.getFullName() != null ? user.getFullName() : "");
                    etEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                    etID.setText(user.getRmitId() != null ? user.getRmitId() : "");

                    // Check and handle course schedule
                    if (user.getCourseSchedule() != null && !user.getCourseSchedule().isEmpty()) {
                        coursePicker.setText(user.getCourseSchedule());
                    } else {
                        // Handle the case where course schedule is null or empty
                        coursePicker.setText("No course schedule available");
                    }
                    courseImageUri = user.getCourseSchedule() != null ? Uri.parse(user.getCourseSchedule()) : null;
                }
            });
        }
    }


    private void openFileChooserForProfile() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openFileChooserForCourse() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_COURSE_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                // Handle profile image selection
                imageUri = data.getData();
                profilePicker.setImageURI(imageUri);
            } else if (requestCode == PICK_COURSE_IMAGE_REQUEST) {
                // Handle course schedule image selection
                courseImageUri = data.getData();
                // Update the coursePicker TextView or ImageView as per your UI
                 coursePicker.setText(courseImageUri.toString());
            }
        }
    }




    private void fetchProfile() {
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false); // Prevent dismiss by tapping outside of the dialog
        progressDialog.show();

        String name = etName.getText().toString();
        String id = etID.getText().toString();
        String email = etEmail.getText().toString();
        String courses = coursePicker.getText().toString();

        Log.d("Profile Image URI", String.valueOf(imageUri));
        Log.d("Course Image URI", String.valueOf(courseImageUri)); // Assuming courseImageUri is the Uri for the course image

        if (imageUri != null && !Objects.requireNonNull(imageUri.getScheme()).startsWith("http")) {
            uploadImage(imageUri, "profile_images/", profileImageUri -> {
                if (courseImageUri != null && !Objects.requireNonNull(courseImageUri.getScheme()).startsWith("http")) {
                    uploadImage(courseImageUri, "course_images/", courseImageUri -> {
                        updateProfile(profileImageUri, name, id, email, courseImageUri.toString());
                    });
                } else {
                    updateProfile(profileImageUri, name, id, email, (String) coursePicker.getText());
                }
            });
        } else {
            if (courseImageUri != null && !Objects.requireNonNull(courseImageUri.getScheme()).startsWith("http")) {
                uploadImage(courseImageUri, "course_images/", courseImageUri -> {
                    updateProfile(null, name, id, email, courseImageUri.toString());
                });
            } else {
                updateProfile((String) null, name, id, email, (String) coursePicker.getText());
            }
        }
    }

    private void uploadImage(Uri imageUri, String path, Consumer<String> onSuccess) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            byte[] imageData = ImageUtils.compressImage(bitmap, 60);

            firebaseService.uploadImageToStorage(path, imageData)
                    .addOnSuccessListener(uri -> onSuccess.accept(uri.toString()))
                    .addOnFailureListener(e -> {
                        Toast.makeText(UpdateProfile.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
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
                        progressDialog.dismiss();
                        Toast.makeText(UpdateProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateProfile.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(UpdateProfile.this, "Profile Failed to Update", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
