package comp5216.sydney.edu.au.pethub.activity;


import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.loadImageFromFirebaseStorageToImageView;
import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.noCacheLoadImageFromFirebaseStorageToImageView;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.model.User;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;

public class AccountActivity extends AppCompatActivity {
    private FirebaseUser user;
    private User myUser;

    private TextView usernameField;
    private TextView emailField;
    private ImageView genderField;
    private ImageView avatarField;

    private MyApp myApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        myApp = (MyApp) getApplication();
        myUser = myApp.getUser();

        usernameField = findViewById(R.id.tv_user_name);
        emailField = findViewById(R.id.tv_email);
        genderField = findViewById(R.id.icon_user_gender);
        avatarField = findViewById(R.id.user_avatar);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (myUser != null) {
            // User is signed in
            usernameField.setText(myUser.getUsername());
            emailField.setText("Email: " + myUser.getEmail());
            if(myUser.getGender().equals("F")) {
                genderField.setImageResource(R.drawable.ic_gender_female);
            }else{
                genderField.setImageResource(R.drawable.ic_gender_male);
            }
        } else {
            // No user is signed in
            Intent intent = new Intent(AccountActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        };

        noCacheLoadImageFromFirebaseStorageToImageView(AccountActivity.this, avatarField, "Users/"+myUser.getFirebaseId()+"/avatar.jpg");

        // 初始化导航栏
        NavigationBarActivity navigationBarActivity = new NavigationBarActivity(this);
        navigationBarActivity.setupNavigationBar();

        findViewById(R.id.edit_profile).setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, EditProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }
}