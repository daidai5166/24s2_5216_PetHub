package comp5216.sydney.edu.au.pethub.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import comp5216.sydney.edu.au.pethub.R;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        // Get users
        myApp = (MyApp) getApplication();
        myUser = myApp.getUser();
        // Initialize Database
        connectDatabase = new ConnectDatabase();
        // Initialize RecyclerView and Adapter
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

        //Read the user's pet data++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        selectPetSpinner = findViewById(R.id.select_pet_spinner);
        List<String> petNames = new ArrayList<>();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, petNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectPetSpinner.setAdapter(spinnerAdapter);

        // Get the Firebase ID of the current user
        String userId = myUser.getFirebaseId();

        // Retrieve the pet information of the user from Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PetAdoptionPost")
                .whereEqualTo("ownerId", userId)  // Filter pet data based on user ID
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    petNames.clear();  // Clear previous data
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String petName = document.getString("petName");  // Obtain the name of the pet
                        petNames.add(petName);  // 添加到宠物名字的列表
                    }
                    spinnerAdapter.notifyDataSetChanged();  // Update data display in Spinner
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SharepetstoryActivity.this, "Failed to load pets.", Toast.LENGTH_SHORT).show();
                });

        // Set up Spinner's listener
        selectPetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the pet name selected by the user
                String selectedPetName = parent.getItemAtPosition(position).toString();
                // Here, you can use selectedPetName or store it as a global variable
                Log.d("SelectedPet", "用户选择了宠物: " + selectedPetName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handling when no item is selected
            }
        });

        findViewById(R.id.post_blog_back_button).setOnClickListener(v -> {
            // Clear user input content
            mblogNameField.setText("");  // Clear blog title input
            mblogDescriptionField.setText("");  // Clear blog title input

            // Clear the image list and refresh RecyclerView
            imageUris.clear();
            imageAdapter.notifyDataSetChanged();

            Intent intent = new Intent(SharepetstoryActivity.this, MyBlogsActivity.class);
            startActivity(intent);

            // End the current activity to prevent data from still being present when reverting back to this interface
            finish();
        });
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

        // Handling photo button click events
        bottomSheetView.findViewById(R.id.take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
                bottomSheetDialog.dismiss();
            }
        });

        // Process upload button click event
        bottomSheetView.findViewById(R.id.upload_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImagesFromGallery();
                bottomSheetDialog.dismiss();
            }
        });

        // Process cancel button click event
        bottomSheetView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        // Set custom views and display them
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    // Open the camera and take a photo
    private void openCamera() {
        if (!marshmallowPermission.checkPermissionForCamera()) {
            marshmallowPermission.requestPermissionForCamera();}
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String photoFileName = "IMG_" + timeStamp + ".jpg";

        photo_uri = getFileUri(photoFileName); // Generate Path
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photo_uri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE);
        }
    }

    // Select pictures from the album
    private void selectImagesFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);  // Allows Multiple Selection
        startActivityForResult(intent, PICK_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE && data != null) {

                // System.out.println(photo_uri);
                scanFile(file.getAbsolutePath());
                imageUris.add(photo_uri);  // Add to image list

                imageAdapter.notifyDataSetChanged();  // Update RecyclerView
            } else if (requestCode == PICK_IMAGES && data != null) {
                // The results returned from the album
                if (data.getClipData() != null) {
                    // 用户选择了多张图片
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);  // Add to image list
                    }
                } else if (data.getData() != null) {
                    // 用户只选择了一张图片
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);  // Add to image list
                }
                imageAdapter.notifyDataSetChanged();  // Update RecyclerView
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
            mblogNameField.setError("This field is required.");
            mblogNameField.requestFocus(); // Focus on this input box
            return false;
        }
        return true;
    }

    public boolean checkDescriptionInput(String description) {
        // Ignore capitalization for judgment
        if (description.isEmpty()) {
            // Set error prompt
            mblogDescriptionField.setError("Please enter Description for blog.");
            mblogDescriptionField.requestFocus(); // Set the focus to the gender input box
            return false;
        }
        return  true;
    }

    public void onPostBlogClick(View v){
        blogTitle = mblogNameField.getText().toString().trim();
        mblogDescription = mblogDescriptionField.getText().toString().trim();

        //Use selectedPetName in your blog post creation logic
        String selectedPetName = selectPetSpinner.getSelectedItem().toString();

        // Get the current user's ID
        String ownerId = myUser.getFirebaseId();

        // Get the current time as postTime
        String postTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Check the title
        if (!checkNameInput(blogTitle)){
            return;
        }

        // Check the description, which is the content
        if (!checkDescriptionInput(mblogDescription)){
            return;
        }

        // Retrieve detailed information of the selected pet from the database
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

                        // Call the addBlog method
                        connectDatabase.addBlog(
                                blogTitle,
                                mblogDescription,
                                selectedPetName,
                                category,
                                postTime,
                                ownerId,
                                petID,
                                documentId -> {
                                    // After successfully adding the blog, upload images
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
                                        connectDatabase.uploadBlogImage(this, documentId, imageUri, imageName,
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
                        // No matching pet found
                        Toast.makeText(SharepetstoryActivity.this, "Pet not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreDatabase", "Error fetching pet details", e);
                    Toast.makeText(SharepetstoryActivity.this, "Failed to fetch pet details.", Toast.LENGTH_SHORT).show();
                });
    }
}