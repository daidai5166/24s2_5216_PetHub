package comp5216.sydney.edu.au.pethub.adapters;


import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.noCacheLoadImageFromFirebaseStorageToImageView;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

// postpet页面上传按钮右边的recycleview的适配器

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Object> imageUris; // Supports Uri and String types
    private Context context;

    public ImageAdapter(Context context, List<Object> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Dynamically create FrameLayout as a container for image frames
        FrameLayout frameLayout = new FrameLayout(context);

        // Create ImageView to display images
        ImageView imageView = new ImageView(context);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                400,
                400
        );
        layoutParams.setMargins(20, 0, 20, 0);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        // Create delete button
        ImageButton btnDelete = new ImageButton(context);
        FrameLayout.LayoutParams btnLayoutParams = new FrameLayout.LayoutParams(
                40, 40
        );
        btnLayoutParams.gravity = Gravity.TOP | Gravity.END;  // Place the button in the upper right corner
        btnDelete.setLayoutParams(btnLayoutParams);
        btnDelete.setImageResource(android.R.drawable.ic_delete);  // Set delete icon
        btnDelete.setBackgroundColor(Color.TRANSPARENT);  // Set background transparency

        // Add ImageView and delete button to FrameLayout
        frameLayout.addView(imageView);
        frameLayout.addView(btnDelete);

        return new ImageViewHolder(frameLayout, imageView, btnDelete);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
//        Uri imageUri = imageUris.get(position);
//        holder.imageView.setImageURI(imageUri);  //Set Image
        Object imageObject = imageUris.get(position);

        // Determine whether the type is Uri or String
        if (imageObject instanceof Uri) {
            // If it is Uri type, load directly
            holder.imageView.setImageURI((Uri) imageObject);
        } else if (imageObject instanceof String) {
            // If it is a String type, call the function to load Firebase Storage images
            noCacheLoadImageFromFirebaseStorageToImageView(context, holder.imageView, (String) imageObject);
        }

        // Delete button click event
        holder.btnDelete.setOnClickListener(v -> {

            if (imageObject instanceof String) {
                // If it is a String type, it means it is an image in the cloud, delete the record in Firebase Storage
                String imagePath = (String) imageObject;

                // Get image references for Firebase Storage
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagePath);
                storageReference.delete().addOnSuccessListener(aVoid -> {
                    // After successfully deleting cloud images, delete local references
                    imageUris.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, imageUris.size());
                    Log.d("Firebase database", "Cloud image deleted successfully.");
                }).addOnFailureListener(e -> {
                    // Delete failed, output error message
                    Log.e("Firebase database", "Failed to delete cloud image.", e);
                });
            } else if (imageObject instanceof Uri) {
                // If it is of Uri type, it means it is a local image, and the local image in the list can be directly deleted
                imageUris.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, imageUris.size());
                Log.d("DeleteImage", "Local image removed.");
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageButton btnDelete;
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView, ImageView imageView, ImageButton btnDelete) {
            super(itemView);
            this.imageView = imageView;
            this.btnDelete = btnDelete;
        }
    }
}
