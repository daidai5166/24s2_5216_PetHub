package comp5216.sydney.edu.au.pethub.activity;

import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.noCacheLoadImageFromFirebaseStorageToImageView;
import static comp5216.sydney.edu.au.pethub.util.Utility.mergeLocation;
import static comp5216.sydney.edu.au.pethub.util.Utility.splitLocation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.model.User;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;

public class EditProfileActivity extends AppCompatActivity {
    User user;
    ImageView backButton;
    ImageView avatar;

    EditText username;
    Button changeAvatarButton;
//    EditText email;
    EditText phone;
    EditText locationRow1;
    EditText locationRow2;
    EditText locationRow3;
    ConnectDatabase connectDatabase = new ConnectDatabase();
    private ImageView genderMale, genderFemale;

    private String selectedGender = "" ; // 用来存储选择的性别
    private static final int MY_PERMISSIONS_REQUEST_READ_PHOTOS = 102;

    Uri avatarUri;
    Bitmap scaledAvatar;

    Boolean avatarChanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editprofile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //没登录的话跳转到登录功能
        MyApp myApp = (MyApp) getApplication();
        user = myApp.getUser();
        if (user == null)  {
            // No user is signed in
            Intent intent = new Intent(EditProfileActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

        username = findViewById(R.id.edit_name);
//        email = findViewById(R.id.edit_email);
        phone = findViewById(R.id.edit_phone);
        locationRow1 = findViewById(R.id.location_first_row);
        locationRow2 = findViewById(R.id.location_second_row);
        locationRow3 = findViewById(R.id.location_third_row);
        avatar = findViewById(R.id.user_avatar);
        genderMale = findViewById(R.id.gender_male);
        genderFemale = findViewById(R.id.gender_female);
        changeAvatarButton = findViewById(R.id.btn_change_picture);

        // 获取初始的 ImageView 的背景 Drawable
        Drawable backgroundDrawable = avatar.getBackground();

        // 检查背景是否为 BitmapDrawable
        if (backgroundDrawable instanceof BitmapDrawable) {
            // 如果是 BitmapDrawable，直接获取 Bitmap
            scaledAvatar = ((BitmapDrawable) backgroundDrawable).getBitmap();
        } else {
            // 如果是其他类型的 Drawable，手动将 Drawable 转换为 Bitmap
            scaledAvatar = Bitmap.createBitmap(backgroundDrawable.getIntrinsicWidth(), backgroundDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(scaledAvatar);
            backgroundDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            backgroundDrawable.draw(canvas);
        }
        scaledAvatar = avatar.getDrawingCache();

        //设置现有的用户信息
        username.setText(user.getUsername());
//        email.setText(user.getEmail());
        if(user.getPhone() != 0) {
            phone.setText(String.valueOf(user.getPhone()));
        }
        if(user.getAddress() != null || !user.getAddress().equals("")) {
            String[] locationList = splitLocation(user.getAddress(), "%");
            locationRow1.setText(locationList[0]);
            locationRow2.setText(locationList[1]);
            locationRow3.setText(locationList[2]);
        }

        noCacheLoadImageFromFirebaseStorageToImageView(this, avatar, "Users/"+user.getFirebaseId()+"/avatar.jpg");

        genderMale = findViewById(R.id.gender_male);
        genderFemale = findViewById(R.id.gender_female);

        //初始化性别
        if(user.getGender().equals("M")) {
            selectedGender = "M";
            genderMale.setBackgroundColor(Color.parseColor("#9FE716"));
        } else if(user.getGender().equals("F")) {
            selectedGender = "F";
            genderFemale.setBackgroundColor(Color.parseColor("#9FE716"));
        }

        // gender click
        setGenderSelection(genderMale, genderFemale, "M", "male");
        setGenderSelection(genderFemale, genderMale, "F", "female");
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            finish();
        });

        changeAvatarButton.setOnClickListener(v -> {
            avatarChanged = true;
            selectAvatarImage();
        });

        findViewById(R.id.btn_save_changes).setOnClickListener(v -> {
            if(!checkNameFormat(username.getText().toString())) {
                return;
            }
            if(!checkPhoneFormat(phone.getText().toString())) {
                return;
            }
//            String newEmail = email.getText().toString();
            int newPhone = Integer.parseInt(phone.getText().toString());
            String newLocation = mergeLocation(new String[]{
                    locationRow1.getText().toString(),
                    locationRow2.getText().toString(),
                    locationRow3.getText().toString()
            }, "%");
            String newUsername = username.getText().toString();

            Map<String, Object> updates = new HashMap<>();
            updates.put("userName", newUsername);
//            updates.put("email", newEmail);
            updates.put("phone", newPhone);
            updates.put("gender", selectedGender);
            updates.put("address", newLocation);

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            // 修改用户的邮箱地址
//            if (firebaseUser != null) {
//                firebaseUser.updateEmail(newEmail)
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
                                // 邮箱更新成功
//                                Log.d("FirebaseAuth", "User email address updated.");
                                connectDatabase.updateUser(
                                        user.getFirebaseId(),
                                        updates,
                                        (result) -> {
                                            //保存用户信息
                                            user.setUsername(newUsername);
//                                            user.setEmail(newEmail);
                                            user.setAddress(newLocation);
                                            user.setPhone(newPhone);
                                            user.setGender(selectedGender);
                                            myApp.setUser(user);
                                        });

        // TODO: 需要刷新页面才能够替换头像
        if (avatarChanged) {
            connectDatabase.uploadUserAvatar(user.getFirebaseId(), scaledAvatar, uri -> {
                Log.d("Firebase", "Avatar uploaded");
            }, e -> {
                Log.e("Error", "Error updating avatar");
            });
        }
//                            } else {
//                                // 邮箱更新失败，处理错误
//                                Log.e("FirebaseAuth", "Error updating email: ", task.getException());
//                                Toast.makeText(EditProfileActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
            Toast.makeText(EditProfileActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditProfileActivity.this, AccountActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public boolean checkNameFormat(String userName) {
        if (userName.isEmpty()) {
            username.setError("First name is required.");
            username.requestFocus();
            return false;
        }
        return true;
    }

    public boolean checkPhoneFormat(String phone) {
        if (phone.isEmpty()) {
            this.phone.setError("Phone number is required.");
            this.phone.requestFocus();
            return false;
        }
        // 检查是否都是数字
        if (!phone.matches("[0-9]+")) {
            this.phone.setError("Phone number must be numeric.");
            this.phone.requestFocus();
            return false;
        }
        return true;
    }

    public void selectAvatarImage() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Bring up gallery to select a photo
        startActivityForResult(intent, MY_PERMISSIONS_REQUEST_READ_PHOTOS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHOTOS) {
            // Load the selected image into a preview
            if (resultCode == RESULT_OK) {
                avatarUri = data.getData();
                Bitmap selectedImage;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), avatarUri);

                    // Get the width and height of ivPreview
                    int targetWidth = avatar.getWidth();
                    int targetHeight = avatar.getHeight();

                    // Scale the bitmap to the size of ivPreview
                    scaledAvatar = Bitmap.createScaledBitmap(
                            selectedImage, targetWidth, targetHeight, true);

                    // Set the scaled image as background of ivPreview
                    avatar.setImageBitmap(scaledAvatar);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
}