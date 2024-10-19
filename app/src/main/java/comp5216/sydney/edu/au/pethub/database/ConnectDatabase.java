/**
 * import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;
 *
 * public class MainActivity extends AppCompatActivity {
 *
 *     // 创建数据库连接对象
 *     private ConnectDatabase db;
 *
 *     @Override
 *     protected void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         setContentView(R.layout.activity_main);
 *
 *         // 初始化数据库连接对象
 *         db = new ConnectDatabase();
 *
 *         // 示例 1: 添加一个宠物领养贴
 *         addPetAdoptionPostExample();
 *
 *         // 示例 2: 获取所有宠物领养贴
 *         getPetAdoptionPostsExample();
 *
 *         // 示例 3: 更新一个宠物领养贴
 *         updatePetAdoptionPostExample("your-pet-adoption-post-id");
 *
 *         // 示例 4: 删除一个宠物领养贴
 *         deletePetAdoptionPostExample("your-pet-adoption-post-id");
 *
 *         // 示例 5: 添加用户
 *         addUserExample();
 *
 *         // 示例 6: 获取所有用户
 *         getUsersExample();
 *
 *         // 示例 7: 添加博客
 *         addBlogExample();
 *
 *         // 示例 8: 获取所有博客
 *         getBlogsExample();
 *
 *         // 示例 9: 更新博客
 *         updateBlogExample("your-blog-id");
 *
 *         // 示例 10: 删除博客
 *         deleteBlogExample("your-blog-id");
 *     }
 *
 *     // 示例 1: 添加一个宠物领养贴
 *     private void addPetAdoptionPostExample() {
 *         List<String> interestedUsers = new ArrayList<>();
 *         interestedUsers.add("user1");
 *         interestedUsers.add("user2");
 *
 *         List<String> blogTitles = new ArrayList<>();
 *         blogTitles.add("My first blog");
 *
 *         db.addPetAdoptionPost(
 *             "Bobby",      // petName
 *             2,            // age
 *             true,         // gender: true for male, false for female
 *             "Friendly dog",  // description
 *             "photo_path",  // photoPath
 *             "Dog",         // category
 *             100.123,       // longitude
 *             10.123,        // latitude
 *             "owner123",    // ownerId
 *             "adopter456",  // adopterId
 *             interestedUsers,  // interestedUserIds
 *             blogTitles    // blogTitles
 *         );
 *     }
 *
 *     // 示例 2: 获取所有宠物领养贴
 *     private void getPetAdoptionPostsExample() {
 *         db.getPetAdoptionPosts(querySnapshot -> {
 *             for (DocumentSnapshot document : querySnapshot.getDocuments()) {
 *                 Log.d("PetAdoptionPost", document.getId() + " => " + document.getData());
 *             }
 *         });
 *     }
 *
 *     // 示例 3: 更新一个宠物领养贴
 *     private void updatePetAdoptionPostExample(String postId) {
 *         Map<String, Object> updates = new HashMap<>();
 *         updates.put("description", "Updated description of the pet");
 *
 *         db.updatePetAdoptionPost(postId, updates);
 *     }
 *
 *     // 示例 4: 删除一个宠物领养贴
 *     private void deletePetAdoptionPostExample(String postId) {
 *         db.deletePetAdoptionPost(postId);
 *     }
 *
 *     // 示例 5: 添加用户
 *     private void addUserExample() {
 *         db.addUser(
 *             "John Doe",    // userName
 *             "M",           // gender
 *             "john@example.com",  // email
 *             1234567890,    // phone
 *             "123 Street",  // address
 *             "avatar_path"  // avatarPath
 *         );
 *     }
 *
 *     // 示例 6: 获取所有用户
 *     private void getUsersExample() {
 *         db.getUsers(querySnapshot -> {
 *             for (DocumentSnapshot document : querySnapshot.getDocuments()) {
 *                 Log.d("User", document.getId() + " => " + document.getData());
 *             }
 *         });
 *     }
 *
 *     // 示例 7: 添加博客
 *     private void addBlogExample() {
 *         List<String> likedUsers = new ArrayList<>();
 *         likedUsers.add("user1");
 *         likedUsers.add("user2");
 *
 *         db.addBlog(
 *             "First Blog",   // blogTitle
 *             "This is the first blog content",  // content
 *             "Bobby",        // petName
 *             2,              // age
 *             true,           // gender
 *             "Dog",          // category
 *             "2024-10-05",   // postTime
 *             "john@example.com",  // userEmail
 *             likedUsers,     // likedUsers
 *             "blog_photo_path"  // photoPath
 *         );
 *     }
 *
 *     // 示例 8: 获取所有博客
 *     private void getBlogsExample() {
 *         db.getBlogs(querySnapshot -> {
 *             for (DocumentSnapshot document : querySnapshot.getDocuments()) {
 *                 Log.d("Blog", document.getId() + " => " + document.getData());
 *             }
 *         });
 *     }
 *
 *     // 示例 9: 更新博客
 *     private void updateBlogExample(String blogId) {
 *         Map<String, Object> updates = new HashMap<>();
 *         updates.put("content", "Updated blog content");
 *
 *         db.updateBlog(blogId, updates);
 *     }
 *
 *     // 示例 10: 删除博客
 *     private void deleteBlogExample(String blogId) {
 *         db.deleteBlog(blogId);
 *     }
 *
 *     // 示例 11: 上传用户头像
 *         uploadUserAvatarBtn.setOnClickListener(v -> {
 *             if (imageUri != null) {
 *                 connectDatabase.uploadUserAvatar(userName, imageUri,
 *                         uri -> Log.d("UploadImageActivity", "Avatar uploaded, URL: " + uri.toString()),
 *                         e -> Log.e("UploadImageActivity", "Failed to upload avatar", e));
 *             }
 *         });
 *
 *     // 示例 12: 上传宠物图片
 *         uploadPetImageBtn.setOnClickListener(v -> {
 *             if (imageUri != null) {
 *                 connectDatabase.uploadPetImage(petName, imageUri,
 *                         uri -> Log.d("UploadImageActivity", "Pet image uploaded, URL: " + uri.toString()),
 *                         e -> Log.e("UploadImageActivity", "Failed to upload pet image", e));
 *             }
 *         });
 *
 *     // 示例 13: 上传博客图片
 *         uploadBlogImageBtn.setOnClickListener(v -> {
 *             if (imageUri != null) {
 *                 connectDatabase.uploadBlogImage(blogTitle, imageUri,
 *                         uri -> Log.d("UploadImageActivity", "Blog image uploaded, URL: " + uri.toString()),
 *                         e -> Log.e("UploadImageActivity", "Failed to upload blog image", e));
 *             }
 *         });
 * }
 * */

