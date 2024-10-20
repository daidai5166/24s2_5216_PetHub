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
        setContentView(R.layout.activity_welcome);  // Bind layout file
        if (!marshmallowPermission.checkPermissionForLocation()) {
            marshmallowPermission.requestPermissionForLocation();}
        if (!marshmallowPermission.checkPermissionForCamera()) {
            marshmallowPermission.requestPermissionForCamera();}
        // 绑定 ViewPager2
        viewPager = findViewById(R.id.viewPager);
        titleText = findViewById(R.id.title);
        descriptionText = findViewById(R.id.description);
        connectDatabase = new ConnectDatabase();

        // Create and set up an adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);

        // Update text content when the page changes
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateText(position);  // Update text based on the page
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
        // Automatic sliding
        autoScrollViewPager();

        // Get Start Button
        getStartedButton = findViewById(R.id.get_started);
        getStartedButton.setOnClickListener(v -> {
            // Jump to login page
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


    }

    // Automatic sliding function
    private void autoScrollViewPager() {

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % 3;  // Loop to the next page
                viewPager.setCurrentItem(nextItem, true);  // With smooth animation
                handler.postDelayed(this, 3000);  // Automatically slide every 3 seconds
            }
        };
        handler.postDelayed(runnable, 3000);  // Starting after an initial delay of 3 seconds
    }

        // Update text based on the current page position
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
            handler.removeCallbacksAndMessages(null);  // Clear the task of automatic sliding
    }

    // Implementation of ViewPagerAdapter class
    public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

        // Image resource array
        private int[] images = {R.drawable.ic_welcome1, R.drawable.ic_welcome2, R.drawable.ic_welcome3};

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Dynamically create ImageView
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);  // Set the display mode of the image
            return new ViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // Set different images for each page
            holder.imageView.setImageResource(images[position]);
        }

        @Override
        public int getItemCount() {
            return images.length;  // Return page quantity
        }

        // ViewHolder class
        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = (ImageView) itemView;
            }
        }
    }
}

