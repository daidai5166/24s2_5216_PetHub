package comp5216.sydney.edu.au.pethub.adapters;

import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.loadImageFromFirebaseStorageToImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.model.Blog;
import comp5216.sydney.edu.au.pethub.model.Pet;

public class BlogAdapter extends BaseAdapter {
    private Context context;
    private String urlBlog;
    private List<Blog> blogList;

    // 构造函数，接收 Context 和数据
    public BlogAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        if (blogList != null) {
            this.blogList = blogList;
        } else {
            this.blogList = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return blogList.size(); // 返回博客的数量
    }

    @Override
    public Object getItem(int position) {
        return blogList.get(position); // 返回当前位置的博客标题
    }

    @Override
    public long getItemId(int position) {
        return position; // 返回当前位置的 ID
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 使用 ViewHolder 模式来优化性能
        ViewHolder holder;
        if (convertView == null) {
            // 如果 convertView 为空，说明需要初始化
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_blog, parent, false);

            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.blog_image);
            holder.titleView = convertView.findViewById(R.id.blog_title);
            holder.descriptionView = convertView.findViewById(R.id.blog_description);

            convertView.setTag(holder);
        } else {
            // 复用已有的 convertView
            holder = (ViewHolder) convertView.getTag();
        }

        // 获取当前宠物对象
        Blog blog = blogList.get(position);

        // 设置博客的图片、标题和描述
//        holder.imageView.findViewById(R.id.blog_image);
        holder.titleView.setText(blog.getBlogTitle());
        holder.descriptionView.setText(blog.getContent());

        urlBlog = "Blogs/" + blog.getBlogID() + "/image_0.jpg";

        loadImageFromFirebaseStorageToImageView(context, holder.imageView, urlBlog);

        return convertView;
    }

    // ViewHolder 模式，用于缓存视图
    private static class ViewHolder {
        ImageView imageView;
        TextView titleView;
        TextView descriptionView;
    }

    public void updateBlogs(List<Blog> newBlogs) {
        this.blogList.clear();
        this.blogList.addAll(newBlogs);
        notifyDataSetChanged();
    }
}
