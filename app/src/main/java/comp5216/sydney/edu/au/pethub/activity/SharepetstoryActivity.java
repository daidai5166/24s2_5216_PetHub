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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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

    //private String pet_category="";
    private String blogTitle="";
    private String blogAddress="";
    private String mblogDescription="";
    private Double petLng=0.0;
    private Double petLat=0.0;

    private EditText mblogNameField;
    private EditText mblogAddressField;
    private EditText mblogDescriptionField;
    private Button mpostPetButton;

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Uri> imageUris = new ArrayList<>();
    MarshmallowPermission marshmallowPermission = new MarshmallowPermission(this);
    ConnectDatabase connectDatabase;

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
        mblogAddressField = findViewById(R.id.share_blog_address);

        // 初始化 地址补全
        mblogAddressField.setOnClickListener(v -> {
            // 创建一个 Autocomplete Intent，指定返回的字段
            List<Place.Field> fields = Arrays.asList(
                    Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        FrameLayout blogImageUpload = findViewById(R.id.blog_image_upload);

        uploadPetImageClickListener(blogImageUpload);
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
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // 获取用户选择的地点
                Place place = Autocomplete.getPlaceFromIntent(data);
                // 将完整地址显示在 EditText 中
                mblogAddressField.setText(place.getAddress());
                blogAddress = place.getAddress();
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
            return false; // 输入不是 Male 或 Female 时返回 false
        }
        return  true;
    }

    public void onPostPetClick(View v){
        blogTitle = mblogNameField.getText().toString().trim();
        mblogDescription = mblogDescriptionField.getText().toString().trim();
        blogAddress = mblogAddressField.getText().toString().trim();
        List<String> uriStringList = new ArrayList<>();
        // 创建一个用来保存图片名字的列表
        List<String> imageNames = new ArrayList<>();

        // 遍历 imageUris，根据每个 Uri 的索引生成文件名
        for (int i = 0; i < imageUris.size(); i++) {
            // 可以根据索引生成文件名，例如 "image_0.jpg", "image_1.jpg" 等
            String imageName = "/image_" + i + ".jpg";
            imageNames.add(imageName);
        }

        // 检查title
        if (!checkNameInput(blogTitle)){
            return;
        }

        // 检查Description
        if (mblogDescription.isEmpty()){
            mblogDescription = getString(R.string.petEmptyDescription);
        }

        if (imageUris != null){
            // 将每个 Uri 转换为字符串
            for (Uri uri : imageUris) {
                uriStringList.add(uri.toString());
            }
        }

        // TODO 等待navigation bar校验写完
        String ownerId="1";//myUser.getFirebaseId();//用户firebase的ID
        String adopterId="";//新宠物暂无领养人
        List<String> interestedUserIds=new ArrayList<>();//新宠物暂无兴趣人
        List<String> blogTitles=new ArrayList<>();
        connectDatabase.addBlog(
                blogTitle,
                mblogDescription,
                blogAddress,
                petLng,
                petLat,
                ownerId,
                adopterId,
                interestedUserIds,
                uriStringList,
                blogTitles,documentId  ->{
                    for (int i = 0; i < imageUris.size(); i++) {
                        Uri imageUri = imageUris.get(i);        // 获取当前图片的 Uri
                        String imageName = imageNames.get(i);   // 获取当前图片的名称

                        // 调用上传函数，传入图片名称和 Uri
                        connectDatabase.uploadPetImage(documentId, imageUri, imageName,
                                // 成功回调
                                downloadUri -> {
                                    Log.d("FirestoreDatabase", "Image uploaded successfully: " + imageName);
                                    // 您可以在这里处理每次图片上传成功后的逻辑，例如保存下载 URL
                                },
                                // 失败回调
                                e -> {
                                    Log.e("FirestoreDatabase", "Failed to upload image: " + imageName, e);
                                }
                        );
                    }
                    Toast.makeText(SharepetstoryActivity.this, "Upload success.",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SharepetstoryActivity.this, MypetsActivity.class);
                    startActivity(intent);
                    finish();
                },
                e ->{
                    Log.e("FirestoreDatabase", "Error uploading pet");
                });
    }
}