package comp5216.sydney.edu.au.pethub.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;

import comp5216.sydney.edu.au.pethub.activity.NavigationBarActivity;

import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
//import comp5216.sydney.edu.au.pethub.model.PetBlog;
import comp5216.sydney.edu.au.pethub.adapters.ImageAdapterWrapper;

import comp5216.sydney.edu.au.pethub.adapters.ImageAdapter;

public class PetblogActivity extends AppCompatActivity {

    private ConnectDatabase connectDatabase = new ConnectDatabase();

    private GridView blogGridView;
    private ImageAdapterWrapper blogAdapter; // 使用 ImageAdapter 显示博客图片
    private List<Uri> blogImageUris = new ArrayList<>();
    private List<String> blogTitles = new ArrayList<>();

    private EditText searchBar;
    private Button searchButton;
    private TextView latestTab, dogsTab, catsTab, birdsTab, othersTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_petblog);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // 初始化组件
        searchBar = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);
        latestTab = findViewById(R.id.blog_latest_tab);
        dogsTab = findViewById(R.id.blog_dogs_tab);
        catsTab = findViewById(R.id.blog_cats_tab);
        birdsTab = findViewById(R.id.blog_birds_tab);
        othersTab = findViewById(R.id.blog_others_tab);

        loadBlogsFromDatabase();

        // 设置搜索功能
        searchButton.setOnClickListener(v -> {
            String query = searchBar.getText().toString().trim();
            filterBlogs(query);
        });

        // 设置分类标签点击事件
        setCategoryClickListener(latestTab, "latest");
        setCategoryClickListener(dogsTab, "Dog");
        setCategoryClickListener(catsTab, "Cat");
        setCategoryClickListener(birdsTab, "Bird");
        setCategoryClickListener(othersTab, "Other");

        // GridView项的点击事件：跳转到详情页面
        blogGridView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTitle = blogTitles.get(position);
            Intent intent = new Intent(PetblogActivity.this, BlogdetailsActivity.class);
            intent.putExtra("title", selectedTitle);
            startActivity(intent);
        });

        // 初始化导航栏
        NavigationBarActivity navigationBarActivity = new NavigationBarActivity(this);
        navigationBarActivity.setupNavigationBar();
    }
    // 从数据库加载博客数据
    private void loadBlogsFromDatabase() {
        connectDatabase.getBlogs((QuerySnapshot querySnapshot) -> {
            blogImageUris.clear();
            blogTitles.clear();

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                String title = document.getString("blogTitle");
                String photoPath = document.getString("photoPath");

                if (title != null) blogTitles.add(title);
                if (photoPath != null) blogImageUris.add(Uri.parse(photoPath));
            }

            // 初始化并设置 ImageAdapter
            blogAdapter = new ImageAdapterWrapper(this, blogImageUris);
            blogGridView.setAdapter(blogAdapter);
        });
    }

    // 根据关键词过滤博客
    private void filterBlogs(String query) {
        List<Uri> filteredUris = new ArrayList<>();
        List<String> filteredTitles = new ArrayList<>();

        for (int i = 0; i < blogTitles.size(); i++) {
            String title = blogTitles.get(i);
            if (title.toLowerCase().contains(query.toLowerCase())) {
                filteredTitles.add(title);
                filteredUris.add(blogImageUris.get(i));
            }
        }

        // 更新适配器内容
        blogAdapter = new ImageAdapterWrapper(this, blogImageUris);
        blogGridView.setAdapter(blogAdapter);
    }

    // 分类标签点击事件：按类别过滤博客
    private void setCategoryClickListener(TextView tab, String category) {
        tab.setOnClickListener(v -> {
            List<Uri> filteredUris = new ArrayList<>();

            connectDatabase.getBlogs((QuerySnapshot querySnapshot) -> {
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    if (category.equalsIgnoreCase(document.getString("category"))) {
                        String photoPath = document.getString("photoPath");
                        if (photoPath != null) filteredUris.add(Uri.parse(photoPath));
                    }
                }
                // 更新适配器
                blogAdapter = new ImageAdapterWrapper(this, blogImageUris);
                blogGridView.setAdapter(blogAdapter);
            });
        });
    }
}