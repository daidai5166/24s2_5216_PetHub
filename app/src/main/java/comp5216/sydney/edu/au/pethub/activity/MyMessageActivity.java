package comp5216.sydney.edu.au.pethub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.adapters.MyMessageAdapter;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.model.Request;
import comp5216.sydney.edu.au.pethub.model.User;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;

public class MyMessageActivity extends AppCompatActivity {
    private User myUser;
    private Spinner selectPetSpinner;
    private ArrayList<Request> requests;
    private MyMessageAdapter messageAdapter;
    private ListView listView;
    private ArrayList<String> petIDs;
    private String selectedPetID;

    ConnectDatabase connectDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_message_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        MyApp myApp = (MyApp) getApplication();
        myUser = myApp.getUser();
        //Read the user's pet data，++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        selectPetSpinner = findViewById(R.id.select_pet_spinner);
        List<String> petNames = new ArrayList<>();
        petIDs = new ArrayList<>();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, petNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectPetSpinner.setAdapter(spinnerAdapter);
        connectDatabase = new ConnectDatabase();
        // Retrieve the current user's Firebase ID
        String userId = myUser.getFirebaseId();

        // Retrieve the user's pet information from Firebase Firestore.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PetAdoptionPost")
                .whereEqualTo("ownerId", userId)  // Filter pet data based on user ID
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    petNames.clear();  // Clear the previous data
                    petIDs.clear();  // Clear the previous data
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String petName = document.getString("petName");  // Retrieve the pet's name.
                        petNames.add(petName);  // Add to the list of pet names.
                        petIDs.add(document.getId());  // Add to the list of pet IDs.
                    }
                    spinnerAdapter.notifyDataSetChanged();  // Update the displayed data in the Spinner.
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MyMessageActivity.this, "Failed to load pets.", Toast.LENGTH_SHORT).show();
                });
        requests = new ArrayList<>();
        selectPetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                       @Override
                                                       public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                           // Retrieve the content of the selected item.
                                                            selectedPetID = petIDs.get(position);
                                                           // Retrieve the user's request information from Firebase Firestore.
                                                            connectDatabase.getRequestsByPetId(selectedPetID,
                                                                    querySnapshot -> {
                                                                    requests.clear();  // Clear the previous data.
                                                                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                                                        String requestId = document.getId();  // Retrieve the request ID
                                                                        String requestMessage = document.getString("message");  // Retrieve the request message.
                                                                        String requestDate = document.getString("date");  // Retrieve the request date.
                                                                        String email = document.getString("email");  // Retrieve the request email.
                                                                        int phone = document.getLong("phone").intValue();  // Retrieve the request's phone number.
                                                                        String petID = document.getString("petID");  // Retrieve the request's pet ID.
                                                                        String userId = document.getString("userId");  // Retrieve the request's user ID.
                                                                        String userName = document.getString("userName");  // Retrieve the request's username.
                                                                        String address = document.getString("address");  // Retrieve the request's address.
                                                                        Request request = new Request(petID, userId, requestId, userName, email, address, requestMessage, phone, requestDate);  //Create a request object
                                                                        requests.add(request);  // Add to the request list.
                                                                    }
                                                                    // Define the date format
                                                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                                                    requests.sort((r1, r2) -> {
                                                                        try {
                                                                            Date date1 = sdf.parse(r1.getDate());
                                                                            Date date2 = sdf.parse(r2.getDate());
                                                                            return date2.compareTo(date1);
                                                                        } catch (
                                                                                ParseException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                        return 0;  // If failed, set o
                                                                    });
                                                                    messageAdapter.notifyDataSetChanged();  // update ListView date
                                                                },
                                                                e -> {
                                                                    Toast.makeText(MyMessageActivity.this, "Failed to load requests.", Toast.LENGTH_SHORT).show();
                                                                });

                                                       }

                                                       @Override
                                                       public void onNothingSelected(AdapterView<?> parent) {
                                                       }
                                                   });
        listView = findViewById(R.id.list_view);

        // Create an adapter for the list view using Android's built-in item layout
        messageAdapter = new MyMessageAdapter(this, requests);
        // Connect the listView and the adapter
        listView.setAdapter(messageAdapter);



        listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.i("MyMessageActivity", "Clicked item: " + position);
            Intent intent = new Intent(MyMessageActivity.this, SelectnewownerActivity.class);
            if (intent != null) {
//                // put "extras" into the bundle for access in the edit activity
//                intent.putExtra("itemID", clickedItem.getToDoItemID()+"");
//                intent.putExtra("itemName", clickedItem.getToDoItemName());
//                intent.putExtra("itemType", clickedItem.getToDOItemType());
//                intent.putExtra("itemDate", clickedItem.getToDoItemDate());
//                intent.putExtra("itemTime", clickedItem.getToDoItemTime());
//                intent.putExtra("position", position);
                Request request = requests.get(position);
                intent.putExtra("request", request);
                // brings up the second activity
                startActivity(intent);
            }
        });

    }

    public void myMessageOnBackClick(View view) {
        finish();
    }
}