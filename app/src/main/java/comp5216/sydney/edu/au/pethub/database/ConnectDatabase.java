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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.graphics.Bitmap;
import android.util.Log;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.HashMap;
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
                                   String photoPath,
                                   String category,
                                   double longitude,
                                   double latitude,
                                   String ownerId,
                                   String adopterId,
                                   List<String> interestedUserIds,  // 多个人可能想要领养同一个宠物
                                   List<String> blogTitles) {
        CollectionReference pets = db.collection("PetAdoptionPost");
        Map<String, Object> pet = new HashMap<>();
        pet.put("petName", petName);
        pet.put("age", age);
        pet.put("gender", gender);  // 0: 雌性, 1: 雄性
        pet.put("description", description);
        pet.put("photoPath", photoPath);
        pet.put("category", category); // 狗, 猫, 鸟等
        pet.put("longitude", longitude);
        pet.put("latitude", latitude);
        pet.put("ownerId", ownerId);
        pet.put("adopterId", adopterId);
        pet.put("interestedUserIds", interestedUserIds);
        pet.put("blogTitles", blogTitles);

        pets.add(pet).addOnSuccessListener(documentReference -> Log.d(TAG_FIRESTORE, "Pet Adoption Post added with ID: " + documentReference.getId()));
    }

    public void deletePetAdoptionPost(String postId) {
        db.collection("PetAdoptionPost").document(postId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG_FIRESTORE, "Pet Adoption Post deleted successfully"));
    }

    public void updatePetAdoptionPost(String postId, Map<String, Object> updates) {
        db.collection("PetAdoptionPost").document(postId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG_FIRESTORE, "Pet Adoption Post updated successfully"));
    }

    public void getPetAdoptionPosts(OnSuccessListener<QuerySnapshot> successListener) {
        db.collection("PetAdoptionPost")
                .get()
                .addOnSuccessListener(successListener);
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
                        String username = documentSnapshot.getString("userName");
                        String emailFromDb = documentSnapshot.getString("email");
                        String gender = documentSnapshot.getString("gender");
                        int phone = Math.toIntExact(documentSnapshot.getLong("phone"));
                        String address = documentSnapshot.getString("address");
                        String avatarPath = documentSnapshot.getString("avatarPath");

                        // 创建 User 对象
                        User user = new User(username, emailFromDb, gender, phone, address, avatarPath);

                        // 调用回调函数传递 User 对象
                        successListener.onSuccess(user);
                    } else {
                        successListener.onSuccess(null);  // 查询结果为空时
                    }
                })
                .addOnFailureListener(failureListener);
    }

    public void deleteUser(String userId) {
        db.collection("Users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG_FIRESTORE, "User deleted successfully"));
    }

    public void updateUser(String userId, Map<String, Object> updates) {
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
                        int age,
                        boolean gender,
                        String category,
                        String postTime,
                        String userEmail,
                        List<String> likedUsers,
                        String photoPath) {
        CollectionReference blogs = db.collection("Blog");
        Map<String, Object> blog = new HashMap<>();
        blog.put("blogTitle", blogTitle);
        blog.put("content", content);
        blog.put("petName", petName);
        blog.put("age", age);
        blog.put("gender", gender);  // 0: 雌性, 1: 雄性
        blog.put("category", category);
        blog.put("postTime", postTime);
        blog.put("userEmail", userEmail);
        blog.put("likedUsers", likedUsers);
        blog.put("photoPath", photoPath);

        blogs.add(blog).addOnSuccessListener(documentReference -> Log.d(TAG_FIRESTORE, "Blog added with ID: " + documentReference.getId()));
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
    public void uploadPetImage(String petName, Uri petImageUri, OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        StorageReference storageRef = storage.getReference();
        StorageReference petImageRef = storageRef.child("Pets/" + petName + "/image.jpg");

        UploadTask uploadTask = petImageRef.putFile(petImageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            petImageRef.getDownloadUrl().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
            Log.d(TAG_STORAGE, "Pet image uploaded for: " + petName);
        }).addOnFailureListener(failureListener);
    }

    // 上传博客图片
    public void uploadBlogImage(String blogTitle, Uri blogImageUri, OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        StorageReference storageRef = storage.getReference();
        StorageReference blogImageRef = storageRef.child("Blogs/" + blogTitle + "/image.jpg");

        UploadTask uploadTask = blogImageRef.putFile(blogImageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            blogImageRef.getDownloadUrl().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
            Log.d(TAG_STORAGE, "Blog image uploaded for: " + blogTitle);
        }).addOnFailureListener(failureListener);
    }
}