package comp5216.sydney.edu.au.pethub.database;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.net.Uri;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import comp5216.sydney.edu.au.pethub.model.User;
import comp5216.sydney.edu.au.pethub.singleton.MyApp;

public class ConnectDatabase {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private static final String TAG_FIRESTORE = "FirestoreDatabase";
    private static final String TAG_STORAGE = "FirebaseStorage";

    public ConnectDatabase() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    // CRUD for Pet Adoption Post (宠物领养贴)
    public void addPetAdoptionPost(String petName,
                                   int age,
                                   boolean gender,
                                   String description,
                                   String category,
                                   String address,
                                   double longitude,
                                   double latitude,
                                   String ownerId,
                                   String adopterId,
                                   List<String> interestedUserIds,  // 多个人可能想要领养同一个宠物
                                   List<String> uriStringList,      // uri的string格式的list 照片路径
                                   String uploadTime,
                                   OnSuccessListener<String> successListener,
                                   OnFailureListener failureListener) {
        CollectionReference pets = db.collection("PetAdoptionPost");
        Map<String, Object> pet = new HashMap<>();
        pet.put("petName", petName);
        pet.put("age", age);
        pet.put("gender", gender);  // 0: 雌性, 1: 雄性
        pet.put("description", description);
        pet.put("category", category); // 狗, 猫, 鸟等
        pet.put("address", address);
        pet.put("longitude", longitude);
        pet.put("latitude", latitude);
        pet.put("ownerId", ownerId);
        pet.put("adopterId", adopterId);
        pet.put("interestedUserIds", interestedUserIds);
        pet.put("uriStringList", uriStringList);
        pet.put("uploadTime", uploadTime);

        pets.add(pet)
                .addOnSuccessListener(documentReference -> {
                            Log.d(TAG_FIRESTORE, "Pet Adoption Post added with ID: " + documentReference.getId());
                            successListener.onSuccess(documentReference.getId());
                        }
                ).addOnFailureListener(failureListener);

    }

