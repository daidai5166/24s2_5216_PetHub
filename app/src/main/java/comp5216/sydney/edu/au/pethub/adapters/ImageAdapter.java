package comp5216.sydney.edu.au.pethub.adapters;

;

import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.loadImageFromFirebaseStorageToImageView;
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

    private List<Object> imageUris; // 支持 Uri 和 String 类型
    private Context context;

    public ImageAdapter(Context context, List<Object> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 动态创建 FrameLayout 作为图片框容器
        FrameLayout frameLayout = new FrameLayout(context);

        // 创建 ImageView 用于显示图片
        ImageView imageView = new ImageView(context);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                400,  // 宽度
                400//ViewGroup.LayoutParams.MATCH_PARENT  // 高度
        );
        layoutParams.setMargins(20, 0, 20, 0);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        // 创建删除按钮
        ImageButton btnDelete = new ImageButton(context);
        FrameLayout.LayoutParams btnLayoutParams = new FrameLayout.LayoutParams(
                40, 40
        );
        btnLayoutParams.gravity = Gravity.TOP | Gravity.END;  // 将按钮放置在右上角
        btnDelete.setLayoutParams(btnLayoutParams);
        btnDelete.setImageResource(android.R.drawable.ic_delete);  // 设置删除图标
        btnDelete.setBackgroundColor(Color.TRANSPARENT);  // 设置背景透明

        // 将 ImageView 和删除按钮添加到 FrameLayout 中
        frameLayout.addView(imageView);
        frameLayout.addView(btnDelete);

        return new ImageViewHolder(frameLayout, imageView, btnDelete);
//        // 动态创建 ImageView 而不是从 XML 加载
//        ImageView imageView = new ImageView(context);
//        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
//                400,  // 宽度
//                ViewGroup.LayoutParams.MATCH_PARENT  // 高度
//        );
//
//        // 设置外边距
//        layoutParams.setMargins(15, 0, 15, 0);  // 左、上、右、下的外边距
//        imageView.setLayoutParams(layoutParams);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);  // 设置缩放类型
//        return new ImageViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
//        Uri imageUri = imageUris.get(position);
//        holder.imageView.setImageURI(imageUri);  // 设置图片
        Object imageObject = imageUris.get(position);

        // 判断类型是 Uri 还是 String
        if (imageObject instanceof Uri) {
            // 如果是 Uri 类型，直接加载
            holder.imageView.setImageURI((Uri) imageObject);
        } else if (imageObject instanceof String) {
            // 如果是 String 类型，调用加载 Firebase Storage 图片的函数
            noCacheLoadImageFromFirebaseStorageToImageView(context, holder.imageView, (String) imageObject);
        }

        // 删除按钮点击事件
        holder.btnDelete.setOnClickListener(v -> {

            if (imageObject instanceof String) {
                // 如果是 String 类型，表示是云端的图片，删除 Firebase Storage 中的记录
                String imagePath = (String) imageObject;

                // 获取 Firebase Storage 的图片引用
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagePath);
                storageReference.delete().addOnSuccessListener(aVoid -> {
                    // 成功删除云端图片后，删除本地的引用
                    imageUris.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, imageUris.size());
                    Log.d("Firebase database", "Cloud image deleted successfully.");
                }).addOnFailureListener(e -> {
                    // 删除失败，输出错误信息
                    Log.e("Firebase database", "Failed to delete cloud image.", e);
                });
            } else if (imageObject instanceof Uri) {
                // 如果是 Uri 类型，表示是本地的图片，直接删除列表中的本地图片
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
