/**
 * import android.os.Bundle;
 * import androidx.appcompat.app.AppCompatActivity;
 *
 * public class MainActivity extends AppCompatActivity {
 *
 *     @Override
 *     protected void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         setContentView(R.layout.activity_main);
 *
 *         // 初始化导航栏
 *         NavigationBarActivity navigationBarActivity = new NavigationBarActivity(this);
 *         navigationBarActivity.setupNavigationBar();
 *     }
 * }
 * */

package comp5216.sydney.edu.au.pethub.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import comp5216.sydney.edu.au.pethub.MainActivity;
import comp5216.sydney.edu.au.pethub.R;

public class NavigationBarActivity {
    private Activity activity;

    public NavigationBarActivity(Activity activity) {
        this.activity = activity;
    }

    public void setupNavigationBar() {
        // Home button
        activity.findViewById(R.id.btn_home).setOnClickListener(v -> {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
        });

        // Search bottom button
        activity.findViewById(R.id.btn_search_bottom).setOnClickListener(v -> {
            Intent intent = new Intent(activity, FindpetsActivity.class);
            activity.startActivity(intent);
        });

        // Post button
        activity.findViewById(R.id.btn_post).setOnClickListener(v -> {
            Intent intent = new Intent(activity, PostpetActivity.class);
            activity.startActivity(intent);
        });

        // Blog button
        activity.findViewById(R.id.btn_blog).setOnClickListener(v -> {
            Intent intent = new Intent(activity, PetblogActivity.class);
            activity.startActivity(intent);
        });

        // Account button
        activity.findViewById(R.id.btn_account).setOnClickListener(v -> {
            Intent intent = new Intent(activity, AccountActivity.class);
            activity.startActivity(intent);
        });

        // Another top search button (if applicable)
        activity.findViewById(R.id.btn_search_bottom).setOnClickListener(v -> {
            Intent intent = new Intent(activity, FindpetsActivity.class);
            activity.startActivity(intent);
        });
    }
}
