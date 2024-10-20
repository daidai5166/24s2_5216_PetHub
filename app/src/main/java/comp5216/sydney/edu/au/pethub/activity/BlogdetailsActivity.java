package comp5216.sydney.edu.au.pethub.activity;

import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.loadImageFromFirebaseStorageToImageView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.model.Blog;

public class BlogdetailsActivity extends AppCompatActivity {
    TextView blogTitle;
    TextView blogDescription;
    TextView blogPostDate;
    ImageView arrowLeft;
    ImageView arrowRight;
    ImageView blogImageField;
    int currentImageIndex = 0;
    Blog selectedBlog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blogdetails);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        blogTitle = findViewById(R.id.post_title);
        blogDescription = findViewById(R.id.post_description);
        blogPostDate = findViewById(R.id.post_date);
        blogImageField = findViewById(R.id.pet_image);

        // left and right arrow< >
        arrowLeft = findViewById(R.id.arrow_left);
        arrowRight = findViewById(R.id.arrow_right);
        Intent intent = getIntent();
        selectedBlog=intent.getParcelableExtra("selectedBlog");
        blogTitle.setText(selectedBlog.getBlogTitle());
        blogDescription.setText(selectedBlog.getContent());
        blogPostDate.setText(selectedBlog.getPostTime());
        String BlogID = selectedBlog.getBlogID();

        updatePetImageUrl(BlogID, currentImageIndex);// 初始化


        // use left arrow to click the event
        arrowLeft.setOnClickListener(v -> {
            if (currentImageIndex > 0) {
                currentImageIndex--;
                updatePetImageUrl(BlogID, currentImageIndex);
            }
        });

        // right arrow to click the event
        arrowRight.setOnClickListener(v -> {
            currentImageIndex++;
            updatePetImageUrl(BlogID, currentImageIndex);
        });

    }
    private void updatePetImageUrl(String blogId, int index) {
        String BlogImageUrl = "Blogs/" + blogId + "/image_" + index + ".jpg";
        Log.i("BlogdetailsActivity", "updatePetImageUrl: " + BlogImageUrl);
        loadImageFromFirebaseStorageToImageView(BlogdetailsActivity.this, blogImageField, BlogImageUrl,
                (exception) -> {
                    Log.e("PetdetailsActivity", "Image load failed");
                    if(index > 0) {
                        currentImageIndex = 0;
                        updatePetImageUrl(blogId, currentImageIndex);
                    }
                    else{
                        if (selectedBlog.getCategory().equals("Other")) {
                            blogImageField.setImageResource(R.drawable.ic_others_category);
                        } else if (selectedBlog.getCategory().equals("Cat")) {
                            blogImageField.setImageResource(R.drawable.ic_cat_category);
                        } else if (selectedBlog.getCategory().equals("Bird")) {
                            blogImageField.setImageResource(R.drawable.ic_bird_category);
                        } else {
                            blogImageField.setImageResource(R.drawable.ic_dog_category);
                        }
                    }
                });
    }

    public void onBlogDetailBack(View view) {
        finish();
    }




}