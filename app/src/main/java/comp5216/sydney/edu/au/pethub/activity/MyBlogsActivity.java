package comp5216.sydney.edu.au.pethub.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.adapters.myBlogAdapter;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.model.Blog;
import comp5216.sydney.edu.au.pethub.model.User;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;

public class MyBlogsActivity extends AppCompatActivity {

    private MyApp myApp;
    private User myUser;
    private RecyclerView recyclerView;
    private myBlogAdapter myBlogAdapter;
    private List<Blog> myBlogList = new ArrayList<>();
    private Button btn_Post_New_Blog_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_blogs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 绑定Post Blog按钮点击事件
        btn_Post_New_Blog_Button = findViewById(R.id.Post_New_Blog_Button);

        // 获取用户
        myApp = (MyApp) getApplication();
        myUser = myApp.getUser();

        // 初始化RecyclerView
        recyclerView = findViewById(R.id.recyclerViewBlogs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化适配器并绑定RecyclerView
        myBlogAdapter = new myBlogAdapter(this, myBlogList);
        recyclerView.setAdapter(myBlogAdapter);

        btn_Post_New_Blog_Button.setOnClickListener(v -> {
            Intent intent = new Intent(MyBlogsActivity.this, SharepetstoryActivity.class);
            startActivity(intent);
        });

        fetchBlogPosts();
    }

    // 封装获取博客的信息
    @SuppressLint("NotifyDataSetChanged")
    private void fetchBlogPosts() {
        ConnectDatabase connectDatabase = new ConnectDatabase();
        connectDatabase.getBlogsByFilter(
                myUser.getFirebaseId(),
                blogs -> {
                    // 清空之前的列表
                    myBlogList.clear();

                    // 添加获取的博客
                    myBlogList.addAll(blogs);

                    // 更新适配器
                    myBlogAdapter.notifyDataSetChanged();
                },
                e -> {
                    // 处理查询失败
                    Log.e("Blog Fetch Error", "Failed to fetch blogs", e);
                }
        );
    }

    public void myBlogOnBackClick(View view) {
        finish();
    }
}