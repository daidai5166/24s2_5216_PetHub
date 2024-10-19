package comp5216.sydney.edu.au.pethub.activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.adapters.BlogAdapter;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.model.Blog;

public class PetblogActivity extends AppCompatActivity {

    private ConnectDatabase databaseConnection = new ConnectDatabase();  // ConnectDatabase 类实例
    private List<Blog> blogList = new ArrayList<>();  // 用于存储博客的列表

    private BlogAdapter blogAdapter;  // 用于绑定 GridView 的自定义适配器

    private EditText editTextSearchBar;  // 搜索栏
    private Button buttonSearch;  // 搜索按钮
    private TextView textViewLatestTab, textViewDogsTab, textViewCatsTab, textViewBirdsTab, textViewOthersTab;  // 标签栏
    private ImageView imageViewShareButton;  // 分享按钮

    private Map<String, List<Blog>> categorizedBlogs = new HashMap<>();

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

        // 初始化 UI 组件
        editTextSearchBar = findViewById(R.id.search_bar);
        buttonSearch = findViewById(R.id.search_button);
        textViewLatestTab = findViewById(R.id.blog_latest_tab);
        textViewDogsTab = findViewById(R.id.blog_dogs_tab);
        textViewCatsTab = findViewById(R.id.blog_cats_tab);
        textViewBirdsTab = findViewById(R.id.blog_birds_tab);
        textViewOthersTab = findViewById(R.id.blog_others_tab);
        imageViewShareButton = findViewById(R.id.iv_share_blog);

        // 初始化 GridView 并设置适配器
        GridView gridViewBlogs = findViewById(R.id.blog_grid_view);
        blogAdapter = new BlogAdapter(this, blogList);
        gridViewBlogs.setAdapter(blogAdapter);

        // 为标签设置点击事件
        setupTabClickListeners();

        // GridView 项目点击事件
        gridViewBlogs.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(PetblogActivity.this, BlogdetailsActivity.class);
            Blog selectedBlog = blogList.get(position);
            intent.putExtra("selectedBlog", selectedBlog);
            startActivity(intent);
        });

        // 初始化导航栏
        NavigationBarActivity navigationBar = new NavigationBarActivity(this);
        navigationBar.setupNavigationBar();

        // 分享按钮点击事件
        imageViewShareButton.setOnClickListener(v -> {
            Intent intent = new Intent(PetblogActivity.this, SharepetstoryActivity.class);
            startActivity(intent);
        });

        // 拉取博客
        fetchBlogPosts();
    }

    private void setupTabClickListeners() {
        // Latest tab
        textViewLatestTab.setOnClickListener(v -> {
            resetButtonBackgrounds();
            textViewLatestTab.setBackgroundColor(Color.parseColor("#9dbf85"));  // 选中背景色
            filterAndSortBlogsByCategory("Latest");  // 按最新排序或筛选
        });

        // Dogs tab
        textViewDogsTab.setOnClickListener(v -> {
            resetButtonBackgrounds();
            textViewDogsTab.setBackgroundColor(Color.parseColor("#9dbf85"));
            filterAndSortBlogsByCategory("Dogs");  // 筛选狗相关博客
        });

        // Cats tab
        textViewCatsTab.setOnClickListener(v -> {
            resetButtonBackgrounds();
            textViewCatsTab.setBackgroundColor(Color.parseColor("#9dbf85"));
            filterAndSortBlogsByCategory("Cats");  // 筛选猫相关博客
        });

        // Birds tab
        textViewBirdsTab.setOnClickListener(v -> {
            resetButtonBackgrounds();
            textViewBirdsTab.setBackgroundColor(Color.parseColor("#9dbf85"));
            filterAndSortBlogsByCategory("Birds");  // 筛选鸟相关博客
        });

        // Others tab
        textViewOthersTab.setOnClickListener(v -> {
            resetButtonBackgrounds();
            textViewOthersTab.setBackgroundColor(Color.parseColor("#9dbf85"));
            filterAndSortBlogsByCategory("Other");  // 筛选其他相关博客
        });
    }

    private void resetButtonBackgrounds() {
        TextView textViewLatest = findViewById(R.id.blog_latest_tab);
        TextView textViewDogs = findViewById(R.id.blog_dogs_tab);
        TextView textViewCats = findViewById(R.id.blog_cats_tab);
        TextView textViewBirds = findViewById(R.id.blog_birds_tab);
        TextView textViewOthers = findViewById(R.id.blog_others_tab);

        // Reset background to transparent for all TextViews
        textViewLatest.setBackgroundColor(Color.TRANSPARENT);
        textViewDogs.setBackgroundColor(Color.TRANSPARENT);
        textViewCats.setBackgroundColor(Color.TRANSPARENT);
        textViewBirds.setBackgroundColor(Color.TRANSPARENT);
        textViewOthers.setBackgroundColor(Color.TRANSPARENT);
    }

    private void filterAndSortBlogsByCategory(String category) {
        List<Blog> filteredBlogs = categorizedBlogs.get(category.toLowerCase());
        if (filteredBlogs == null) {
            filteredBlogs = new ArrayList<>();
        }

        // 排序逻辑保持不变
        sortBlogsByTime(filteredBlogs);
        blogAdapter.updateBlogs(filteredBlogs);
    }

    // 封装获取博客帖子的函数
    private void fetchBlogPosts() {
        ConnectDatabase connectDatabase = new ConnectDatabase();
        connectDatabase.getBlogs(
                queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            // 手动获取字段并调试数据
                            String blogID = document.getId();
                            String blogTitle = document.getString("blogTitle");
                            String content = document.getString("content");
                            String petName = document.getString("petName");
                            String category = document.getString("category");
                            String postTime = document.getString("postTime");
                            String ownerId = document.getString("ownerId");
                            String petID = document.getString("petID");
                            List<String> likedUsers = (List<String>) document.get("likedUsers");

                            // 构造 Blog 对象
                            Blog blog = new Blog(
                                    blogID,
                                    blogTitle,
                                    content,
                                    petName,
                                    category,
                                    postTime,
                                    ownerId,
                                    petID,
                                    likedUsers != null ? likedUsers : new ArrayList<>()
                            );

                            // 添加到博客列表
                            blogList.add(blog);

                            Log.i("BlogPostID", blogID);
                            Log.d("BlogPost", blog.getBlogTitle());
                        } catch (Exception e) {
                            Log.e("BlogPost", "Error parsing document", e);
                        }
                    }

                    // 对博客进行分类或其他处理
                    categorizeBlogs();

                    // 数据更新后通知适配器刷新
                    blogAdapter.notifyDataSetChanged();
                },
                e -> {
                    Log.e("BlogPost", "Error fetching blog posts", e);
                }
        );
    }

    private void categorizeBlogs() {
        for (Blog blog : blogList) {
            String category = blog.getCategory().toLowerCase();
            categorizedBlogs.putIfAbsent(category, new ArrayList<>());
            categorizedBlogs.get(category).add(blog);
        }
    }

    // 对blogs列表按照距离排序
    public void sortBlogsByTime(List<Blog> sortBlogs) {
        sortBlogs.sort((blog1, blog2) -> {
            // 按时间降序排列
            return blog2.getPostTime().compareTo(blog1.getPostTime());
        });
    }
}
