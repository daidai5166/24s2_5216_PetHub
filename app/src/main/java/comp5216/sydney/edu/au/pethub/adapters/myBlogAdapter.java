package comp5216.sydney.edu.au.pethub.adapters;

import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.loadImageFromFirebaseStorageToImageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.model.Blog;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;

public class myBlogAdapter extends RecyclerView.Adapter<myBlogAdapter.BlogViewHolder> {

    private List<Blog> blogList;
    private OnBlogClickListener blogClickListener;
    private Context context;

    public myBlogAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_blog_card, parent, false);
        return new BlogViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        Blog blog = blogList.get(position);
        String urlMyBlog = "Blogs/" + blog.getBlogID() + "/image_0.jpg";

        // 绑定需要显示的各个前端元素
        holder.tvBlogTitle.setText(blog.getBlogTitle());
        holder.tvBlogContent.setText(blog.getContent());
        holder.tvPostTime.setText("Posted on " + blog.getPostTime());

        // 从 Firebase 加载图片到 ImageView
        loadImageFromFirebaseStorageToImageView(context, holder.imageView, urlMyBlog,
                (e) -> {
                    holder.imageView.setImageResource(R.drawable.ic_dog);
                });


        // 点击删除按钮逻辑
        holder.btnDeleteBlog.setOnClickListener(v -> {
            ConnectDatabase connectDatabase;
            connectDatabase = new ConnectDatabase();
            String blogID = blog.getBlogID();
            connectDatabase.deleteBlog(blogID,
                    aVoid -> {
                        // 删除成功后执行的逻辑
                        blogList.remove(position);  // 从列表中移除该项
                        notifyItemRemoved(position);  // 通知适配器该项已移除
                        notifyItemRangeChanged(position, blogList.size());  // 更新剩余项的范围

                        // 显示成功提示
                        Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                    },
                    e -> {
                        // 删除失败后的逻辑
                        Log.e("myBlog_deleteError","Can't delete"+blogList);
                        Toast.makeText(context, "Failed to delete post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
            );
        });
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBlogTitle, tvBlogContent, tvPostTime;
        public ImageView imageView;
        public Button btnDeleteBlog;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.blog_view);  // 绑定ImageView
            tvBlogTitle = itemView.findViewById(R.id.tvBlogTitle);
            tvBlogContent = itemView.findViewById(R.id.tvBlogContent);
            tvPostTime = itemView.findViewById(R.id.tvPostTime);

            btnDeleteBlog = itemView.findViewById(R.id.btnDeleteBlog);
        }
    }

    public interface OnBlogClickListener {
        void onEditClick(Blog blog);
        void onDeleteClick(Blog blog);
    }
}

