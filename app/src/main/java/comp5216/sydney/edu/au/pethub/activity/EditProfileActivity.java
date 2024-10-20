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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    TextView locationRow1;
    EditText locationRow2;
    EditText locationRow3;
    ConnectDatabase connectDatabase = new ConnectDatabase();
    private ImageView genderMale, genderFemale;

    private String selectedGender = "" ; // 用来存储选择的性别
    private static final int MY_PERMISSIONS_REQUEST_READ_PHOTOS = 102;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 103;

    Uri avatarUri;
    Bitmap scaledAvatar;

    Boolean avatarChanged = false;
    String newAddress;
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
        // User need to login if they aren't
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
//        locationRow2 = findViewById(R.id.location_second_row);
//        locationRow3 = findViewById(R.id.location_third_row);
        avatar = findViewById(R.id.user_avatar);
        genderMale = findViewById(R.id.gender_male);
        genderFemale = findViewById(R.id.gender_female);
        changeAvatarButton = findViewById(R.id.btn_change_picture);

        // Obtain original background ImageView Drawable
        Drawable backgroundDrawable = avatar.getBackground();

        // Check whether background is the BitmapDrawable
        if (backgroundDrawable instanceof BitmapDrawable) {
            // If it is BitmapDrawable，get Bitmap directly
            scaledAvatar = ((BitmapDrawable) backgroundDrawable).getBitmap();
        } else {
            // If it is other type of Drawable，manually transfer the Drawable into Bitmap
            scaledAvatar = Bitmap.createBitmap(backgroundDrawable.getIntrinsicWidth(), backgroundDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(scaledAvatar);
            backgroundDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            backgroundDrawable.draw(canvas);
        }
        scaledAvatar = avatar.getDrawingCache();

        // Set up current user's information
        username.setText(user.getUsername());
//        email.setText(user.getEmail());
        if(user.getPhone() != 0) {
            phone.setText(String.valueOf(user.getPhone()));
        }
        if(user.getAddress() != null || !user.getAddress().equals("")) {
//            String[] locationList = splitLocation(user.getAddress(), "%");
            locationRow1.setText(user.getAddress());
//            locationRow2.setText(locationList[1]);
//            locationRow3.setText(locationList[2]);
        }

        locationRow1.setOnClickListener(v -> {
            // Create a Autocomplete Intent，specify the returned fields
            List<Place.Field> fields = Arrays.asList(
                    Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,Place.Field.LAT_LNG);
            Intent intent1 = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(this);
            startActivityForResult(intent1, AUTOCOMPLETE_REQUEST_CODE);
        });

        noCacheLoadImageFromFirebaseStorageToImageView(this, avatar, "Users/"+user.getFirebaseId()+"/avatar.jpg");

        genderMale = findViewById(R.id.gender_male);
        genderFemale = findViewById(R.id.gender_female);

        // Initialize the gender
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
//            String newLocation = mergeLocation(new String[]{
//                    locationRow1.getText().toString(),
//                    locationRow2.getText().toString(),
//                    locationRow3.getText().toString()
//            }, "%");
            String newLocation = locationRow1.getText().toString();
            String newUsername = username.getText().toString();

            Map<String, Object> updates = new HashMap<>();
            updates.put("userName", newUsername);
//            updates.put("email", newEmail);
            updates.put("phone", newPhone);
            updates.put("gender", selectedGender);
            updates.put("address", newLocation);

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            user.setUsername(newUsername);
//                                            user.setEmail(newEmail);
            user.setAddress(newLocation);
            user.setPhone(newPhone);
            user.setGender(selectedGender);
            myApp.setUser(user);
            // Change user's email
//            if (firebaseUser != null) {
//                firebaseUser.updateEmail(newEmail)
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
                                // Gmail update successfully
//                                Log.d("FirebaseAuth", "User email address updated.");
                                connectDatabase.updateUser(
                                        user.getFirebaseId(),
                                        updates,
                                        result -> {
                                            // save the user information

                                        });

        // TODO: need to fresh the page to change the user photo
        if (avatarChanged) {
            connectDatabase.uploadUserAvatar(user.getFirebaseId(), scaledAvatar, uri -> {
                Log.d("Firebase", "Avatar uploaded");
            }, e -> {
                Log.e("Error", "Error updating avatar");
            });
        }
//                            } else {
//                                // Email update failure, deal with errors
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
        // Check whether they are the number
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
        else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get user's chosen location
                Place place = Autocomplete.getPlaceFromIntent(data);
                // Set all location information shown on the EditText
                locationRow1.setText(place.getAddress());
                newAddress = place.getAddress();
                Log.i("Places", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
//                LatLng latLng = place.getLatLng();
//                if (latLng != null){
//                    petLatitude = latLng.latitude;
//                    userLongitude = latLng.longitude;
//                    Log.i("location", petLatitude +" "+ userLongitude);
//                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // deal with errors
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("Places", "Error: " + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // user cancel the operation
                Log.i("Places", "Autocomplete canceled");
            }
        }
    }
    /**
     * Encapsulate gender selection logic, including background switching and setting the gender value
     * @param selectedView current clicked Imageview(gender icon)
     * @param otherView another gender's ImageView（need reload the background）
     * @param genderValue value of the gender, "F" as male, "M" as female
     * @param genderType gender type, use to change the background
     */
    private void setGenderSelection(ImageView selectedView, ImageView otherView, String genderValue, String genderType) {
        // Set click event for current ImageView
        selectedView.setOnClickListener(v -> {
            selectedGender = genderValue; // Ser current selected gender

            // set current clicked ImageView as chosen
            selectedView.setBackgroundColor(Color.parseColor("#9FE716"));

            // Reset another ImageView background as the default state
            otherView.setBackgroundColor(Color.TRANSPARENT);
            // System.out.println(selectedGender);
        });
    }
}