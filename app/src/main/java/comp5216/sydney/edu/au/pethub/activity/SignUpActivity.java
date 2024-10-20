package comp5216.sydney.edu.au.pethub.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.drawable.BitmapDrawable;

import androidx.appcompat.app.AppCompatActivity;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.IOException;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseStorage mFirebaseStorage;
    StorageReference storageReference;

    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mRetypePasswordField;
    private ImageButton mSignUpButton;
    private FrameLayout ivPreview;
    private ImageView genderMale, genderFemale;

    private String selectedGender = "" ; // Used to store the selected gender
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String retypePassword;

    ConnectDatabase connectDatabase;

    Uri avatarUri;
    Bitmap scaledAvatar;

    private static final int MY_PERMISSIONS_REQUEST_READ_PHOTOS = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mFirstNameField = findViewById(R.id.first_name);
        mLastNameField = findViewById(R.id.last_name);
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        mRetypePasswordField = findViewById(R.id.retype_password);
        mSignUpButton = findViewById(R.id.btn_sign_up);
        genderMale = findViewById(R.id.gender_male);
        genderFemale = findViewById(R.id.gender_female);

        // gender click
        setGenderSelection(genderMale, genderFemale, "M", "male");
        setGenderSelection(genderFemale, genderMale, "F", "female");
        ivPreview = findViewById(R.id.upload_button);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        storageReference = mFirebaseStorage.getReference();

        connectDatabase = new ConnectDatabase();

        ivPreview.setOnClickListener(v -> {
            selectAvatarImage();
        });
    }


    /**
     *Encapsulate gender selection logic, including background switching and setting gender values
     *@ paramselectedView The currently clicked ImageView (gender icon)
     *@ paramotherView ImageView of another gender (background reset required)
     *@ paramgenderValue is the value of gender, where "F" represents male and "M" represents female
     *@ paramgenderType gender type, used to switch the identification of the background
     */
    private void setGenderSelection(ImageView selectedView, ImageView otherView, String genderValue, String genderType) {
        // Set click events for the currently clicked ImageView
        selectedView.setOnClickListener(v -> {
            selectedGender = genderValue; // Set the currently selected gender

            // Modify the background of the currently clicked ImageView to the selected state
            selectedView.setBackgroundColor(Color.parseColor("#9FE716"));

            // Reset the background of another ImageView to its default state
            otherView.setBackgroundColor(Color.TRANSPARENT);
            // System.out.println(selectedGender);
        });
    }

    public void onSignUpClick(View v) {
        firstName = mFirstNameField.getText().toString();
        lastName = mLastNameField.getText().toString();
        email = mEmailField.getText().toString();
        password = mPasswordField.getText().toString();
        retypePassword = mRetypePasswordField.getText().toString();

        if (!checkNameFormat(firstName, lastName)) {
            return;
        }

        if (!checkEmailFormat(email)) {
            return;
        }

        if (!checkPasswordFormat(password)) {
            return;
        }

        if(selectedGender.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please select your gender.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!checkPasswordMatch(password, retypePassword)) {
            return;
        }

        mSignUpButton.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    selectAvatarImage();
                    connectDatabase.addUser(
                            firstName + " " + lastName,
                            selectedGender,
                            email,
                            0,
                            "",
                            "", id -> {
                                // Upload using the returned ID as the file name
                                connectDatabase.uploadUserAvatar(id, scaledAvatar, uri -> {
                                        Log.d("Firebase", "Avatar uploaded");
                                    }, e -> {
                                        Log.e("Error", "Error updating avatar");
                                    });
                                }, e -> {
                                    Log.e("Error", "Error adding user");
                                });

                    // Sign up success, update UI with the signed-in user's information
                    Log.d("Firebase", "createUserWithEmail:success");

                    Toast.makeText(SignUpActivity.this, "Sign up successfully, " +
                                    "please sign in your account.",
                            Toast.LENGTH_SHORT).show();

                    mSignUpButton.setEnabled(true);
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Firebase", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignUpActivity.this, "Sign up failed, " +
                                    "please try again.",
                            Toast.LENGTH_SHORT).show();
                    mSignUpButton.setEnabled(true);
                }
            });
    }

    public boolean checkNameFormat(String firstName, String lastName) {
        if (firstName.isEmpty()) {
            mFirstNameField.setError("First name is required.");
            mFirstNameField.requestFocus();
            return false;
        }
        if (lastName.isEmpty()) {
            mLastNameField.setError("Last name is required.");
            mLastNameField.requestFocus();
            return false;
        }
        return true;
    }

    public boolean checkEmailFormat(String email) {
        if (email.isEmpty()) {
            mEmailField.setError("Email is required.");
            mEmailField.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailField.setError("Please enter a valid email.");
            mEmailField.requestFocus();
            return false;
        }
        return true;
    }

    public boolean checkPasswordFormat(String password) {
        if (password.isEmpty()) {
            mPasswordField.setError("Password is required.");
            mPasswordField.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            mPasswordField.setError("Password must be at least 6 characters.");
            mPasswordField.requestFocus();
            return false;
        }
        return true;
    }

    public boolean checkPasswordMatch(String password, String retypePassword) {
        if (!password.equals(retypePassword)) {
            mRetypePasswordField.setError("Passwords do not match.");
            mRetypePasswordField.requestFocus();
            return false;
        }
        return true;
    }

    public void selectAvatarImage() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Bring up gallery to select a photo
        startActivityForResult(intent, MY_PERMISSIONS_REQUEST_READ_PHOTOS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHOTOS) {
            // Load the selected image into a preview
            if (resultCode == RESULT_OK) {
                avatarUri = data.getData();
                Bitmap selectedImage;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), avatarUri);

                    // Get the width and height of ivPreview
                    int targetWidth = ivPreview.getWidth();
                    int targetHeight = ivPreview.getHeight();

                    // Scale the bitmap to the size of ivPreview
                    scaledAvatar = Bitmap.createScaledBitmap(
                            selectedImage, targetWidth, targetHeight, true);

                    // Set the scaled image as background of ivPreview
                    ivPreview.setBackground(new BitmapDrawable(getResources(), scaledAvatar));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
