package comp5216.sydney.edu.au.pethub.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.adapters.PetAdapter;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.model.Pet;
import comp5216.sydney.edu.au.pethub.model.User;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;

public class FindpetsActivity extends AppCompatActivity {

    Boolean selectedGender = true;
    Spinner spinnerPetType;
    EditText spinnerLocation;
    private ImageView genderMale, genderFemale;
    Button searchButton;

    ArrayList<Pet> pets;
    PetAdapter petAdapter;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_findpets);
        searchButton = findViewById(R.id.btn_search);
        genderMale = findViewById(R.id.gender_male);
        genderFemale = findViewById(R.id.gender_female);

        setGenderSelection(genderMale, genderFemale, true, "male");
        setGenderSelection(genderFemale, genderMale, false, "female");
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
        MyApp myApp = (MyApp) getApplication();
        user = myApp.getUser();
//        // 获取 string-array 资源
//        ArrayAdapter<CharSequence> adapterLocation = ArrayAdapter.createFromResource(this,
//                R.array.search_pet_locations, android.R.layout.simple_spinner_item);
//
//        // 设置下拉样式
//        adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        // 设置下拉样式
//        adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        // 绑定 Adapter 到 Spinner
//        spinnerLocation.setAdapter(adapterLocation);

        // Search page 获取 GridView 并设置点击事件
        GridView gridView = findViewById(R.id.grid_pets);
        // GridView设置点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FindpetsActivity.this, PetdetailsActivity.class);
                Pet selectedPet = pets.get(position);
                intent.putExtra("selectedPet", selectedPet);
                // 可根据点击的项传递宠物的相关数据
                startActivity(intent);
            }
        });



        // 初始化导航栏
        NavigationBarActivity navigationBarActivity = new NavigationBarActivity(this);
        navigationBarActivity.setupNavigationBar();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        searchButton.setOnClickListener(v -> {
            pets = new ArrayList<>();
            // 重新获取数据
            fetchPetAdoptionPostsWithFilter();
            petAdapter = new PetAdapter(this, pets);
            gridView.setAdapter(petAdapter);
        });
    }

    /**
     * 封装性别选择逻辑，包括背景切换和性别值的设置
     * @param selectedView 当前点击的 ImageView（性别图标）
     * @param otherView 另一个性别的 ImageView（需重置背景）
     * @param genderValue 性别的值，true 表示男性，false 表示女性
     * @param genderType 性别类型，用于切换背景的标识
     */
    private void setGenderSelection(ImageView selectedView, ImageView otherView, Boolean genderValue, String genderType) {
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
        String selectedCategory = spinnerPetType.getSelectedItem().toString();
        String selectedLocation = spinnerLocation.getText().toString();
        Log.i("Filter", "Category: " + selectedCategory + " Gender: " + selectedGender + " Location: " + selectedLocation);
        ConnectDatabase connectDatabase = new ConnectDatabase();
        connectDatabase.getPetAdoptionPostsByFilter(selectedCategory, selectedGender, selectedLocation,
                queryDocumentSnapshots -> {
                    Log.i("FindpetsActivity", "fetchPetAdoptionPostsWithFilter: " + queryDocumentSnapshots.size());
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
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
                                    uploadTime
                            );

                            if(user == null || !Objects.equals(user.getFirebaseId(), ownerId)) {
                                // 添加到列表并刷新
                                pets.add(pet);
                            }

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