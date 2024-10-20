package comp5216.sydney.edu.au.pethub.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.model.Pet;
import comp5216.sydney.edu.au.pethub.adapters.PetAdapter;
import comp5216.sydney.edu.au.pethub.model.User;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;
import comp5216.sydney.edu.au.pethub.util.MarshmallowPermission;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private List<Pet> pets = new ArrayList<>();
    private PetAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;

    private double currentLatitude;
    private double currentLongitude;
    private Button btnDogs;
    private Button btnCats;
    private Button btnBirds;
    private Button btnOthers;
    private Button btnSearch;
    private TextView textSearch;
    private User myUser;
    private MyApp myApp;

    MarshmallowPermission marshmallowPermission = new MarshmallowPermission(this);
    private Map<String, List<Pet>> categorizedPets = new HashMap<>();

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

        // 获取位置
        getLastKnownLocation();

        if (!marshmallowPermission.checkPermissionForCamera()) {
            marshmallowPermission.requestPermissionForCamera();}

        btnDogs = findViewById(R.id.btn_dogs);
        btnCats = findViewById(R.id.btn_cats);
        btnBirds = findViewById(R.id.btn_birds);
        btnOthers = findViewById(R.id.btn_others);
        btnSearch = findViewById(R.id.btn_search);
        textSearch = findViewById(R.id.search_pet);

        myApp = (MyApp) this.getApplication();
        myUser = myApp.getUser();

        // 为标签设置点击事件
        setupTabClickListeners();

        // 初始化导航栏
        NavigationBarActivity navigationBarActivity = new NavigationBarActivity(this);
        navigationBarActivity.setupNavigationBar();

        // Main page 获取 GridView
        GridView gridView = findViewById(R.id.grid_pets);

        // 创建适配器并绑定数据
        adapter = new PetAdapter(this, pets);
        gridView.setAdapter(adapter);

        // GridView设置点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PetdetailsActivity.class);
                Pet selectedPet = pets.get(position);
                intent.putExtra("selectedPet", selectedPet);
                // 可根据点击的项传递宠物的相关数据
                startActivity(intent);
            }
        });
    }

    private void setupTabClickListeners() {
        btnDogs.setOnClickListener(v -> {
            resetButtonBackgrounds();
            btnDogs.setBackgroundColor(Color.parseColor("#9dbf85"));
            filterAndSortPetsByCategory("Dog");
        });

        btnCats.setOnClickListener(v -> {
            resetButtonBackgrounds();
            btnCats.setBackgroundColor(Color.parseColor("#9dbf85"));
            filterAndSortPetsByCategory("Cat");
        });

        btnBirds.setOnClickListener(v -> {
            resetButtonBackgrounds();
            btnBirds.setBackgroundColor(Color.parseColor("#9dbf85"));
            filterAndSortPetsByCategory("Bird");
        });

        btnOthers.setOnClickListener(v -> {
            resetButtonBackgrounds();
            btnOthers.setBackgroundColor(Color.parseColor("#9dbf85"));
            filterAndSortPetsByCategory("Other");
        });

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FindpetsActivity.class);
            startActivity(intent);
        });

        textSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FindpetsActivity.class);
            startActivity(intent);
        });
    }

    private void resetButtonBackgrounds() {
        Button btnDogs = findViewById(R.id.btn_dogs);
        Button btnCats = findViewById(R.id.btn_cats);
        Button btnBirds = findViewById(R.id.btn_birds);
        Button btnOthers = findViewById(R.id.btn_others);

        // Reset background to transparent for all buttons
        btnDogs.setBackgroundColor(Color.TRANSPARENT);
        btnCats.setBackgroundColor(Color.TRANSPARENT);
        btnBirds.setBackgroundColor(Color.TRANSPARENT);
        btnOthers.setBackgroundColor(Color.TRANSPARENT);
    }

    private void categorizePets() {
        for (Pet pet : pets) {
            String category = pet.getCategory().toLowerCase();
            categorizedPets.putIfAbsent(category, new ArrayList<>());
            categorizedPets.get(category).add(pet);
        }
    }

    private void filterAndSortPetsByCategory(String category) {
        List<Pet> filteredPets = categorizedPets.get(category.toLowerCase());
        if (filteredPets == null) {
            filteredPets = new ArrayList<>();
        }

        // 排序逻辑保持不变
        sortPetsByDistance(filteredPets);
        adapter.updatePets(filteredPets);
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
                            if (myUser == null ||
                                    !Objects.equals(ownerId, myUser.getFirebaseId()) ||
                                    !Objects.equals(adopterId, "")) {
                                pets.add(pet);
                            }

                            Log.i("PetAdoptionPostID", petID);
                            Log.d("PetAdoptionPost", pet.getPetName());
                            Log.d("PetAdoptionPost", "latitude: " + latitude + ", longitude:" + longitude);
                        } catch (Exception e) {
                            Log.e("PetAdoptionPost", "Error parsing document", e);
                        }
                    }

                    // 对宠物进行分类并填入HashMap
                    categorizePets();

                    // 在获取到当前位置后，进行排序
                    sortPetsByDistance(pets);

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
    public void sortPetsByDistance(List<Pet> sortPets) {
        if (currentLatitude == 0.0 && currentLongitude == 0.0) {
            // 未获取到当前位置时跳过排序
            Log.w("Sort", "Invalid Location");
            return;
        }

        // 先按距离升序排列
        sortPets.sort((pet1, pet2) -> {
            double distanceToPet1 = calculateDistance(currentLatitude, currentLongitude, pet1.getLatitude(), pet1.getLongitude());
            double distanceToPet2 = calculateDistance(currentLatitude, currentLongitude, pet2.getLatitude(), pet2.getLongitude());

            int distanceComparison = Double.compare(distanceToPet1, distanceToPet2);
            if (distanceComparison != 0) {
                return distanceComparison;
            }

            // 如果距离相等，再按时间降序排列
            return pet2.getUploadTime().compareTo(pet1.getUploadTime());
        });
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

                            // 获取宠物领养数据
                            fetchPetAdoptionPosts();
                        }
                    });
        } else {
            // 请求权限
            marshmallowPermission.requestPermissionForLocation();
        }
    }
}