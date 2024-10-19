package comp5216.sydney.edu.au.pethub.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import comp5216.sydney.edu.au.pethub.R;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import comp5216.sydney.edu.au.pethub.adapters.ImageAdapter;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.model.User;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;
import comp5216.sydney.edu.au.pethub.util.MarshmallowPermission;

public class SharepetstoryActivity extends AppCompatActivity {
    private MyApp myApp;
    private User myUser;
    private static final int PICK_IMAGES = 101;
    private static final int CAPTURE_IMAGE = 102;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 103;
    public final String APP_TAG = "Sharepetstoryactivity";
    private File file;
    private Uri photo_uri;

    private String blogTitle="";
    private String mblogDescription="";

    private EditText mblogNameField;
    private EditText mblogDescriptionField;

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Object> imageUris = new ArrayList<>();
    MarshmallowPermission marshmallowPermission = new MarshmallowPermission(this);
    ConnectDatabase connectDatabase;
    private Spinner selectPetSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sharepetstory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // 获取用户
        myApp = (MyApp) getApplication();
        myUser = myApp.getUser();
        // 初始化数据库
        connectDatabase = new ConnectDatabase();
        // 初始化 RecyclerView 和 Adapter
        recyclerView = findViewById(R.id.share_recyclerViewImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(this, imageUris);
        recyclerView.setAdapter(imageAdapter);

        mblogNameField = findViewById(R.id.blog_title);
        mblogDescriptionField = findViewById(R.id.blog_description);
        mblogDescriptionField.setMaxLines(6);
        mblogDescriptionField.setEllipsize(TextUtils.TruncateAt.END);

        FrameLayout blogImageUpload = findViewById(R.id.blog_image_upload);

        uploadPetImageClickListener(blogImageUpload);

        //读取用户的宠物数据，++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        selectPetSpinner = findViewById(R.id.select_pet_spinner);
        List<String> petNames = new ArrayList<>();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, petNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectPetSpinner.setAdapter(spinnerAdapter);

        // 获取当前用户的 Firebase ID
        String userId = myUser.getFirebaseId();

        // 从 Firebase Firestore 中获取该用户的宠物信息
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PetAdoptionPost")
                .whereEqualTo("ownerId", userId)  // 根据用户 ID 过滤宠物数据
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    petNames.clear();  // 清空之前的数据
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String petName = document.getString("petName");  // 获取宠物的名字
                        petNames.add(petName);  // 添加到宠物名字的列表
                    }
                    spinnerAdapter.notifyDataSetChanged();  // 更新 Spinner 中的数据显示
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SharepetstoryActivity.this, "Failed to load pets.", Toast.LENGTH_SHORT).show();
                });

        // 设置 Spinner 的监听器
        selectPetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 获取用户选中的宠物名称
                String selectedPetName = parent.getItemAtPosition(position).toString();
                // 在这里可以使用 selectedPetName，或者存储为全局变量
                Log.d("SelectedPet", "用户选择了宠物: " + selectedPetName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项目时的处理
            }
        });

        findViewById(R.id.post_blog_back_button).setOnClickListener(v -> {
            // 清空用户输入的内容
            mblogNameField.setText("");  // 清空博客标题输入
            mblogDescriptionField.setText("");  // 清空博客内容输入

            // 清空图片列表并刷新 RecyclerView
            imageUris.clear();
            imageAdapter.notifyDataSetChanged();

            Intent intent = new Intent(SharepetstoryActivity.this, MyBlogsActivity.class);
            startActivity(intent);

            // 结束当前活动，防止回退到该界面时数据仍然存在
            finish();
        });
    }

    //  绑定上传按钮的点击事件
    private void uploadPetImageClickListener(FrameLayout frameLayout) {
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击上传按钮时执行的操作
                showBottomSheetDialog();
            }
        });
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);

        // 为 BottomSheetDialog 加载自定义布局
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);

        // 处理拍照按钮点击事件
        bottomSheetView.findViewById(R.id.take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
                bottomSheetDialog.dismiss();
            }
        });

        // 处理上传按钮点击事件
        bottomSheetView.findViewById(R.id.upload_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImagesFromGallery();
                bottomSheetDialog.dismiss();
            }
        });

        // 处理取消按钮点击事件
        bottomSheetView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        // 设置自定义视图并显示
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    // 打开相机拍照
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

    // 从相册选择图片
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
            if (requestCode == CAPTURE_IMAGE && data != null) {

                // System.out.println(photo_uri);
                scanFile(file.getAbsolutePath());
                imageUris.add(photo_uri);  // 添加到图片列表

                imageAdapter.notifyDataSetChanged();  // 更新 RecyclerView
            } else if (requestCode == PICK_IMAGES && data != null) {
                // 从相册返回的结果
                if (data.getClipData() != null) {
                    // 用户选择了多张图片
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);  // 添加到图片列表
                    }
                } else if (data.getData() != null) {
                    // 用户只选择了一张图片
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);  // 添加到图片列表
                }
                imageAdapter.notifyDataSetChanged();  // 更新 RecyclerView
            }
            System.out.println(imageUris);
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

        MediaScannerConnection.scanFile(SharepetstoryActivity.this,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }

    public boolean checkNameInput(String title) {

        if (title.isEmpty()) {
            mblogNameField.setError("This field is required."); // 设置错误消息
            mblogNameField.requestFocus(); // 聚焦到该输入框
            return false;
        }
        return true;
    }

    public boolean checkDescriptionInput(String description) {
        // 忽略大小写进行判断
        if (description.isEmpty()) {
            // 设置错误提示
            mblogDescriptionField.setError("Please enter Description for blog.");
            mblogDescriptionField.requestFocus(); // 将焦点设置到性别输入框
            return false;
        }
        return  true;
    }

    public void onPostBlogClick(View v){
        blogTitle = mblogNameField.getText().toString().trim();
        mblogDescription = mblogDescriptionField.getText().toString().trim();

        //Use selectedPetName in your blog post creation logic
        String selectedPetName = selectPetSpinner.getSelectedItem().toString();

        // 获取当前用户的 ID
        String ownerId = myUser.getFirebaseId();

        // 获取当前时间作为 postTime
        String postTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // 检查title
        if (!checkNameInput(blogTitle)){
            return;
        }

        // 检查Description 也就是 content
        if (!checkDescriptionInput(mblogDescription)){
            return;
        }

        // 从数据库中获取选中宠物的详细信息
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PetAdoptionPost")
                .whereEqualTo("ownerId", ownerId)
                .whereEqualTo("petName", selectedPetName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot petDocument = querySnapshot.getDocuments().get(0);
                        String petID = petDocument.getId();
                        String category = petDocument.getString("category");

                        // 调用 addBlog 方法
                        connectDatabase.addBlog(
                                blogTitle,
                                mblogDescription,
                                selectedPetName,
                                category,
                                postTime,
                                ownerId,
                                petID,
                                documentId -> {
                                    // 博客添加成功后，上传图片
                                    List<String> imageNames = new ArrayList<>();
                                    for (int i = 0; i < imageUris.size(); i++) {
                                        String imageName = "/image_" + i + ".jpg";
                                        imageNames.add(imageName);
                                    }

                                    for (int i = 0; i < imageUris.size(); i++) {
                                        Uri imageUri = (Uri) imageUris.get(i);
                                        String imageName = imageNames.get(i);
                                        Log.i("imageName", imageName);

                                        // 上传与博客相关的每张图片
                                        connectDatabase.uploadBlogImage(documentId, imageUri, imageName,
                                                downloadUri -> {
                                                    Log.d("FirestoreDatabase", "Image uploaded successfully: " + imageName);
                                                },
                                                e -> {
                                                    Log.e("FirestoreDatabase", "Failed to upload image: " +  e);
                                                }
                                        );
                                    }

                                    Toast.makeText(SharepetstoryActivity.this, "Upload success.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SharepetstoryActivity.this, MyBlogsActivity.class);
                                    startActivity(intent);
                                    finish();
                                },
                                e ->{
                                    Log.e("FirestoreDatabase", "Error uploading blog", e);
                                    Toast.makeText(SharepetstoryActivity.this, "Failed to upload blog.", Toast.LENGTH_SHORT).show();
                                }
                        );
                    } else {
                        // 未找到匹配的宠物
                        Toast.makeText(SharepetstoryActivity.this, "Pet not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreDatabase", "Error fetching pet details", e);
                    Toast.makeText(SharepetstoryActivity.this, "Failed to fetch pet details.", Toast.LENGTH_SHORT).show();
                });
    }
}