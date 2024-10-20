package comp5216.sydney.edu.au.pethub.adapters;

import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;



public class ImageAdapterWrapper extends BaseAdapter {
    private ImageAdapter imageAdapter;

    public ImageAdapterWrapper(Context context, List<Object> imageUris) {
        // Use existing ImageAdapter logic
        imageAdapter = new ImageAdapter(context, imageUris);
    }

    @Override
    public int getCount() {
        return imageAdapter.getItemCount();
    }

    @Override
    public Object getItem(int position) {
        return null; // No need to implement it here, as we directly display the image
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Convert ViewHolder to ImageViewHolder
        ImageAdapter.ImageViewHolder viewHolder;
        if (convertView == null) {
            // Create a new ViewHolder
            viewHolder = (ImageAdapter.ImageViewHolder) imageAdapter.onCreateViewHolder(parent, imageAdapter.getItemViewType(position));
            convertView = viewHolder.itemView;
            convertView.setTag(viewHolder);
        } else {
            // Reuse existing ViewHolders
            viewHolder = (ImageAdapter.ImageViewHolder) convertView.getTag();
        }

        // Bind data to ViewHolder
        imageAdapter.onBindViewHolder(viewHolder, position);

        return convertView;
    }
}
