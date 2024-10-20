package comp5216.sydney.edu.au.pethub.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.adapters.mypetAdapter;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.model.Pet;
import comp5216.sydney.edu.au.pethub.model.User;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;

public class MypetsActivity extends AppCompatActivity {

    private MyApp myApp;
    private User myUser;
    private RecyclerView recyclerView;
    private mypetAdapter myPetAdapter;
    private List<Pet> myPetList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mypets);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        myApp = (MyApp) getApplication();
        myUser = myApp.getUser();

        recyclerView = findViewById(R.id.recycler_mypet);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myPetAdapter = new mypetAdapter(this, myPetList);
        recyclerView.setAdapter(myPetAdapter);

        fetchPetAdoptionPosts();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchPetAdoptionPosts() {
        ConnectDatabase connectDatabase = new ConnectDatabase();
        connectDatabase.getPetAdoptionPosts(
                queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            if(Objects.equals(myUser.getFirebaseId(), document.getString("ownerId")))
                            {
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

                            Pet pet = new Pet(
                                    petID,
                                    petName,
                                    age, gender,
                                    description,
                                    category,
                                    address,
                                    longitude,
                                    latitude,
                                    ownerId,
                                    adopterId,
                                    interestedUserIds,
                                    uriStringList,
                                    uploadTime);

                            myPetList.add(pet);
                            Log.i("PetAdoptionPostID", petID);
                            Log.d("PetAdoptionPost", pet.getPetName());
                            }
                        } catch (Exception e) {
                            Log.e("PetAdoptionPost", "Error parsing document", e);
                        }
                    }

                    myPetAdapter.notifyDataSetChanged();
                },
                e -> {
                    Log.e("PetAdoptionPost", "Error fetching pet adoption posts", e);
                }
        );
    }

    public void goBack(View view){
        Intent intent = new Intent(MypetsActivity.this, AccountActivity.class);
        startActivity(intent);
        finish();
    }
}