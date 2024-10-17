package comp5216.sydney.edu.au.pethub;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import comp5216.sydney.edu.au.pethub.activity.AccountActivity;
import comp5216.sydney.edu.au.pethub.activity.FindpetsActivity;
import comp5216.sydney.edu.au.pethub.activity.PetblogActivity;
import comp5216.sydney.edu.au.pethub.activity.PetdetailsActivity;
import comp5216.sydney.edu.au.pethub.activity.PostpetActivity;
import comp5216.sydney.edu.au.pethub.activity.NavigationBarActivity;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.model.Pet;
import comp5216.sydney.edu.au.pethub.adapters.PetAdapter;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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

        // 初始化导航栏
        NavigationBarActivity navigationBarActivity = new NavigationBarActivity(this);
        navigationBarActivity.setupNavigationBar();

        // Main page 获取 GridView 并设置点击事件
        GridView gridView = findViewById(R.id.grid_pets);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PetdetailsActivity.class);
                // 可根据点击的项传递宠物的相关数据
                startActivity(intent);
            }
        });

        // 创建宠物数据
        List<Pet> pets = new ArrayList<>();

        /*pets.add(new Pet(
                "Buddy",
                3,
                true,
                "Friendly dog looking for a loving home",
                "Dog",
                "123 Park Avenue",
                40.7128,
                -74.0060,
                "owner123",
                null, // 还没有领养者
                Arrays.asList("user1", "user2"), // 感兴趣的用户列表
                Arrays.asList("https://example.com/dog1.jpg", "https://example.com/dog2.jpg"), // 图片 URI 列表
                Arrays.asList("My blog about dogs", "Why you should adopt Buddy"), // 博客标题
                successListener,
                failureListener
        ));

        pets.add(new Pet(
                "Whiskers",
                2,
                false,
                "Playful cat with a big heart",
                "Cat",
                "456 Elm Street",
                34.0522,
                -118.2437,
                "owner456",
                null,
                Arrays.asList("user3", "user4"),
                Arrays.asList("https://example.com/cat1.jpg", "https://example.com/cat2.jpg"),
                Arrays.asList("My experience with Whiskers", "Adopt a playful friend"),
                successListener,
                failureListener
        ));

        pets.add(new Pet(
                "Tweety",
                1,
                false,
                "Colorful bird who loves to sing",
                "Bird",
                "789 Oak Street",
                51.5074,
                -0.1278,
                "owner789",
                null,
                Arrays.asList("user5"),
                Arrays.asList("https://example.com/bird1.jpg"),
                Arrays.asList("How to care for birds", "Meet Tweety the singing bird"),
                successListener,
                failureListener
        ));*/

        // 创建适配器并绑定数据
        PetAdapter adapter = new PetAdapter(this, pets);
        gridView.setAdapter(adapter);

        ConnectDatabase connectDatabase = new ConnectDatabase();

        connectDatabase.getPetAdoptionPosts(
                queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            // 手动获取字段并调试数据
                            String petName = document.getString("petName");
                            int age = document.getLong("age").intValue();
                            boolean gender = document.getBoolean("gender");
                            String description = document.getString("description");
                            String category = document.getString("category");
                            String address = document.getString("address");
                            double longitude = document.getDouble("longitude");
                            double latitude = document.getDouble("latitude");
                            String ownerId = document.getString("ownerId");
                            String adopterId = document.getString("adopterId");
                            List<String> interestedUserIds = (List<String>) document.get("interestedUserIds");
                            List<String> uriStringList = (List<String>) document.get("uriStringList");
                            List<String> blogTitles = (List<String>) document.get("blogTitles");

                            // 构造 Pet 对象
                            Pet pet = new Pet(petName, age, gender, description, category, address, longitude, latitude, ownerId, adopterId, interestedUserIds, uriStringList, blogTitles);

                            // 添加到列表并刷新
                            pets.add(pet);
                            Log.d("PetAdoptionPost", pet.getPetName());
                        } catch (Exception e) {
                            Log.e("PetAdoptionPost", "Error parsing document", e);
                        }
                    }
                    // 数据更新后通知适配器刷新
                    adapter.notifyDataSetChanged();
                },
                e -> {
                    Log.e("PetAdoptionPost", "Error fetching pet adoption posts", e);
                }
        );
    }
}