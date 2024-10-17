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

    private List<Pet> pets = new ArrayList<>();
    private PetAdapter adapter;

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

        // 创建适配器并绑定数据
        adapter = new PetAdapter(this, pets);
        gridView.setAdapter(adapter);

        // 获取宠物领养数据
        fetchPetAdoptionPosts();
    }

    // 封装获取宠物领养帖的函数
    private void fetchPetAdoptionPosts() {
        ConnectDatabase connectDatabase = new ConnectDatabase();
        connectDatabase.getPetAdoptionPosts(
                queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            // 手动获取字段并调试数据
                            String petID = document.getId();
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
                            String uploadTime = document.getString("uploadTime");

                            // 构造 Pet 对象
                            Pet pet = new Pet(petID, petName, age, gender, description, category, address, longitude, latitude, ownerId, adopterId, interestedUserIds, uriStringList, uploadTime);
                          
                            // 添加到列表并刷新
                            pets.add(pet);
                            Log.i("PetAdoptionPostID", petID);
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