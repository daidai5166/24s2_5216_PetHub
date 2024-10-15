package comp5216.sydney.edu.au.pethub.singleton;

import android.app.Application;

import com.google.android.libraries.places.api.Places;

import comp5216.sydney.edu.au.pethub.model.User;

// 一个使用了单例模式的全局类，存储了需要全局使用的信息比如用户信息。
public class MyApp extends Application {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Application 初始化时可以进行一些全局配置
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAeTkzTFkUB7D0evgPCAzSxFfdx_2ksA6I");
        }
    }
}
