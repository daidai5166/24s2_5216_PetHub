package comp5216.sydney.edu.au.pethub;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;

import comp5216.sydney.edu.au.pethub.activity.PetdetailsActivity;
import comp5216.sydney.edu.au.pethub.activity.NavigationBarActivity;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.model.Pet;
import comp5216.sydney.edu.au.pethub.adapters.PetAdapter;
import comp5216.sydney.edu.au.pethub.util.MarshmallowPermission;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Pet> pets = new ArrayList<>();
    private PetAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;
    MarshmallowPermission marshmallowPermission = new MarshmallowPermission(this);

    // 全局变量
    private double currentLatitude;
    private double currentLongitude;

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

        // 获取位置
        getLastKnownLocation();
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
                            String uploadTime = document.getString("uploadTime");

                            // 构造 Pet 对象
                            Pet pet = new Pet(
                                    petID,
                                    petName,
                                    age, gender,
                                    description,
                                    category,
                                    address,
                                    longitude,
                                    latitude,
                                    ownerId,
                                    adopterId,
                                    interestedUserIds,
                                    uriStringList,
                                    uploadTime);
                          
                            // 添加到列表并刷新
                            pets.add(pet);
                            Log.i("PetAdoptionPostID", petID);
                            Log.d("PetAdoptionPost", pet.getPetName());
                        } catch (Exception e) {
                            Log.e("PetAdoptionPost", "Error parsing document", e);
                        }
                    }

                    // 在获取到当前位置后，进行排序
                    sortPetsByDistance();

                    // 数据更新后通知适配器刷新
                    adapter.notifyDataSetChanged();
                },
                e -> {
                    Log.e("PetAdoptionPost", "Error fetching pet adoption posts", e);
                }
        );
    }

    // 计算两个经纬度之间的距离，返回结果为米
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0]; // 返回米为单位的距离
    }

    // 对pets列表按照距离排序
    public void sortPetsByDistance() {
        if (currentLatitude == 0.0 && currentLongitude == 0.0) {
            // 未获取到当前位置时跳过排序
            Log.w("Sort", "当前位置无效，无法排序");
            return;
        }

        // 使用Collections.sort进行排序
        Collections.sort(pets, (pet1, pet2) -> {
            double distanceToPet1 = calculateDistance(currentLatitude, currentLongitude, pet1.getLatitude(), pet1.getLongitude());
            double distanceToPet2 = calculateDistance(currentLatitude, currentLongitude, pet2.getLatitude(), pet2.getLongitude());

            // 按距离升序排列
            return Double.compare(distanceToPet1, distanceToPet2);
        });

        // 更新UI
//        adapter.notifyDataSetChanged();
    }

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (marshmallowPermission.checkPermissionForLocation()) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();
                            Log.d("Location", "Latitude: " + currentLatitude + ", Longitude: " + currentLongitude);
                        }
                    });
        } else {
            // 请求权限
            marshmallowPermission.requestPermissionForLocation();
        }
    }
}