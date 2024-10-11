package comp5216.sydney.edu.au.pethub.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import comp5216.sydney.edu.au.pethub.MainActivity;
import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.model.User;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;

public class AccountActivity extends AppCompatActivity {
    private FirebaseUser user;
    private User myUser;

    private TextView usernameField;
    private TextView emailField;

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

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (myUser != null) {
            // User is signed in
            usernameField.setText(myUser.getUsername());
            emailField.setText("Email: " + myUser.getEmail());
        } else {
            // No user is signed in
            Intent intent = new Intent(AccountActivity.this, SignInActivity.class);
            startActivity(intent);
        };
        // 设置 account界面 Edit Profile 的点击事件监听
        findViewById(R.id.tv_edit_profile).setOnClickListener(v -> {
                    Intent intent = new Intent(AccountActivity.this, EditprofileActivity.class);
                    startActivity(intent);
                });
        // 设置 account界面 My Pet 的点击事件监听器
        findViewById(R.id.tv_my_pet).setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, MypetsActivity.class);
            startActivity(intent);
        });
        // 设置 account界面 My Blog 的点击事件监听器
        findViewById(R.id.tv_my_blog).setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, MyBlogsActivity.class);
            startActivity(intent);
        });
        // 设置 account界面 My Message 的点击事件监听器
        findViewById(R.id.tv_my_message).setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, MyMessageList.class);
            startActivity(intent);
        });

        // Main page 获取 Blog 按钮并设置点击事件
        findViewById(R.id.btn_blog).setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, PetblogActivity.class);
            startActivity(intent);
        });
        // Main page 获取 Post 按钮并设置点击事件
        findViewById(R.id.btn_post).setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, PostpetActivity.class);
            startActivity(intent);
        });
        // Main page 获取 Search bottom 按钮并设置点击事件
        findViewById(R.id.btn_search_bottom).setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, FindpetsActivity.class);
            startActivity(intent);
        });

        //没登录的话跳转到登录功能
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
        } else {
            // No user is signed in
            Intent intent = new Intent(AccountActivity.this, SignInActivity.class);
            startActivity(intent);
        }
    }
}