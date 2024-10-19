package comp5216.sydney.edu.au.pethub.activity;

import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.loadImageFromFirebaseStorageToImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.model.Request;
import comp5216.sydney.edu.au.pethub.model.User;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;

public class SelectnewownerActivity extends AppCompatActivity {

    ConnectDatabase connectDatabase;
    ImageView userImageField;
    TextView userNameField;
    TextView userPhoneField;
    TextView userEmailField;
    TextView userMessageField;

    Button selectOwnerButton;
    private TextView userAddressField;
    User user;
    Request request;
    String petID;
    String requestUserId;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 103;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_selectnewowner);
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


        // 获取 Intent 并提取数据
        Intent intent = getIntent();
        request = intent.getParcelableExtra("request");
        petID = request.getPetID();
        userNameField.setText(request.getUserName());
        userPhoneField.setText(String.valueOf(request.getPhone()));
        userEmailField.setText(request.getEmail());
        userAddressField.setText(request.getAddress());
        userMessageField.setText(request.getMessage());
        requestUserId = request.getUserId();
        loadImageFromFirebaseStorageToImageView(this, userImageField, "Users/" + requestUserId + "/avatar.jpg");

        selectOwnerButton = findViewById(R.id.select_new_owner_button);
    }

    public void onSelectClick(View v) {
        // Update the pet owner
        connectDatabase.updatePetAdopterId(petID, requestUserId);
        // Delete the request
        connectDatabase.deleteRequest(request.getRequestId());
        Toast.makeText(this, "Owner selected successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MyMessageActivity.class);
        startActivity(intent);
        finish();
    }
}