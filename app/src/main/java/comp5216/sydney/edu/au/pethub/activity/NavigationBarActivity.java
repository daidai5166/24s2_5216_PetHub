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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import comp5216.sydney.edu.au.pethub.MainActivity;
import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;
import comp5216.sydney.edu.au.pethub.model.User;

public class NavigationBarActivity {
    private Activity activity;
    private FirebaseUser currentUser;
    private User myUser;
    private MyApp myApp;

    public NavigationBarActivity(Activity activity) {
        this.activity = activity;
        myApp = (MyApp) activity.getApplication();
        myUser = myApp.getUser();
        // 获取当前的 Firebase 用户
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void setupNavigationBar() {
        // 绑定按钮
        activity.findViewById(R.id.btn_home).setOnClickListener(v -> {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
        });

        activity.findViewById(R.id.btn_search_bottom).setOnClickListener(v -> {
            Intent intent = new Intent(activity, FindpetsActivity.class);
            activity.startActivity(intent);
        });

        if (myUser == null) {
            // 禁用其他按钮
            activity.findViewById(R.id.btn_post).setOnClickListener(v -> {
                Intent intent = new Intent(activity, SignInActivity.class);
                activity.startActivity(intent);
                Toast.makeText(activity, "Please log in to have a complete experience.", Toast.LENGTH_SHORT).show();
            });

            activity.findViewById(R.id.btn_blog).setOnClickListener(v -> {
                Intent intent = new Intent(activity, SignInActivity.class);
                activity.startActivity(intent);
                Toast.makeText(activity, "Please log in to have a complete experience.", Toast.LENGTH_SHORT).show();
            });

            activity.findViewById(R.id.btn_account).setOnClickListener(v -> {
                Intent intent = new Intent(activity, SignInActivity.class);
                activity.startActivity(intent);
                Toast.makeText(activity, "Please log in to have a complete experience.", Toast.LENGTH_SHORT).show();
            });

        } else {
            activity.findViewById(R.id.btn_post).setOnClickListener(v -> {
                Intent intent = new Intent(activity, PostpetActivity.class);
                activity.startActivity(intent);
            });

            activity.findViewById(R.id.btn_blog).setOnClickListener(v -> {
                Intent intent = new Intent(activity, PetblogActivity.class);
                activity.startActivity(intent);
            });

            activity.findViewById(R.id.btn_account).setOnClickListener(v -> {
                Intent intent = new Intent(activity, AccountActivity.class);
                activity.startActivity(intent);
            });
        }
    }
}

