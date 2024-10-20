package comp5216.sydney.edu.au.pethub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;
import comp5216.sydney.edu.au.pethub.util.MarshmallowPermission;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import comp5216.sydney.edu.au.pethub.R;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private android.widget.Button getStartedButton;
    private TextView titleText, descriptionText;
    private Handler handler = new Handler();
    ConnectDatabase connectDatabase;
    MarshmallowPermission marshmallowPermission = new MarshmallowPermission(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);  // 绑定布局文件
        if (!marshmallowPermission.checkPermissionForLocation()) {
            marshmallowPermission.requestPermissionForLocation();}
        if (!marshmallowPermission.checkPermissionForCamera()) {
            marshmallowPermission.requestPermissionForCamera();}
        // 绑定 ViewPager2
        viewPager = findViewById(R.id.viewPager);
        titleText = findViewById(R.id.title);
        descriptionText = findViewById(R.id.description);
        connectDatabase = new ConnectDatabase();

        // 创建并设置适配器
        ViewPagerAdapter adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);

        // 页面变化时更新文字内容
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateText(position);  // 根据页面更新文字
            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Log.i("WelcomeActivity", "User already logged in");
            MyApp myApp = (MyApp) getApplication();

            connectDatabase.getUserByEmail(firebaseUser.getEmail(), user -> {
                myApp.setUser(user);
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, e -> {

            });
        }
        // 自动滑动
        autoScrollViewPager();

        // 获取开始按钮
        getStartedButton = findViewById(R.id.get_started);
        getStartedButton.setOnClickListener(v -> {
            // 跳转到登录页面
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


    }

    // 自动滑动的函数
    private void autoScrollViewPager() {

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % 3;  // 循环到下一个页面
                viewPager.setCurrentItem(nextItem, true);  // 带有平滑动画
                handler.postDelayed(this, 3000);  // 每隔 2.5 秒自动滑动
            }
        };
        handler.postDelayed(runnable, 3000);  // 初次延迟 2.5 秒后开始
    }

    // 根据当前页面位置更新文字
    private void updateText(int position) {
        switch (position) {
            case 0:
                titleText.setText(R.string.welcome_title1);
                descriptionText.setText(R.string.description1);
                break;
            case 1:
                titleText.setText(R.string.welcome_title2);
                descriptionText.setText(R.string.description2);
                break;
            case 2:
                titleText.setText(R.string.welcome_title3);
                descriptionText.setText(R.string.description3);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);  // 清除自动滑动的任务
    }

    // ViewPagerAdapter 类实现
    public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

        // 图片资源数组
        private int[] images = {R.drawable.ic_welcome1, R.drawable.ic_welcome2, R.drawable.ic_welcome3};

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 动态创建 ImageView
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);  // 设置图片的显示模式
            return new ViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // 为每个页面设置不同的图片
            holder.imageView.setImageResource(images[position]);
        }

        @Override
        public int getItemCount() {
            return images.length;  // 返回页面数量
        }

        // ViewHolder 类
        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = (ImageView) itemView;
            }
        }
    }
}

