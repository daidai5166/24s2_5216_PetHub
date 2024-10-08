package comp5216.sydney.edu.au.pethub.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;

import comp5216.sydney.edu.au.pethub.R;


public class AccountActivity extends AppCompatActivity {
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
        // 设置 account界面 Edit Profile 的点击事件监听
        findViewById(R.id.tv_edit_profile).setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
        // 设置 account界面 My Pet 的点击事件监听器
        findViewById(R.id.tv_my_pet).setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, MyPetsActivity.class);
            startActivity(intent);
        });
    }
}