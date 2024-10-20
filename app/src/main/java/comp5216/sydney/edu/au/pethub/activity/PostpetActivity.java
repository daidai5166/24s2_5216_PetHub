package comp5216.sydney.edu.au.pethub.activity;


import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.adapters.ImageAdapter;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.model.Pet;
import comp5216.sydney.edu.au.pethub.model.User;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;
import comp5216.sydney.edu.au.pethub.util.MarshmallowPermission;

public class PostpetActivity extends AppCompatActivity {

    private MyApp myApp;
    private User myUser;
    private static final int PICK_IMAGES = 101;
    private static final int CAPTURE_IMAGE = 102;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 103;
    public final String APP_TAG = "Postpetactivity";
    private File file;
    private Uri photo_uri;

    private String pet_id="";
    private String pet_category="";
    private String petName="";
    private String petGender="";
    private String petAge="";
    private String petAddress="";
    private String petDescription="";
    private Double petLng=0.0;
    private Double petLat=0.0;

    private LinearLayout dogs_icons;
    private LinearLayout cats_icons;
    private LinearLayout birds_icons;
    private LinearLayout others_icons;
    private EditText mpetNameField;
    private EditText mpetGenderField;
    private EditText mpetAgeField;
    private EditText mpetAddressField;
    private EditText mpetDescriptionField;
    private Button mpostPetButton;
    private ImageView genderMale, genderFemale;

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Object> imageUris= new ArrayList<>();
    MarshmallowPermission marshmallowPermission = new MarshmallowPermission(this);
    ConnectDatabase connectDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_postpet);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // 判断 Intent 是否包含 pet 对象
        Intent intentFromMypets = getIntent();
        Pet pet = intentFromMypets.getParcelableExtra("selectedPet");

        // 获取用户
        myApp = (MyApp) getApplication();
        myUser = myApp.getUser();


        // 初始化数据库
        connectDatabase = new ConnectDatabase();

        // 初始化 RecyclerView 和 Adapter
        recyclerView = findViewById(R.id.recyclerViewImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(this, imageUris);
        recyclerView.setAdapter(imageAdapter);

        dogs_icons = findViewById(R.id.dogs_icon);
        cats_icons = findViewById(R.id.cats_icon);
        birds_icons = findViewById(R.id.birds_icon);
        others_icons = findViewById(R.id.others_icon);

        mpetNameField = findViewById(R.id.pet_name);
        genderMale = findViewById(R.id.gender_male);
        genderFemale = findViewById(R.id.gender_female);
        mpetAgeField = findViewById(R.id.pet_age);
        mpetAddressField = findViewById(R.id.pet_address);
        mpetDescriptionField = findViewById(R.id.pet_description);

        // gender click
        setGenderSelection(genderMale, genderFemale, "M", "male");
        setGenderSelection(genderFemale, genderMale, "F", "female");

        // 初始化 地址补全
        mpetAddressField.setOnClickListener(v -> {
            // Create an Autocomplete Intent and specify the fields to be returned
            List<Place.Field> fields = Arrays.asList(
                    Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        FrameLayout petImageUpload = findViewById(R.id.pet_image_upload);

        View.OnClickListener layoutClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLayoutClick(v);
            }
        };
        dogs_icons.setOnClickListener(layoutClickListener);
        cats_icons.setOnClickListener(layoutClickListener);
        birds_icons.setOnClickListener(layoutClickListener);
        others_icons.setOnClickListener(layoutClickListener);

        uploadPetImageClickListener(petImageUpload);
        if (pet != null){
            mpetNameField.setText(pet.getPetName());
            if (pet.isGender()){
                genderMale.setBackgroundColor(Color.parseColor("#9FE716"));
                petGender = "m";
            }else{
                genderFemale.setBackgroundColor(Color.parseColor("#9FE716"));
                petGender = "f";

            }
            // mpetGenderField.setText(pet.isGender()  ? "Male" : "Female");
            mpetAgeField.setText(String.valueOf(pet.getAge()));
            mpetAddressField.setText(pet.getAddress());
            mpetDescriptionField.setText(pet.getDescription());
            pet_id = pet.getPetID();
            petLat = pet.getLatitude();
            petLng = pet.getLongitude();
            // 根据 pet 的类别设置图标背景
            pet_category = pet.getCategory(); // 假设 Pet 对象中有 getCategory() 方法
            if (pet_category != null) {
                if (pet_category.equals("Dog")) {
                    handleLayoutClick(dogs_icons);
                } else if (pet_category.equals("Cat")) {
                    handleLayoutClick(cats_icons);
                } else if (pet_category.equals("Bird")) {
                    handleLayoutClick(birds_icons);
                } else if (pet_category.equals("Other")) {
                    handleLayoutClick(others_icons);
                }
            }
            for (int i = 0; i < pet.getUriStringList().size(); i++) {
                // 生成类似 image_01, image_02 的字符串
                String imageName = "Pets/" +pet.getPetID() +"/image_" + i + ".jpg"; //
                imageUris.add(imageName);
                imageAdapter.notifyDataSetChanged();
            }


        }


    }

    private void setGenderSelection(ImageView selectedView, ImageView otherView, String genderValue, String genderType) {
        // 为当前点击的 ImageView 设置点击事件
        selectedView.setOnClickListener(v -> {
            petGender = genderValue; // 设置当前选择的性别

            // 修改当前点击的 ImageView 背景为选中状态
            selectedView.setBackgroundColor(Color.parseColor("#9FE716"));

            // 重置另一个 ImageView 的背景为默认状态
            otherView.setBackgroundColor(Color.TRANSPARENT);
            // System.out.println(selectedGender);
        });
    }


    //  点击类别的事件
    private void handleLayoutClick(View view) {
        // 首先重置所有按钮的背景
        resetAllButtonsBackground();
        if (view.getId() == R.id.dogs_icon) {
            pet_category = "Dog";
            view.setBackgroundColor(Color.parseColor("#9dbf85"));
            System.out.println(pet_category);
        } else if (view.getId() == R.id.cats_icon) {
            pet_category = "Cat";
            view.setBackgroundColor(Color.parseColor("#9dbf85"));
            System.out.println(pet_category);
        } else if (view.getId() == R.id.birds_icon) {
            pet_category = "Bird";
            view.setBackgroundColor(Color.parseColor("#9dbf85"));
            System.out.println(pet_category);
        } else if (view.getId() == R.id.others_icon) {
            pet_category = "Other";
            view.setBackgroundColor(Color.parseColor("#9dbf85"));
            System.out.println(pet_category);
        }
    }

    // 重置所有按钮的背景到默认状态
    private void resetAllButtonsBackground() {
        dogs_icons.setBackgroundResource(0);  // Restore default background
        cats_icons.setBackgroundResource(0);  // Restore default background
        birds_icons.setBackgroundResource(0);  // Restore default background
        others_icons.setBackgroundResource(0);  // Restore default background
    }

    //  Click event for binding upload button
    private void uploadPetImageClickListener(FrameLayout frameLayout) {
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The operation performed when clicking the upload button
                showBottomSheetDialog();
            }
        });
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);

        // Load custom layout for BottomSheetDialog
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);


        bottomSheetView.findViewById(R.id.take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetView.findViewById(R.id.upload_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImagesFromGallery();
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }




    private void openCamera() {
        if (!marshmallowPermission.checkPermissionForCamera()) {
            marshmallowPermission.requestPermissionForCamera();}
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String photoFileName = "IMG_" + timeStamp + ".jpg";

        photo_uri = getFileUri(photoFileName); // 生成路径
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photo_uri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE);
        }
    }


    private void selectImagesFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);  // 允许多选
        startActivityForResult(intent, PICK_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE) {

                // System.out.println(photo_uri);
                scanFile(file.getAbsolutePath());
                imageUris.add(photo_uri);

                imageAdapter.notifyDataSetChanged();
            } else if (requestCode == PICK_IMAGES && data != null) {
                // 从相册返回的结果
                if (data.getClipData() != null) {
                    // 用户选择了多张图片
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);
                }
                imageAdapter.notifyDataSetChanged();
            }
            System.out.println(imageUris);
        }
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // 获取用户选择的地点
                Place place = Autocomplete.getPlaceFromIntent(data);
                // 将完整地址显示在 EditText 中
                mpetAddressField.setText(place.getAddress());
                petAddress = place.getAddress();
                Log.i("Places", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                LatLng latLng = place.getLatLng();
                if (latLng != null){
                    petLat = latLng.latitude;
                    petLng = latLng.longitude;
                    Log.i("location", petLat+" "+petLng);
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // 处理错误
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("Places", "Error: " + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // 用户取消了操作
                Log.i("Places", "Autocomplete canceled");
            }
        }
    }

    // Returns the Uri for a photo/media stored on disk given the fileName and type
    public Uri getFileUri(String fileName) {
        Uri fileUri = null;
        try {

            File mediaStorageDir = new File(getExternalMediaDirs()[0], APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d(APP_TAG, "failed to create directory");
            }

            // Create the file target for the media based on filename
            file = new File(mediaStorageDir, fileName);

            // Wrap File object into a content provider, required for API >= 24
            // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
            if (Build.VERSION.SDK_INT >= 24) {
                fileUri = FileProvider.getUriForFile(this.getApplicationContext(), "comp5216.sydney.edu.au.pethub.fileProvider", file);
            } else {
                fileUri = Uri.fromFile(mediaStorageDir);
            }
        } catch (Exception ex) {
            Log.e("getFileUri", ex.getStackTrace().toString());
        }
        return fileUri;
    }

    private void scanFile(String path) {

        MediaScannerConnection.scanFile(PostpetActivity.this,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }


    public boolean checkNameInput(String name) {

        if (name.isEmpty()) {
            mpetNameField.setError("This field is required."); // 设置错误消息
            mpetNameField.requestFocus(); // 聚焦到该输入框
            return false;
        }
        return true;
    }

    public boolean checkGenderInput(String gender) {
        if (gender.isEmpty()) {
            Toast.makeText(PostpetActivity.this, "Please select your gender.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean checkAgeInput(String age) {
        // 检查是否为空
        if (age.isEmpty()) {
            mpetAgeField.setError("Age is required.");
            mpetAgeField.requestFocus();
            return false;
        }
        // 检查是否为数字
        if (!TextUtils.isDigitsOnly(age)) {
            mpetAgeField.setError("Please enter a valid number.");
            mpetAgeField.requestFocus();
            return false;
        }
        return true; // 如果 age 不为空且为数字，返回 true
    }

    public void onPostPetClick(View v){
        petName = mpetNameField.getText().toString().trim();
        // petGender = mpetGenderField.getText().toString().trim();
        petAge = mpetAgeField.getText().toString().trim();
        petDescription = mpetDescriptionField.getText().toString().trim();
        petAddress = mpetAddressField.getText().toString().trim();
        List<String> uriStringList = new ArrayList<>();
        // 创建一个用来保存图片名字的列表
        List<String> imageNames = new ArrayList<>();

        String uploadTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // 遍历 imageUris，根据每个 Uri 的索引生成文件名
        for (int i = 0; i < imageUris.size(); i++) {
            // 可以根据索引生成文件名，例如 "image_0.jpg", "image_1.jpg" 等
            String imageName = "/image_" + i + ".jpg";
            imageNames.add(imageName);
        }

        // 检查name
        if (!checkNameInput(petName)){
            return;
        }
        // 检查gender
        if (!checkGenderInput(petGender)){
            return;
        }
        // 检查age
        if (!checkAgeInput(petAge)){
            return;
        }
        // 检查category
        if (pet_category.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.selectPetCategoryWarn, Toast.LENGTH_SHORT).show();
            return;
        }
        // 检查Description
        if (petDescription.isEmpty()){
            petDescription = getString(R.string.petEmptyDescription);
        }

        if (imageUris != null){
            // 将每个 Uri 转换为字符串
            for (Object uri : imageUris) {
                uriStringList.add(uri.toString());
            }
        }
        int age = Integer.parseInt(petAge); // 转换petAge格式
        boolean gender = petGender.equalsIgnoreCase("m"); //转化petGender格式到Boolean, male为true, female为false
        String ownerId=myUser.getFirebaseId();//myUser.getFirebaseId();//用户firebase的ID
        String adopterId="";//新宠物暂无领养人
        List<String> interestedUserIds=new ArrayList<>();//新宠物暂无兴趣人
        System.out.println(imageUris);
        connectDatabase.addPetAdoptionPost(pet_id,
                petName,
                age,
                gender,
                petDescription,
                pet_category,
                petAddress,
                petLng,
                petLat,
                ownerId,
                adopterId,
                interestedUserIds,
                uriStringList,
                uploadTime,
                documentId -> {
                    int[] uploadCount = {0}; // 计数器
                    for (int i = 0; i < imageUris.size(); i++) {
                        Object imageObject = imageUris.get(i);
                        // 判断是否是 Uri 类型
                        if (imageObject instanceof Uri) {
                        Uri imageUri = (Uri) imageObject;        // 获取当前图片的 Uri
                        String imageName = imageNames.get(i);   // 获取当前图片的名称

                        // 调用上传函数，传入图片名称和 Uri
                        connectDatabase.uploadPetImage(this, documentId, imageUri, imageName,
                                // 成功回调
                                downloadUri -> {
                                    Log.d("FirestoreDatabase", "Image uploaded successfully: " + imageName);
                                    // 增加上传成功的计数
//                                    uploadCount[0]++;
//
//                                    // 如果所有图片上传完毕
//                                    if (uploadCount[0] == imageUris.size()) {
//                                        // 调用数据库读取操作并跳转
//                                        Toast.makeText(PostpetActivity.this, "Upload success.",
//                                                Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(PostpetActivity.this, MypetsActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                    }
                                },
                                // 失败回调
                                e -> {
                                    Log.e("FirestoreDatabase", "Failed to upload image: " + imageName, e);
                                }
                        );

                    }}
                    Toast.makeText(PostpetActivity.this, "Upload success.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PostpetActivity.this, MypetsActivity.class);
                    startActivity(intent);
                    finish();
                },
                e ->{
                    Log.e("FirestoreDatabase", "Error uploading pet");
                });
    }
    public void goBack(View view){
        onBackPressed();
        finish();
    }

}