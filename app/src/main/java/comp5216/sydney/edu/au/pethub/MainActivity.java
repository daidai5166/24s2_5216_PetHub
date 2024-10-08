package comp5216.sydney.edu.au.pethub;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import comp5216.sydney.edu.au.pethub.activity.AccountActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Main page 获取 Account 按钮并设置点击事件
        findViewById(R.id.btn_account).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
        });
        // Main page 获取 Blog 按钮并设置点击事件
        findViewById(R.id.btn_blog).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PetBlogActivity.class);
            startActivity(intent);
        });
        // Main page 获取 Post 按钮并设置点击事件
        findViewById(R.id.btn_post).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PostPetActivity.class);
            startActivity(intent);
        });
        // Main page 获取 Search bottom 按钮并设置点击事件
        findViewById(R.id.btn_search_bottom).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FindPetsActivity.class);
            startActivity(intent);
        });
        // Main page 获取另一个顶部的 Search 按钮并设置点击事件
        findViewById(R.id.btn_search).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FindPetsActivity.class);
            startActivity(intent);
        });
        // Main page 获取 GridView 并设置点击事件
        GridView gridView = findViewById(R.id.grid_pets);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PetDetailsActivity.class);
                // 可根据点击的项传递宠物的相关数据
                startActivity(intent);
            }
        });
    }
}