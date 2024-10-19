package comp5216.sydney.edu.au.pethub.activity;

import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.loadImageFromFirebaseStorageToImageView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.model.Pet;

public class PetdetailsActivity extends AppCompatActivity {
    TextView petNameField;
    TextView petTypeField;
    TextView petAgeField;
    TextView petLocationField;
    TextView petDescriptionField;
    ImageView petImageField;
    ImageView petGenderField;
    ImageView ownerAvatarField;
    ImageView arrowLeft;
    ImageView arrowRight;

    TextView ownerNameField;
    TextView postDateField;

    Button adoptButton;
    ImageView backButton;
    int currentImageIndex = 0;

    ConnectDatabase connectDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_petdetails);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        petNameField = findViewById(R.id.pet_name);
        petTypeField = findViewById(R.id.pet_type);
        petAgeField = findViewById(R.id.pet_age);
        petLocationField = findViewById(R.id.pet_location);
        petDescriptionField = findViewById(R.id.pet_description);
        petImageField = findViewById(R.id.pet_image);
        petGenderField = findViewById(R.id.gender_icon);

        ownerNameField = findViewById(R.id.owner_name);
        postDateField = findViewById(R.id.post_date);
        ownerAvatarField = findViewById(R.id.owner_image);
        adoptButton = findViewById(R.id.btn_adopt);
        backButton = findViewById(R.id.back_button);
        // 左右箭头
        arrowLeft = findViewById(R.id.arrow_left);
        arrowRight = findViewById(R.id.arrow_right);

        // 获取 Intent 并提取数据
        Intent intent = getIntent();
        Pet pet = (Pet) intent.getParcelableExtra("selectedPet");
        String petID = pet.getPetID();
        String petName = pet.getPetName();
        String petType = pet.getCategory();
        int petAge = pet.getAge();
        String petLocation = pet.getAddress();
        String petDescription = pet.getDescription();
        String petImageUrl = "Pets/" + petID + "/image_" + currentImageIndex + ".jpg";
        boolean petGender = pet.isGender();

        String ownerId = pet.getOwnerId();
        String ownerAvatarUrl = "Users/" + ownerId + "/avatar.jpg";
        String postDate = pet.getUploadTime();
        Log.i("PetdetailsActivity", "onCreate");
        Log.i("PetdetailsActivity", "petID: " + petID);
        Log.i("PetdetailsActivity", "petName: " + petName);
        Log.i("PetdetailsActivity", "petType: " + petType);
        Log.i("PetdetailsActivity", "petAge: " + petAge);
        Log.i("PetdetailsActivity", "petLocation: " + petLocation);
        Log.i("PetdetailsActivity", "petDescription: " + petDescription);

        // 设置数据
        petNameField.setText(petName);
        petTypeField.setText(petType);
        petAgeField.setText(petAge + " years");
        petLocationField.setText(petLocation);
        petDescriptionField.setText(petDescription);
        postDateField.setText(postDate);

        connectDatabase = new ConnectDatabase();
        connectDatabase.getUserById(ownerId, user -> {
            ownerNameField.setText(user.getUsername());
        }, (e) -> {

        });
        if(petGender) {
            petGenderField.setImageResource(R.drawable.ic_gender_male);
        } else {
            petGenderField.setImageResource(R.drawable.ic_gender_female);
        }
        updatePetImageUrl(petID, currentImageIndex);
        loadImageFromFirebaseStorageToImageView(PetdetailsActivity.this, ownerAvatarField, ownerAvatarUrl);

        // 左箭头点击事件
        arrowLeft.setOnClickListener(v -> {
            if (currentImageIndex > 0) {
                currentImageIndex--;
                updatePetImageUrl(petID, currentImageIndex);
            }
        });

        // 右箭头点击事件
        arrowRight.setOnClickListener(v -> {
            currentImageIndex++;
            updatePetImageUrl(petID, currentImageIndex);
        });
    }

    private void updatePetImageUrl(String petId, int index) {
        String petImageUrl = "Pets/" + petId + "/image_" + index + ".jpg";
        Log.i("PetdetailsActivity", "updatePetImageUrl: " + petImageUrl);
        loadImageFromFirebaseStorageToImageView(PetdetailsActivity.this, petImageField, petImageUrl,
                (exception) -> {
                Log.e("PetdetailsActivity", "Image load failed");
                if(index > 0) {
                    currentImageIndex = 0;
                    updatePetImageUrl(petId, currentImageIndex);
                }
                else{
                    if (petTypeField.getText().equals("Other")) {
                        petImageField.setImageResource(R.drawable.ic_others_category);
                    } else if (petTypeField.getText().equals("Cat")) {
                        petImageField.setImageResource(R.drawable.ic_cat_category);
                    } else if (petTypeField.getText().equals("Bird")) {
                        petImageField.setImageResource(R.drawable.ic_bird_category);
                    } else {
                        petImageField.setImageResource(R.drawable.ic_dog_category);
                    }
                }
            });
    }

    public void onAdoptButton(View view) {
        Intent intent = new Intent(this, SendtoownerActivity.class);
        startActivity(intent);
    }

    public void onBackClick(View view) {
        finish();
    }
}