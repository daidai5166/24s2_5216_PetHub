package comp5216.sydney.edu.au.pethub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView mForgotPassword;
    private Button mSignInButton;
    private Button mSignUpButton;
    private String email;
    private String password;
    private ConnectDatabase connectDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mEmailField = findViewById(R.id.edit_email);
        mPasswordField = findViewById(R.id.edit_password);
        mForgotPassword = findViewById(R.id.txt_forgot_password);
        mSignInButton = findViewById(R.id.btn_sign_in);
        mSignUpButton = findViewById(R.id.btn_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        connectDatabase = new ConnectDatabase();

        mForgotPassword.setOnClickListener(this::onForgotPasswordClick);
    }

    public void onSignInClick(View v) {
        email = mEmailField.getText().toString();
        password = mPasswordField.getText().toString();

        if (!checkEmailFormat(email)) {
            return;
        }
        if (!checkPasswordFormat(password)) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set global User object
                        MyApp app = (MyApp) getApplication();

                        connectDatabase.getUserByEmail(email, user -> {
                            if (user != null) {
                                Log.d("User Info", "User found: " + user.getUsername());
                                app.setUser(user);
                            } else {
                                Log.d("User Info", "User not found.");
                            }
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Firebase Auth", "signInWithEmail:success");
                            Toast.makeText(SignInActivity.this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInActivity.this, AccountActivity.class);
                            startActivity(intent);
                            finish();
                        }, e -> {
                            Log.e("Error", "Error fetching user");
                            Toast.makeText(SignInActivity.this, "Incorrect email or password.",
                                    Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Firebase Auth", "signInWithEmail:failure", task.getException());
                        Toast.makeText(SignInActivity.this, "Incorrect email or password.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onSignUpClick(View v) {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void onForgotPasswordClick(View v) {
        if (!checkEmailFormat(email)) {
            Toast.makeText(SignInActivity.this, "Incorrect email format, please enter" +
                            "the account email to reset password.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase Auth", "Email sent.");
                        Toast.makeText(SignInActivity.this, "A email to reset " +
                                        "password has been sent to the email above.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("Firebase Auth", "Email not sent.", task.getException());
                        Toast.makeText(SignInActivity.this, "Sent email failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
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
}