package comp5216.sydney.edu.au.pethub.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.adapters.PetAdapter;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.model.Pet;

public class FindpetsActivity extends AppCompatActivity {

    String selectedGender = "M";
    Spinner spinnerPetType;
    Spinner spinnerLocation;
    private ImageView genderMale, genderFemale;

    ArrayList<Pet> pets;
    PetAdapter petAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_findpets);
        genderMale = findViewById(R.id.gender_male);
        genderFemale = findViewById(R.id.gender_female);

        setGenderSelection(genderMale, genderFemale, "M", "male");
        setGenderSelection(genderFemale, genderMale, "F", "female");
        genderMale.setBackgroundColor(Color.parseColor("#9FE716"));

        // 获取 Spinner
        spinnerPetType = findViewById(R.id.spinner_pet_type);
        // 获取 string-array 资源
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pet_types, android.R.layout.simple_spinner_item);

        // 设置下拉样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 绑定 Adapter 到 Spinner
        spinnerPetType.setAdapter(adapter);

        spinnerLocation = findViewById(R.id.spinner_location);

        // 获取 string-array 资源
        ArrayAdapter<CharSequence> adapterLocation = ArrayAdapter.createFromResource(this,
                R.array.search_pet_locations, android.R.layout.simple_spinner_item);

        // 设置下拉样式
        adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 设置下拉样式
        adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 绑定 Adapter 到 Spinner
        spinnerLocation.setAdapter(adapterLocation);

        // Search page 获取 GridView 并设置点击事件
        GridView gridView = findViewById(R.id.grid_pets);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(FindpetsActivity.this, PetdetailsActivity.class);
            // 可根据点击的项传递宠物的相关数据
            startActivity(intent);
        });

        petAdapter = new PetAdapter(this, pets);
        gridView.setAdapter(petAdapter);

        // 初始化导航栏
        NavigationBarActivity navigationBarActivity = new NavigationBarActivity(this);
        navigationBarActivity.setupNavigationBar();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fetchPetAdoptionPostsWithFilter();
    }

    /**
     * 封装性别选择逻辑，包括背景切换和性别值的设置
     * @param selectedView 当前点击的 ImageView（性别图标）
     * @param otherView 另一个性别的 ImageView（需重置背景）
     * @param genderValue 性别的值，"F" 表示男性，"M" 表示女性
     * @param genderType 性别类型，用于切换背景的标识
     */
    private void setGenderSelection(ImageView selectedView, ImageView otherView, String genderValue, String genderType) {
        // 为当前点击的 ImageView 设置点击事件
        selectedView.setOnClickListener(v -> {
            selectedGender = genderValue; // 设置当前选择的性别

            // 修改当前点击的 ImageView 背景为选中状态
            selectedView.setBackgroundColor(Color.parseColor("#9FE716"));

            // 重置另一个 ImageView 的背景为默认状态
            otherView.setBackgroundColor(Color.TRANSPARENT);
            // System.out.println(selectedGender);
        });
    }

    // 封装获取宠物领养帖的函数
    private void fetchPetAdoptionPostsWithFilter() {
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

                            // 构造 Pet 对象
                            Pet pet = new Pet(
                                    petID,
                                    petName,
                                    age,
                                    gender,
                                    description,
                                    category,
                                    address,
                                    longitude,
                                    latitude,
                                    ownerId,
                                    adopterId,
                                    interestedUserIds,
                                    uriStringList,
                                    blogTitles
                            );

                            // 添加到列表并刷新
                            pets.add(pet);
                            Log.d("PetAdoptionPost", pet.getPetName());
                        } catch (Exception e) {
                            Log.e("PetAdoptionPost", "Error parsing document", e);
                        }
                    }
                    // 数据更新后通知适配器刷新
                    petAdapter.notifyDataSetChanged();
                }, e -> {

                });
    }
}