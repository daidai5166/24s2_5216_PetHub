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
        return blogList.size(); // Return the number of blogs
    }

    @Override
    public Object getItem(int position) {
        return blogList.get(position); // Return the blog title of the current location
    }

    @Override
    public long getItemId(int position) {
        return position; // Return the ID of the current location
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Use ViewHolder mode to optimize performance
        ViewHolder holder;
        if (convertView == null) {
            // If convertView is empty, it means initialization is required
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_blog, parent, false);

            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.blog_image);
            holder.titleView = convertView.findViewById(R.id.blog_title);
            holder.descriptionView = convertView.findViewById(R.id.blog_description);

            convertView.setTag(holder);
        } else {
            // Reuse existing convertView
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the current pet object
        Blog blog = blogList.get(position);

        // Set blog images, titles, and descriptions
        //  holder.imageView.findViewById(R.id.blog_image);
        holder.titleView.setText(blog.getBlogTitle());
        holder.descriptionView.setText(blog.getContent());

        urlBlog = "Blogs/" + blog.getBlogID() + "/image_0.jpg";

        loadImageFromFirebaseStorageToImageView(context, holder.imageView, urlBlog);

        return convertView;
    }

    // ViewHolder mode, used for caching views
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
