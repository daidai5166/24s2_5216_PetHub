package comp5216.sydney.edu.au.pethub.adapters;

import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.loadImageFromFirebaseStorageToImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.model.Request;

public class MyMessageAdapter extends ArrayAdapter<Request> {
    public MyMessageAdapter(Context context, List<Request> objects) {
        super(context,0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Rewrite the getView method to display the item name, type, and due date
        Request currentItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.my_message_item, parent, false
            );
        }

        TextView nameTextView = convertView.findViewById(R.id.request_user_name);
        if (currentItem != null) {
            nameTextView.setText(currentItem.getUserName());
        }

        TextView typeTextView = convertView.findViewById(R.id.request_message);
        if (currentItem != null) {
            typeTextView.setText(currentItem.getMessage());
        }

        TextView dateTimeTextView = convertView.findViewById(R.id.request_date);
        if (currentItem != null) {
            dateTimeTextView.setText(currentItem.getDate());
        }

        ImageView userAvatar = convertView.findViewById(R.id.request_user_avatar);
        loadImageFromFirebaseStorageToImageView(getContext(), userAvatar, "Users/" + currentItem.getUserId() + "/avatar.jpg");

        return convertView;
    }
}
