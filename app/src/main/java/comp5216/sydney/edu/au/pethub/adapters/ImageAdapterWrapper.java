package comp5216.sydney.edu.au.pethub.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapterWrapper extends BaseAdapter {
    private ImageAdapter imageAdapter;

    public ImageAdapterWrapper(Context context, List<Uri> imageUris) {
        // 使用现有的 ImageAdapter 逻辑
        imageAdapter = new ImageAdapter(context, imageUris);
    }

    @Override
    public int getCount() {
        return imageAdapter.getItemCount();
    }

    @Override
    public Object getItem(int position) {
        return null; // 不需要在此实现，因为我们直接显示图片
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 将 ViewHolder 转换为 ImageViewHolder
        ImageAdapter.ImageViewHolder viewHolder;
        if (convertView == null) {
            // 创建新的 ViewHolder
            viewHolder = (ImageAdapter.ImageViewHolder) imageAdapter.onCreateViewHolder(parent, imageAdapter.getItemViewType(position));
            convertView = viewHolder.itemView;
            convertView.setTag(viewHolder);
        } else {
            // 复用已有的 ViewHolder
            viewHolder = (ImageAdapter.ImageViewHolder) convertView.getTag();
        }

        // 绑定数据到 ViewHolder
        imageAdapter.onBindViewHolder(viewHolder, position);

        return convertView;
    }
}
