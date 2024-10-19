package comp5216.sydney.edu.au.pethub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
import java.util.Collections;
import java.util.Comparator;
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
        //读取用户的宠物数据，++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        selectPetSpinner = findViewById(R.id.select_pet_spinner);
        List<String> petNames = new ArrayList<>();
        petIDs = new ArrayList<>();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, petNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectPetSpinner.setAdapter(spinnerAdapter);
        connectDatabase = new ConnectDatabase();
        // 获取当前用户的 Firebase ID
        String userId = myUser.getFirebaseId();

        // 从 Firebase Firestore 中获取该用户的宠物信息
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PetAdoptionPost")
                .whereEqualTo("ownerId", userId)  // 根据用户 ID 过滤宠物数据
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    petNames.clear();  // 清空之前的数据
                    petIDs.clear();  // 清空之前的数据
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String petName = document.getString("petName");  // 获取宠物的名字
                        petNames.add(petName);  // 添加到宠物名字的列表
                        petIDs.add(document.getId());  // 添加到宠物 ID 的列表
                    }
                    spinnerAdapter.notifyDataSetChanged();  // 更新 Spinner 中的数据显示
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MyMessageActivity.this, "Failed to load pets.", Toast.LENGTH_SHORT).show();
                });
        requests = new ArrayList<>();
        selectPetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                       @Override
                                                       public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                           // 获取选中项的内容
                                                            selectedPetID = petIDs.get(position);
                                                           // 从 Firebase Firestore 中获取该用户的请求信息
                                                            connectDatabase.getRequestsByPetId(selectedPetID,
                                                                    querySnapshot -> {
                                                                    requests.clear();  // 清空之前的数据
                                                                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                                                        String requestId = document.getId();  // 获取请求的 ID
                                                                        String requestMessage = document.getString("message");  // 获取请求的消息
                                                                        String requestDate = document.getString("date");  // 获取请求的日期
                                                                        String email = document.getString("email");  // 获取请求的邮箱
                                                                        int phone = document.getLong("phone").intValue();  // 获取请求的电话
                                                                        String petID = document.getString("petID");  // 获取请求的宠物 ID
                                                                        String userId = document.getString("userId");  // 获取请求的用户 ID
                                                                        String userName = document.getString("userName");  // 获取请求的用户名
                                                                        String address = document.getString("address");  // 获取请求的地址
                                                                        Request request = new Request(petID, userId, requestId, userName, email, address, requestMessage, phone, requestDate);  // 创建请求对象
                                                                        requests.add(request);  // 添加到请求列表
                                                                    }
                                                                    // 定义日期格式
                                                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                                                    // 使用Comparator对requestDate进行排序
                                                                    requests.sort((r1, r2) -> {
                                                                        try {
                                                                            // 将String类型的日期转换为Date类型
                                                                            Date date1 = sdf.parse(r1.getDate());
                                                                            Date date2 = sdf.parse(r2.getDate());
                                                                            // 按日期降序排序（时间靠后的排在前面）
                                                                            return date2.compareTo(date1);
                                                                        } catch (
                                                                                ParseException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                        return 0;  // 如果解析失败，视为相等
                                                                    });
                                                                    messageAdapter.notifyDataSetChanged();  // 更新 ListView 中的数据显示
                                                                },
                                                                e -> {
                                                                    Toast.makeText(MyMessageActivity.this, "Failed to load requests.", Toast.LENGTH_SHORT).show();
                                                                });

                                                       }

                                                       @Override
                                                       public void onNothingSelected(AdapterView<?> parent) {
                                                           // 当没有选中任何选项时的逻辑
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