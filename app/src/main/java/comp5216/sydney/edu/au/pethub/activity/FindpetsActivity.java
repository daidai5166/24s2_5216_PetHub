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

        // Get Spinner
        spinnerPetType = findViewById(R.id.spinner_pet_type);
        // Get string-array resource
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pet_types, android.R.layout.simple_spinner_item);

        // Set the dropdown style
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Bind Adapter to Spinner
        spinnerPetType.setAdapter(adapter);

        spinnerLocation = findViewById(R.id.spinner_location);
        MyApp myApp = (MyApp) getApplication();
        user = myApp.getUser();

        // Search page to get GridView and set click event
        GridView gridView = findViewById(R.id.grid_pets);
        // GridView set click event
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FindpetsActivity.this, PetdetailsActivity.class);
                Pet selectedPet = pets.get(position);
                intent.putExtra("selectedPet", selectedPet);
                // Pass pets' data according to clicked items
                startActivity(intent);
            }
        });

        // Initialize the navigation bar
        NavigationBarActivity navigationBarActivity = new NavigationBarActivity(this);
        navigationBarActivity.setupNavigationBar();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        searchButton.setOnClickListener(v -> {
            pets = new ArrayList<>();
            // Retrieve data again
            fetchPetAdoptionPostsWithFilter();
            petAdapter = new PetAdapter(this, pets);
            gridView.setAdapter(petAdapter);
        });
    }

    /**
     * Encapsulate the gender selection logic, including background switching and setting the gender value
     * @param selectedView current clicked ImageView (gender icon)
     * @param otherView Another ImageView for the opposite gender (background needs to be reset)
     * @param genderValue value for gender,true represents male and false represents female.
     * @param genderType Gender type, used as an identifier for background switching
     */
    private void setGenderSelection(ImageView selectedView, ImageView otherView, Boolean genderValue, String genderType) {
        // Set the click event for the current clicked ImageView
        selectedView.setOnClickListener(v -> {
            selectedGender = genderValue; // set current gender

            // Change the background of the currently clicked ImageView to the selected state.
            selectedView.setBackgroundColor(Color.parseColor("#9FE716"));

            // Reset another ImageView background to the default state
            otherView.setBackgroundColor(Color.TRANSPARENT);
            // System.out.println(selectedGender);
        });
    }

    // Encapsulate the function to retrieve pet adoption posts
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
                            // manually get the field and debug data
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

                            // construct Pet object
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

                            if((user == null || !Objects.equals(user.getFirebaseId(), ownerId)) && Objects.equals(adopterId, "")) {
                                // add into the list and fresh
                                pets.add(pet);
                            }

                            Log.d("PetAdoptionPost", pet.getPetName());
                        } catch (Exception e) {
                            Log.e("PetAdoptionPost", "Error parsing document", e);
                        }
                    }
                    // notify adapter to fresh after data updates
                    petAdapter.notifyDataSetChanged();
                }, e -> {

                });
    }
}