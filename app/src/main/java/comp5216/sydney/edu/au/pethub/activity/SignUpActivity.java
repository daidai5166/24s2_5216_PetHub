package comp5216.sydney.edu.au.pethub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

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

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String retypePassword;

    ConnectDatabase connectDatabase;

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

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        storageReference = mFirebaseStorage.getReference();

        connectDatabase = new ConnectDatabase();
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

        if (!checkPasswordMatch(password, retypePassword)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    uploadAvatarToFirebase();

                    // TODO: Wait for front-end to finish Gender component
                    connectDatabase.addUser(
                            firstName + " " + lastName,
                            "O",
                            email,
                            0,
                            " ",
                            " ");
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Firebase", "createUserWithEmail:success");
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Firebase", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignUpActivity.this, "Sign up failed, " +
                                    "please try again.",
                            Toast.LENGTH_SHORT).show();
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

    public void uploadAvatarToFirebase() {
        // TODO: Wait for Liu to finish the database operation
//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            // Create a storage reference from our app
//            StorageReference storageRef = storageReference.child(
//                    "avatar/" + cityName + "/" + file.getName()
//            );
//            // Upload file to Firebase Storage
//            storageRef.putFile(Uri.fromFile(file))
//                    .addOnSuccessListener(taskSnapshot -> {
//                        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                            // add to the database
//                            mediaItemDao.insert(mediaItem);
//                        });
//                        try {
//                            future.get();
//                        } catch (ExecutionException | InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                        // Get a URL to the uploaded content
//                        Toast.makeText(MainActivity.this, "Upload successful",
//                                Toast.LENGTH_SHORT).show();
//                        Log.d("Firebase", "Upload successful");
//                    })
//                    .addOnFailureListener(exception -> {
//                        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                            // add to the database
//                            mediaItem.setBackup(false);
//                            mediaItemDao.insert(mediaItem);
//                        });
//                        try {
//                            future.get();
//                        } catch (ExecutionException | InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                        // Handle unsuccessful uploads
//                        Toast.makeText(MainActivity.this,
//                                "Upload failed", Toast.LENGTH_SHORT).show();
//                        Log.d("Firebase", "Upload failed");
//                    });
//        });
    }
}
