package comp5216.sydney.edu.au.pethub.activity;

import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.loadImageFromFirebaseStorageToImageView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.model.User;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;

public class SendtoownerActivity extends AppCompatActivity {

    ConnectDatabase connectDatabase;
    ImageView userImageField;
    TextView userNameField;
    EditText userPhoneField;
    EditText userEmailField;
    EditText userMessageField;

    Button sendButton;
    private Double userLongitude = 0.0;
    private Double petLatitude =0.0;
    private EditText userAddressField;
    private String userAddress ="";
    User user;
    String petID;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 103;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sendtoowner);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        connectDatabase = new ConnectDatabase();
        userImageField = findViewById(R.id.profile_image);
        userNameField = findViewById(R.id.user_name);
        userPhoneField = findViewById(R.id.phone);
        userEmailField = findViewById(R.id.email);
        userAddressField = findViewById(R.id.address);
        userMessageField = findViewById(R.id.message);

        MyApp myApp = (MyApp) getApplication();
        user = myApp.getUser();

        userNameField.setText(user.getUsername());
        userPhoneField.setText(user.getPhone() + "");
        userEmailField.setText(user.getEmail());

        // 获取 Intent 并提取数据
        Intent intent = getIntent();
        petID = intent.getStringExtra("petID");

        loadImageFromFirebaseStorageToImageView(this, userImageField, "Users/" + user.getFirebaseId() + "/avatar.jpg");

        sendButton = findViewById(R.id.send_to_owner_button);
        // 初始化 地址补全
        userAddressField.setOnClickListener(v -> {
            // 创建一个 Autocomplete Intent，指定返回的字段
            List<Place.Field> fields = Arrays.asList(
                    Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,Place.Field.LAT_LNG);
            Intent intent1 = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(this);
            startActivityForResult(intent1, AUTOCOMPLETE_REQUEST_CODE);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // 获取用户选择的地点
                Place place = Autocomplete.getPlaceFromIntent(data);
                // 将完整地址显示在 EditText 中
                userAddressField.setText(place.getAddress());
                userAddress = place.getAddress();
                Log.i("Places", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                LatLng latLng = place.getLatLng();
                if (latLng != null){
                    petLatitude = latLng.latitude;
                    userLongitude = latLng.longitude;
                    Log.i("location", petLatitude +" "+ userLongitude);
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

    public void onSendClick(View v) {
        // Send message to owner
        String message = userMessageField.getText().toString();
        if (message.isEmpty()) {
            userMessageField.setError("Message is required");
            return;
        }
        if (userAddress.isEmpty()) {
            userAddressField.setError("Address is required");
            return;
        }
        connectDatabase.addRequest(
                petID,
                user.getFirebaseId(),
                userNameField.getText().toString(),
                userEmailField.getText().toString(),
                Integer.parseInt(userPhoneField.getText().toString()),
                userAddress,
                message,(id) -> {
                    Log.i("SendtoownerActivity", "Request sent successfully");
                    Intent intent = new Intent(this, MainActivity.class);
                    Toast.makeText(this, "Request sent successfully, thank you!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }, (e) -> {
                    Log.i("SendtoownerActivity", "Request sent failed");
                    Toast.makeText(this, "Request sent failed, please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}