    public void deletePetAdoptionPost(String postId, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.collection("PetAdoptionPost").document(postId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    successListener.onSuccess(aVoid);
                    Log.d(TAG_FIRESTORE, "Pet Adoption Post deleted successfully");

                })
                .addOnFailureListener(failureListener);
    }

    public void updatePetAdoptionPost(String postId, Map<String, Object> updates) {
        db.collection("PetAdoptionPost").document(postId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG_FIRESTORE, "Pet Adoption Post updated successfully"));
    }

    public void getPetAdoptionPosts(OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {
        db.collection("PetAdoptionPost")
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public void getPetAdoptionPostsByFilter(String category, boolean gender, String location, OnSuccessListener<List<DocumentSnapshot>> successListener, OnFailureListener failureListener) {
        // 构建初始查询，包含 category 和 gender 的筛选
        Query query = db.collection("PetAdoptionPost")
                .whereEqualTo("category", category)  // 完全匹配 category 字段
                .whereEqualTo("gender", gender);  // 匹配 gender 为 true 或 false

        // 执行查询
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // 如果 location 不是空字符串，进行客户端过滤
                    List<DocumentSnapshot> filteredDocs = new ArrayList<>();
                    if (location != null && !location.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            String docLocation = doc.getString("address");
                            if (docLocation != null && docLocation.contains(location)) {
                                filteredDocs.add(doc);
                            }
                        }
                    } else {
                        // 如果 location 为空，使用所有文档
                        filteredDocs.addAll(queryDocumentSnapshots.getDocuments());
                    }

                    // 返回过滤后的 DocumentSnapshot 列表
                    successListener.onSuccess(filteredDocs);
                })
                .addOnFailureListener(failureListener);
    }


    // CRUD for User (用户)
    public void addUser(String userName,
                        String gender,
                        String email,
                        int phone,
                        String address,
                        String avatarPath,
                        OnSuccessListener<String> successListener,
                        OnFailureListener failureListener) {
        String userId = "";
        CollectionReference users = db.collection("Users");
        Map<String, Object> user = new HashMap<>();
        user.put("userName", userName);
        user.put("gender", gender); // F, M, O
        user.put("email", email);
        user.put("phone", phone);
        user.put("address", address);
        user.put("avatarPath", avatarPath);

        users.add(user).addOnSuccessListener(documentReference -> {
            Log.d(TAG_FIRESTORE, "User added with ID: " + documentReference.getId());
            successListener.onSuccess(documentReference.getId());
        });
    }

    public void getUserByEmail(String email, OnSuccessListener<User> successListener, OnFailureListener failureListener) {
        db.collection("Users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                        // 按需提取字段
                        String firebaseId = documentSnapshot.getId();
                        String username = documentSnapshot.getString("userName");
                        String emailFromDb = documentSnapshot.getString("email");
                        String gender = documentSnapshot.getString("gender");
                        int phone = Math.toIntExact(documentSnapshot.getLong("phone"));
                        String address = documentSnapshot.getString("address");
                        String avatarPath = documentSnapshot.getString("avatarPath");

                        // 创建 User 对象
                        User user = new User(firebaseId, username, emailFromDb, gender, phone, address, avatarPath);

                        // 调用回调函数传递 User 对象
                        successListener.onSuccess(user);
                    } else {
                        successListener.onSuccess(null);  // 查询结果为空时
                    }
                })
                .addOnFailureListener(failureListener);
    }

    public void getUserById(String userId, OnSuccessListener<User> successListener, OnFailureListener failureListener) {
        db.collection("Users")
                .document(userId)  // 使用 documentId 查询
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 按需提取字段
                        String firebaseId = documentSnapshot.getId();
                        String username = documentSnapshot.getString("userName");
                        String emailFromDb = documentSnapshot.getString("email");
                        String gender = documentSnapshot.getString("gender");
                        int phone = Math.toIntExact(documentSnapshot.getLong("phone"));
                        String address = documentSnapshot.getString("address");
                        String avatarPath = documentSnapshot.getString("avatarPath");

                        // 创建 User 对象
                        User user = new User(firebaseId, username, emailFromDb, gender, phone, address, avatarPath);

                        // 调用回调函数传递 User 对象
                        successListener.onSuccess(user);
                    } else {
                        successListener.onSuccess(null);  // 如果没有找到该 document
                    }
                })
                .addOnFailureListener(failureListener);
    }

    public void deleteUser(String userId) {
        db.collection("Users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG_FIRESTORE, "User deleted successfully"));
    }

    public void updateUser(String userId, Map<String, Object> updates, OnSuccessListener<QuerySnapshot> successListener) {
        db.collection("Users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG_FIRESTORE, "User updated successfully"));
    }

    public void getUsers(OnSuccessListener<QuerySnapshot> successListener) {
        db.collection("Users")
                .get()
                .addOnSuccessListener(successListener);
    }

    // CRUD for Blog (博客)
    public void addBlog(String blogTitle,
                        String content,
                        String petName,
                        String category,
                        String postTime,
                        String ownerId,
                        String petID,
                        OnSuccessListener<String> successListener, //从宠物领养贴找到 blogTitle.
                        OnFailureListener failureListener) {
        CollectionReference blogs = db.collection("Blogs");
        Map<String, Object> blog = new HashMap<>();
        List<String> likedUsers = new ArrayList<>();
        blog.put("blogTitle", blogTitle);
        blog.put("content", content);
        blog.put("petName", petName);
        blog.put("category", category);
        blog.put("postTime", postTime);
        blog.put("userEmail", ownerId);
        blog.put("petID", petID);
        blogs.add(blog).addOnSuccessListener(documentReference -> Log.d(TAG_FIRESTORE, "Blog added with ID: " + documentReference.getId()));
        blog.put("likedUsers", likedUsers);

        blogs.add(blog)
                .addOnSuccessListener(documentReference -> {
                            Log.d(TAG_FIRESTORE, "Pet Blog Post added with ID: " + documentReference.getId());
                            successListener.onSuccess(documentReference.getId());
                        }
                ).addOnFailureListener(failureListener);
    }

    public void deleteBlog(String blogId) {
        db.collection("Blog").document(blogId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG_FIRESTORE, "Blog deleted successfully"));
    }

    public void updateBlog(String blogId, Map<String, Object> updates) {
        db.collection("Blog").document(blogId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG_FIRESTORE, "Blog updated successfully"));
    }

    public void getBlogs(OnSuccessListener<QuerySnapshot> successListener) {
        db.collection("Blog")
                .get()
                .addOnSuccessListener(successListener);
    }

    // 获取博客 - 通过博客标题 (blogTitle) 获取
    public void getBlogsByTitle(String blogTitle, OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {
        db.collection("Blog")
                .whereEqualTo("blogTitle", blogTitle)  // 按标题查询
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    // 获取博客 - 通过宠物名字 (petName) 获取
    public void getBlogsByPetName(String petName, OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {
        db.collection("Blog")
                .whereEqualTo("petName", petName)  // 按宠物名字查询
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    // CRUD for Request (请求)
    public void addRequest(String userId,
                           String userName,
                           String email,
                           int phone,
                           String address,
                           String message,
                           OnSuccessListener<String> successListener,
                           OnFailureListener failureListener) {
        CollectionReference requests = db.collection("Requests");
        Map<String, Object> request = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        request.put("userId", userId);
        request.put("userName", userName);
        request.put("email", email);
        request.put("phone", phone);
        request.put("address", address);
        request.put("message", message);
        request.put("date", currentDate);

        requests.add(request).addOnSuccessListener(documentReference -> {
            Log.d(TAG_FIRESTORE, "User added with ID: " + documentReference.getId());
            successListener.onSuccess(documentReference.getId());
        });
    }
    // 上传用户头像
    public void uploadUserAvatar(String userId, Bitmap avatarBitmap, OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        StorageReference storageRef = storage.getReference();
        StorageReference avatarRef = storageRef.child("Users/" + userId + "/avatar.jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = avatarRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            avatarRef.getDownloadUrl().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
            Log.d(TAG_STORAGE, "User avatar uploaded for: " + userId);
        }).addOnFailureListener(failureListener);
    }

    // 上传宠物图片
    public void uploadPetImage(String petName, Uri petImageUri, String photoName ,OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        StorageReference storageRef = storage.getReference();
        StorageReference petImageRef = storageRef.child("Pets/" + petName + photoName);

        UploadTask uploadTask = petImageRef.putFile(petImageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            petImageRef.getDownloadUrl().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
            Log.d(TAG_STORAGE, "Pet image uploaded for: " + petName);
        }).addOnFailureListener(failureListener);
    }

    // 上传博客图片
    public void uploadBlogImage(String blogID, Uri blogImageUri, String imageName, OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        StorageReference storageRef = storage.getReference();
        StorageReference blogImageRef = storageRef.child("Blogs/" + blogID + imageName);

        UploadTask uploadTask = blogImageRef.putFile(blogImageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            blogImageRef.getDownloadUrl().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
            Log.d(TAG_STORAGE, "Blog image uploaded for: " + blogID);
        }).addOnFailureListener(failureListener);
    }


    // 从Firebase Storage加载图片到ImageView 参数: this, imageView, 路径
    // 例如: loadImageFromFirebaseStorageToImageView(this, imageView, "Users/user123/avatar.jpg");
    public static void loadImageFromFirebaseStorageToImageView(
            Context context,
            ImageView imageView,
            String imagePath
    ) {
        Log.i("Load Image", "Loading image from " + imagePath + " from storage");

        if (imagePath != null && !imagePath.isEmpty()) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagePath);
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.i("Firebase Storage", "Image found in storage");
                Glide.with(context).load(uri).into(imageView);
            }).addOnFailureListener(e -> {
                Log.e("Load Image", "Failed to load image from storage");
            });
        }
    }

    // 添加了OnFailureListener的版本
    public static void loadImageFromFirebaseStorageToImageView(
            Context context,
            ImageView imageView,
            String imagePath,
            OnFailureListener failureListener
    ) {
        Log.i("Load Image", "Loading image from " + imagePath + " from storage");

        if (imagePath != null && !imagePath.isEmpty()) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagePath);
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.i("Firebase Storage", "Image found in storage");
                Glide.with(context).load(uri).into(imageView);
            }).addOnFailureListener(e -> {
                failureListener.onFailure(e);
                Log.e("Firebase Storage Load Image", "Failed to load image from storage");
            });
        }
    }

    // 不使用cache加载图片到ImageView 参数: this, imageView, 路径
    // 会清除Glide的内存缓存
    // 目前感觉没起作用
    /*public static void noCacheLoadImageFromFirebaseStorageToImageView(
            Context context,
            ImageView imageView,
            String imagePath
    ) {
        Log.i("Load Image", "Loading image from " + imagePath + " from storage");
        // 清除 Glide 的内存缓存
        Glide.get(context).clearMemory();
        if (imagePath != null && !imagePath.isEmpty()) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagePath);
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.i("Firebase Storage", "Image found in storage");
                Glide.with(context)
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imageView);
            }).addOnFailureListener(e -> {
                Log.e("Load Image", "Failed to load image from storage");
            });
        }
    }*/
    public static void noCacheLoadImageFromFirebaseStorageToImageView(
            Context context,
            ImageView imageView,
            String imagePath
    ) {
        Log.i("Load Image", "Loading image from " + imagePath + " from storage");

        if (imagePath != null && !imagePath.isEmpty()) {
            // 获取 Firebase Storage 图片的下载 URL
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagePath);
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.i("Firebase Storage", "Image found in storage");
                // 使用 HttpURLConnection 下载图片并加载到 ImageView
                new Thread(() -> {
                    try {
                        URL url = new URL(uri.toString());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(input);

                        // 更新 UI 要在主线程中执行
                        ((Activity) context).runOnUiThread(() -> {
                            imageView.setImageBitmap(bitmap);
//                            imageView.setBackground(new BitmapDrawable());
                        });

                    } catch (IOException e) {
                        Log.e("Load Image", "Failed to load image from URL", e);
                    }
                }).start();
            }).addOnFailureListener(e -> {
                Log.e("Load Image", "Failed to load image from storage", e);
            });
        }
    }
}
